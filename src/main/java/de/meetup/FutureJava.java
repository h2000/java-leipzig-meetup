package de.meetup;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureJava {

  // tag::future1[]
  public static class Calculator {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<Integer> calc(int number) {
      return executor.submit(() -> { // Callable<Integer>
        System.out.println("inside future, got: " + number);
        Thread.sleep(500);
        return number * number;
      });
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {

    final Calculator calculator = new Calculator();
    // -- 1. blocking
    Future<Integer> future1 = calculator.calc(1);
    Thread.sleep(10);
    System.out.println("before get");
    int result1 = future1.get();
    System.out.println("result1: " + result1 + "\n");

    // output:
    // inside future
    // before get
    // result: 4
    // end::future1[]

    // tag::future2[]
    // -- 2. active waiting
    Future<Integer> future2 = calculator.calc(2);
    while (!future2.isDone()) {
      System.out.println("Calculating 2...");
      Thread.sleep(200);
    }
    int result2 = future2.get();
    System.out.println("result2: " + result2 + "\n");
    // Calculating 2...
    // inside future, got: 2
    // Calculating 2...
    // Calculating 2...
    // result2: 4
    // end::future2[]

    // tag::future3[]
    // -- 3. waiting for two results
    Future<Integer> future3 = calculator.calc(3);
    Future<Integer> future4 = calculator.calc(4);
    while (!(future3.isDone() && future4.isDone())) {
      System.out.println("Calculating 3...");
      Thread.sleep(400);
    }
    Integer result3 = future3.get();
    Integer result4 = future4.get();
    System.out.println("result3: " + (result3 + result4) + "\n");
    // Calculating 3...
    // inside future, got: 3
    // Calculating 3...
    // Calculating 3...
    // inside future, got: 4
    // Calculating 3...
    // Calculating 3...
    // result3: 25
    // end::future3[]
    System.exit(0);
  }
}
