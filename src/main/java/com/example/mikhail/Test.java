package com.example.mikhail;

import io.vertx.core.VertxOptions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static io.vertx.core.Vertx.vertx;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.lineSeparator;

public final class Test {
  public static void main(String[] args) {
    final var vertx = vertx(new VertxOptions().setWorkerPoolSize(getRuntime().availableProcessors()));
    final var errors = new ConcurrentLinkedQueue<String>();

    new TagsCacheReader(vertx, errors).run();

    vertx.close().toCompletionStage().toCompletableFuture().join();
    System.out.println(String.join(lineSeparator(), new ArrayList<>(errors)));
  }
}
