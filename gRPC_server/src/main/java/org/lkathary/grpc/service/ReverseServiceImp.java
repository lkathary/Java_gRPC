package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.ReverseProcessorGrpc;
import org.lkathary.grpc.ReverseService;

public class ReverseServiceImp extends ReverseProcessorGrpc.ReverseProcessorImplBase {

    @Override
    public StreamObserver<ReverseService.StringRequest> reverse(
            StreamObserver<ReverseService.StringResponse> responseObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " ==");
        System.out.println("== text requests: ==");

        return new StreamObserver<ReverseService.StringRequest>() {

            @Override
            public void onNext(ReverseService.StringRequest stringRequest) {
                System.out.print("string: " + stringRequest);
                ReverseService.StringResponse stringResponse = ReverseService.StringResponse.newBuilder()
                        .setResult(new StringBuilder(stringRequest.getWord()).reverse().toString())
                        .build();
                responseObserver.onNext(stringResponse);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                responseObserver.onError(e);
            }
        };
    }
}
