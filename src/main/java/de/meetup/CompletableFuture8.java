package de.meetup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CompletableFuture8 {
  // tag::future80[]
  public static CompletableFuture<Integer> calculate() {
    CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
      System.out.print("inside.");
      Thread.sleep(500);
      completableFuture.complete(42);
      return null;
    });

    return completableFuture;
  }
  // end::future80[]
 // tag::future81[]
  public static void main(String[] args) throws InterruptedException, ExecutionException {
   
    System.out.println("-- 1. callback");
    final CompletableFuture<Integer> calculate = calculate();
    Thread.sleep(10);
    System.out.print("before.");

    calculate.thenAccept(nr -> System.out.print("thenAccept:" + nr + "."))
        .join(); // .join() -> wait of result (used only for demo)
    System.out.print("after.\n");
    // Output:
    // -- 1. callback
    // inside.before.thenAccept:42.after.
    // end::future81[]
    // tag::future82[]
    System.out.println("-- 2. transform/map future result");
    CompletableFuture
      .supplyAsync(() -> "abc")
      .thenApply(String::toUpperCase)
      .thenAccept(r -> System.out.println("result thenApply: " + r))
      .join();
    // Output:
    // -- 2. transform/map future result
    // result thenApply: ABC
    // end::future82[]

    // tag::future83[]
    // -- 3. combine futures
    System.out.println("-- 3.1. compose ~ flatMap");
    CompletableFuture
      .supplyAsync(() -> "one")
      .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " two"))
      .thenAccept(r -> System.out.println("result thenCompose: " + r))
      .join();
    // output:
    // -- 3.1. compose ~ flatMap
    // result thenCompose: one two
    // end::future83[]
    // tag::future84[]
    System.out.println("-- 3.2. wait for the result of two parallel futures");
    CompletableFuture
      .supplyAsync(() -> 7)
      .thenCombine(CompletableFuture.supplyAsync(() -> 6),
        (a, b) -> a * b
      )
      .thenAccept(r -> System.out.println("result thenCombine: " + r))
      .join();
    // output:
    // -- 3.2. wait for the result of two parallel futures
    // result thenCombine: 42
    // end::future84[]
    // tag::future85[]
    System.out.println("-- 3.3. wait for multiple results");
    CompletableFuture<String> future1
      = CompletableFuture.supplyAsync(() -> "A.");
    CompletableFuture<String> future2
      = CompletableFuture.supplyAsync(() -> "B.");
    CompletableFuture<String> future3
      = CompletableFuture.supplyAsync(() -> "C.");

    CompletableFuture<Void> allFutures
      = CompletableFuture.allOf(future1, future2, future3);
    allFutures.join();
    System.out.println("allOf: " + future1.get() + "" + future2.get() + "" + future3.get());
    // output:
    // -- 3.3. wait for multiple results
    // allOf: A.B.C.
    // end::future85[]
    // tag::future86[]
    System.out.println("-- 4. exceptionally, handle");
    CompletableFuture
      .supplyAsync(() -> {
        throw new NullPointerException();
      })
      .exceptionally(ex -> 2L)
      .thenAccept(c -> System.out.println("exceptionally: " + c))
      .join()
      ;
    // output:
    // -- 4. exceptionally, handle
    // exceptionally: 2
    // end::future86[]
    System.exit(0);
  }

}
