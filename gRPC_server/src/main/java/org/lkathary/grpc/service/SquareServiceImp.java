package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.SquareProcessorGrpc;
import org.lkathary.grpc.SquareService;

public class SquareServiceImp extends SquareProcessorGrpc.SquareProcessorImplBase {

    @Override
    public void square(SquareService.MathRequest request,
                       StreamObserver<SquareService.MathResponse> responseStreamObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " ==");
        System.out.println("== math request: ==");
        System.out.println(request);

        for (int i = 1; i <= request.getNumber(); ++i) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SquareService.MathResponse response = SquareService.MathResponse
                    .newBuilder()
                    .setText(i + " * " + i + " = ")
                    .setNumber((long) i * i)
                    .build();

            responseStreamObserver.onNext(response);
        }
        responseStreamObserver.onCompleted();
    }
}
