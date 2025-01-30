package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.TextProcessorGrpc;
import org.lkathary.grpc.TextService;

public class TextServiceImp extends TextProcessorGrpc.TextProcessorImplBase {

    @Override
    public StreamObserver<TextService.TextRequest> popular(StreamObserver<TextService.TextResponse> responseObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " ==");
        System.out.println("== text requests: ==");

        return new StreamObserver<TextService.TextRequest>() {
            final StringBuilder sb = new StringBuilder();

            @Override
            public void onNext(TextService.TextRequest textRequest) {
                System.out.println("word: " + textRequest.getWord());
                sb.append(textRequest.getWord()).append(":");
            }
            @Override
            public void onCompleted() {
                responseObserver.onNext(TextService.TextResponse.newBuilder()
                        .setResult(sb.toString())
                        .build());
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
