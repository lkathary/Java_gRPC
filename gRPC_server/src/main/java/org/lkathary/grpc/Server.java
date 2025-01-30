package org.lkathary.grpc;

import io.grpc.ServerBuilder;
import org.lkathary.grpc.service.*;

import java.io.IOException;

public class Server {
    public static void main(String[] args) {

        io.grpc.Server server = ServerBuilder
                .forPort(8085)
                .addService(new GreetingServiceImp())
                .addService(new SquareServiceImp())
                .addService(new TextServiceImp())
                .addService(new MathServiceImp())
                .addService(new ReverseServiceImp())
                .addService(new StockServiceImp())
                .build();

        try {
            server.start();
            System.out.println("Server started...");
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            server.shutdown();
            System.out.println("Server shutdown");
        }
    }
}