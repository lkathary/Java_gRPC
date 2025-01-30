package org.lkathary.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class Client {
    public static void main(String[] args) throws InterruptedException {

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget("localhost:8085")
                .usePlaintext()
                .build();

//      Task 1.
        System.out.println("==== Task 1: [request -> response] ====");

        GreetingProcessorGrpc.GreetingProcessorBlockingStub stub1 = GreetingProcessorGrpc
                .newBlockingStub(managedChannel);

        GreetingService.HelloRequest request = GreetingService.HelloRequest
                .newBuilder()
                .setCompany("Google")
                .addEmployee("Mike")
                .addEmployee("Mike")
                .addEmployee("Alex")
                .addEmployee("Andrey")
                .build();

        try {
            GreetingService.HelloResponse response = stub1.greeting(request);
            System.out.println(response);

            System.out.println(response.getGreeting());
            System.out.println("Number of employees: " + response.getNumber());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("The server is probably shutdown");
            System.exit(1);
        }

//      Task 2.
        System.out.println("==== Task 2: [request -> (stream)response] ====");

        System.out.println("Input number(0..20): ");
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();

        SquareService.MathRequest request2 = SquareService.MathRequest
                .newBuilder()
                .setNumber(num)
                .build();

        SquareProcessorGrpc.SquareProcessorBlockingStub stab2 = SquareProcessorGrpc
                .newBlockingStub(managedChannel);
        Iterator<SquareService.MathResponse> response2 = stab2.square(request2);
        while (response2.hasNext()) {
            SquareService.MathResponse tmp = response2.next();
            System.out.println(tmp.getText() + tmp.getNumber());
        }

//      Task 3.
        System.out.println("==== Task 3: [(stream)request -> response] ====");

        StreamObserver<TextService.TextResponse> responseStream = new StreamObserver<TextService.TextResponse>() {
            @Override
            public void onNext(TextService.TextResponse textResponse) {
                System.out.println("onNext");
                System.out.println(textResponse);
            }

            @Override
            public void onCompleted() {
                System.out.println("Finish: Task 3");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }
        };

        List<String> words = Arrays.asList("aaa", "bbb", "aba", "baa", "aba");
        TextProcessorGrpc.TextProcessorStub nonBlockingStub = TextProcessorGrpc
                .newStub(managedChannel);
        StreamObserver<TextService.TextRequest> requestStream = nonBlockingStub.popular(responseStream);

        try {
            for (String word : words) {
                requestStream.onNext(TextService.TextRequest
                        .newBuilder()
                        .setWord(word)
                        .build());
            }
        } catch (RuntimeException e) {
            requestStream.onError(e);
            throw e;
        }
        requestStream.onCompleted();

//      Task 4.
        System.out.println("==== Task 4: [(stream)request -> response] ====");

        MathProcessorGrpc.MathProcessorStub nonBlockingStub2 = MathProcessorGrpc.newStub(managedChannel);

        StreamObserver<MathService.Metric> collect = nonBlockingStub2
                .collect(new StreamObserver<MathService.Average>() {

            @Override
            public void onNext(MathService.Average value) {
                System.out.println("Average: " + value.getVal());
            }

            @Override
            public void onCompleted() {
                System.out.println("Finish: Task 4");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }
        });

        Stream.of(1L, 2L, 5L, 7L).map(l -> MathService.Metric.newBuilder().setMetric(l).build())
                .forEach(collect::onNext);
        collect.onCompleted();


//      Task 5.
        System.out.println("==== Task 5: [(stream)request -> (stream)response] ====");

        StreamObserver<ReverseService.StringResponse> responseObserver =
                new StreamObserver<ReverseService.StringResponse>() {

            @Override
            public void onNext(ReverseService.StringResponse stringResponse) {
                System.out.println("== onNext ==");
                System.out.println(stringResponse);
            }

            @Override
            public void onCompleted() {
                System.out.println("Finish: Task 5");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }
        };

        List<String> list = Arrays.asList("one", "two", "three", "four", "five");
        ReverseProcessorGrpc.ReverseProcessorStub nonBlockingStub3 = ReverseProcessorGrpc
                .newStub(managedChannel);
        StreamObserver<ReverseService.StringRequest> requestObserver = nonBlockingStub3.reverse(responseObserver);

        try {
            for (String it : list) {
                System.out.println("request: " + it);
                Thread.sleep(500);
                requestObserver.onNext(ReverseService.StringRequest
                        .newBuilder()
                        .setWord(it)
                        .build());
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();


//      !!! Don't allow to shut down channel while streaming... !!!!
        managedChannel.shutdown().awaitTermination(2000, TimeUnit.MILLISECONDS);
    }
}
