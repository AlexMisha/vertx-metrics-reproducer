package com.example.mikhail;

import io.vertx.core.VertxOptions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.vertx.core.Vertx.vertx;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.lineSeparator;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MINUTES;

public final class Test {
  public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
    final var vertx = vertx(new VertxOptions().setWorkerPoolSize(getRuntime().availableProcessors()));

    final var errors = new ConcurrentLinkedQueue<String>();

    final var executor = newSingleThreadExecutor();
    runAsync(new TagsCacheReader(vertx, errors), executor).get(1, MINUTES);

    executor.shutdown();
    vertx.close().toCompletionStage().toCompletableFuture().join();

    System.out.println(String.join(lineSeparator(), new ArrayList<>(errors)));
  }
}
