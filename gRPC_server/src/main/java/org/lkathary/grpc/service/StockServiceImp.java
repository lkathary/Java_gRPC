package org.lkathary.grpc.service;

import io.grpc.stub.StreamObserver;
import org.lkathary.grpc.StockQuoteProviderGrpc;
import org.lkathary.grpc.StockService;

import java.util.concurrent.ThreadLocalRandom;

public class StockServiceImp extends StockQuoteProviderGrpc.StockQuoteProviderImplBase {

    @Override
    public StreamObserver<StockService.Stock> clientSideStreamingGetStatisticsOfStocks(
            StreamObserver<StockService.StockQuote> responseObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " -> Client-Side Streaming requests: ==");

        return new StreamObserver<StockService.Stock>() {
            int count;
            double price = 0.0;
            StringBuffer sb = new StringBuffer();

            @Override
            public void onNext(StockService.Stock stock) {
                System.out.println(stock);
                count++;
                price = +fetchStockPriceBid(stock);
                sb.append(":").append(stock.getTickerSymbol());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StockService.StockQuote.newBuilder()
                        .setPrice(price / count)
                        .setDescription("Statistics by " + sb.toString())
                        .build());
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                responseObserver.onError(e);
            }
        };
    }

    @Override
    public StreamObserver<StockService.Stock> bidirectionalStreamingGetListsStockQuotes(
            StreamObserver<StockService.StockQuote> responseObserver) {

        System.out.println("== gRPC:" + this.getClass().getName() + " -> Bidirectional Streaming requests: ==");

        return new StreamObserver<StockService.Stock>() {
            @Override
            public void onNext(StockService.Stock request) {
                System.out.println(request);
                for (int i = 1; i <= 5; i++) {
                    StockService.StockQuote stockQuote = StockService.StockQuote.newBuilder()
                            .setPrice(fetchStockPriceBid(request))
                            .setOfferNumber(i)
                            .setDescription("Price for stock:" + request.getTickerSymbol())
                            .build();
                    responseObserver.onNext(stockQuote);
                }
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                responseObserver.onError(e);
            }
        };
    }

    private static double fetchStockPriceBid(StockService.Stock stock) {

        return stock.getTickerSymbol()
                .length()
                + ThreadLocalRandom.current()
                .nextDouble(-0.1d, 0.1d);
    }
}
