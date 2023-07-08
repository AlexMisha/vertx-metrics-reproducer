package com.example.mikhail;

import io.vertx.core.Vertx;
import io.vertx.micrometer.Label;
import io.vertx.micrometer.impl.Labels;
import io.vertx.micrometer.impl.meters.TagsCache;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.ThreadLocalRandom.current;

record TagsCacheReader(Vertx vertx, Queue<String> errors) implements Runnable {
  private static final AtomicBoolean done = new AtomicBoolean();

  @Override
  public void run() {
    final var labels = Label.values();

    while (true) {
      if (done.get()) {
        return;
      }

      vertx.executeBlocking(
          promise -> {
            final var label = labels[current().nextInt(0, labels.length)];
            final var value = String.valueOf(label.ordinal());
            final var expectedTags = Labels.toTags(new Label[]{label}, new String[]{value}).toString();
            final var actualTags = TagsCache
                .getOrCreate(null, new Label[]{label}, new String[]{value})
                .toString();

            if (!expectedTags.equals(actualTags)) {
              errors.offer("Expected: " + expectedTags + ", actual: " + actualTags);
              done.set(true);
            }
            promise.complete();
          },
          false
      );
    }
  }
}
