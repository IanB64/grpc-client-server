package com.example.grpc;

import com.example.grpc.dataTransfer.DataChunk;
import com.example.grpc.dataTransfer.DataTransferServiceGrpc;
import com.example.grpc.dataTransfer.Reply;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/**
 * DataTransferServiceImpl
 */
public class DataTransferServiceImpl extends DataTransferServiceGrpc.DataTransferServiceImplBase {
  @Override
  public void send(DataChunk request, StreamObserver<Reply> responseObserver) {
    byte[] response = new byte[1];
    Reply reply = Reply.newBuilder().setReplyBytes(ByteString.copyFrom(response)).build();
    responseObserver.onNext(reply);

    responseObserver.onCompleted();
  }
}
