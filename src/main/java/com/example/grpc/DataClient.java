package com.example.grpc;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.example.grpc.dataTransfer.DataChunk;
import com.example.grpc.dataTransfer.DataTransferServiceGrpc;
import com.example.grpc.dataTransfer.Reply;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// mvn exec:java -Dexec.mainClass=com.example.grpc.DataClient -Dexec.args="hostaddr port packetsize datasize"
public class DataClient {
  public static void main(String[] args) throws InterruptedException, IOException {
    String hostAddr = args[0];
    int port = Integer.parseInt(args[1]);
    int packetsize = Integer.parseInt(args[2]);
    final int size = Integer.parseInt(args[3]);

    final ManagedChannel channel = ManagedChannelBuilder.forAddress(hostAddr, port).usePlaintext().build();
    DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferService = DataTransferServiceGrpc.newBlockingStub(channel);

    int numPackets = 0;
    byte[] data;
    int totalByteSent = 0;
    long startTime, endTime;

    int current = 0;
    try {
      startTime = System.nanoTime();
      System.out.println("Start time: " + startTime);
      while (current != size) {
        numPackets++;
        if (size - current >= packetsize)
          current += packetsize;
        else {
          packetsize = (int) (size - current);
          current = size;
        }

        data = new byte[packetsize];
        totalByteSent += packetsize;
        DataChunk dataChunk = DataChunk.newBuilder().setData(ByteString.copyFrom(data, 0, packetsize)).build();

        long packetStart = System.nanoTime();
        Reply reply = dataTransferService.send(dataChunk);
        long packetEnd = System.nanoTime();
        long duration = packetEnd - packetStart;

        System.out.print("*");
        System.out.println(duration);
      }
    } catch (Exception e) {
      e.printStackTrace();
      channel.shutdown();
      return;
    }

    System.out.println("\n"+ numPackets + " packets sent. " + totalByteSent + " bytes of total data sent.");

    endTime = System.nanoTime();
    System.out.println("\nEnd time: " + endTime);
    long duration = (endTime - startTime);
    double ms = duration / 1000000.0;
    System.out.println("Duration: " + ms + " ms");

    double throughput = (double) current * (1000000000L) / duration;
    System.out.println("Throughput: " + getFormatSize(throughput) + "/s");
    channel.shutdown();

  }

  private static DecimalFormat df = null;

  static {
    // set format
    df = new DecimalFormat("#0.0");
    df.setRoundingMode(RoundingMode.HALF_UP);
    df.setMaximumFractionDigits(1);
    df.setMinimumFractionDigits(1);
  }

  // format
  static private String getFormatSize(double length) {
    double size = length / (1 << 30);
    if (size >= 1) {
      return df.format(size) + "GB";
    }
    size = length / (1 << 20);
    if (size >= 1) {
      return df.format(size) + "MB";
    }
    size = length / (1 << 10);
    if (size >= 1) {
      return df.format(size) + "KB";
    }
    return df.format(length) + "B";
  }
}
