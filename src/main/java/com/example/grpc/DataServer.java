package com.example.grpc;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;


// mvn exec:java -Dexec.mainClass=com.example.grpc.DataServer
public class DataServer {
  public static void main(String[] args) throws InterruptedException, IOException {
    Server server = ServerBuilder.forPort(0).addService(new DataTransferServiceImpl()).build();

    server.start();
    System.out.println("Server started. Listening on port: " + server.getPort());

    server.awaitTermination();
  }
}
