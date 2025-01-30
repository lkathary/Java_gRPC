package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.MathProcessorGrpc;
import org.lkathary.grpc.MathService;

public class MathServiceImp extends MathProcessorGrpc.MathProcessorImplBase {

    @Override
    public StreamObserver<MathService.Metric> collect(StreamObserver<MathService.Average> responseObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " ==");
        System.out.println("== requests: ==");

        return new StreamObserver<MathService.Metric>() {
            private long sum = 0;
            private long count = 0;

            @Override
            public void onNext(MathService.Metric value) {
                System.out.print("value: " + value);
                sum += value.getMetric();
                count++;
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(MathService.Average.newBuilder()
                        .setVal((double) sum / count)
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
