package de.meetup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFuture9 {

  public static void main(String[] args) {
    // tag::future9[]
    // -- only small changes from java 8 to java 9 (excerpt)

    // use default executor if not other provided (e.g. for subclass)
    new CompletableFuture<>().defaultExecutor();

    // completeAsync
    ExecutorService executor = Executors.newSingleThreadExecutor();
    new CompletableFuture<>().completeAsync(() -> 42L);
    new CompletableFuture<>().completeAsync(() -> 42L, executor);

    // delay the completion by 1 second
    Executor delayedExecutor = CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS);
    new CompletableFuture<>()
      .completeAsync(() -> "input", delayedExecutor);

    // orTimeout, exceptional if timeout reached
    new CompletableFuture<>().orTimeout(1, TimeUnit.SECONDS);

    // completeOnTimeout with input
    new CompletableFuture<>().completeOnTimeout("input", 1, TimeUnit.SECONDS);

    // ...
    // end::future9[]
  }
}
