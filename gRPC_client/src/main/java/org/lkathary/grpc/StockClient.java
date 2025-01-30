package org.lkathary.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StockClient {
    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8085)
//                .forTarget("localhost:8085")
                .usePlaintext()
                .build();

        StockQuoteProviderGrpc.StockQuoteProviderBlockingStub blockingStub = StockQuoteProviderGrpc
                .newBlockingStub(channel);
        StockQuoteProviderGrpc.StockQuoteProviderStub nonBlockingStub = StockQuoteProviderGrpc
                .newStub(channel);

        List<StockService.Stock> stocks = new ArrayList<>();
        stocks.add(StockService.Stock.newBuilder()
                .setTickerSymbol("A1")
                .setCompanyName("A2")
                .setDescription("A3")
                .build());
        stocks.add(StockService.Stock.newBuilder()
                .setTickerSymbol("B1")
                .setCompanyName("B2")
                .setDescription("B3")
                .build());

//      Client RPC with Client-Side Streaming
        StreamObserver<StockService.StockQuote> responseObserver = new StreamObserver<StockService.StockQuote>() {
            @Override
            public void onNext(StockService.StockQuote summary) {
                System.out.printf("Response: got stock statistics -> Average Price: {%1$s}, description: {%2$s}\n",
                        summary.getPrice(), summary.getDescription());
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished clientSideStreamingGetStatisticsOfStocks");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }
        };

        StreamObserver<StockService.Stock> requestObserver = nonBlockingStub
                .clientSideStreamingGetStatisticsOfStocks(responseObserver);

        try {
            for (StockService.Stock stock : stocks) {
                System.out.printf("Client-Side Streaming request: {%1$s}, {%2$s}, {%3$s}\n",
                        stock.getTickerSymbol(), stock.getCompanyName(), stock.getDescription());
                requestObserver.onNext(stock);
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();

//      Client RPC with Bidirectional Streaming
        StreamObserver<StockService.StockQuote> responseObserver2 = new StreamObserver<StockService.StockQuote>() {
            @Override
            public void onNext(StockService.StockQuote stockQuote) {
                System.out.println("== onNext ==");
                System.out.println(stockQuote);
                //logInfo("RESPONSE price#{0} : {1}, description:{2}", stockQuote.getOfferNumber(), stockQuote.getPrice(), stockQuote.getDescription());
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished bidirectionalStreamingGetListsStockQuotes");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }
        };

        StreamObserver<StockService.Stock> requestObserver2 = nonBlockingStub
                .bidirectionalStreamingGetListsStockQuotes(responseObserver2);
        try {
            for (StockService.Stock stock : stocks) {
                System.out.printf("Bidirectional Streaming request: {%1$s}, {%2$s}\n",
                        stock.getTickerSymbol(), stock.getCompanyName());
                requestObserver2.onNext(stock);
                Thread.sleep(200);
            }
        } catch (RuntimeException e) {
            requestObserver2.onError(e);
            throw e;
        }
        requestObserver2.onCompleted();

//      !!! Don't allow to shut down channel while streaming... !!!!
        channel.shutdown().awaitTermination(2000, TimeUnit.MILLISECONDS);
    }
}
