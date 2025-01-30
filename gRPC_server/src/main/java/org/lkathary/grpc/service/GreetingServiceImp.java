package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.GreetingProcessorGrpc;
import org.lkathary.grpc.GreetingService;

public class GreetingServiceImp extends GreetingProcessorGrpc.GreetingProcessorImplBase {

    @Override
    public void greeting(GreetingService.HelloRequest request,
                         StreamObserver<GreetingService.HelloResponse> responseStreamObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " ==");
        System.out.println("== request: ==");
        System.out.println(request);

        GreetingService.HelloResponse response = GreetingService.HelloResponse
                .newBuilder()
                .setGreeting("Hi, " + request.getCompany())
                .setNumber(request.getEmployeeCount())
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }
}
