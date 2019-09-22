package de.meetup;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.IntStream;

public class Flow9 {

  // # RX
  //
  // T -> Future<T>
  // Iterable<T> -> Flow.Publisher<T>
  //
  // # Pattern:
  // onNext(t) 0..N [onError(Throwable) | onComplete()]

  //  # From Imperative to Reactive Programming (from https://projectreactor.io)
  //
  //  Reactive libraries such as Reactor aim to address these drawbacks
  //  of "classic" asynchronous approaches on the JVM while also
  //  focusing on a few additional aspects:
  //
  //  - Composability and readability
  //  - Data as a flow manipulated with a rich vocabulary of operators
  //  - Nothing happens until you subscribe
  //  - Backpressure or the ability for the consumer to signal the producer
  //    that the rate of emission is too high
  //  - High level but high value abstraction that is concurrency-agnostic
  //
  // “Reactive Streams”-Standard defines simple interfaces and rules (incl. test kit TCK)
  // => which are now in java9 as Foundation

  public static class SimplePublisher implements Flow.Publisher<Integer> {

    private final IntStream stream;
    private final boolean throwException;

    public SimplePublisher(int max, boolean throwException) {
      this.stream = IntStream.range(0, max);
      this.throwException = throwException;
    }

    @Override
    public void subscribe(final Subscriber<? super Integer> subscriber) {
      subscriber.onSubscribe(new Subscription() {
        @Override
        public void request(final long n) { /* TODO */}

        @Override
        public void cancel() { /* TODO */}
      });
      for (int item : stream.toArray()) {
        if (throwException && item == 3) {
          subscriber.onError(new RuntimeException("boom"));
          return;
        }
        subscriber.onNext(item);
      }
      subscriber.onComplete();
    }
  }

  public static class MySubscriber implements Subscriber<Integer> {

    private Subscription subscription;

    @Override
    public void onSubscribe(final Subscription subscription) {
      this.subscription = subscription;
      System.out.print(".onSubscribe()");
    }

    @Override
    public void onNext(final Integer item) {
      System.out.print(".onNext(" + item + ")");
      subscription.request(1);
    }

    @Override
    public void onError(final Throwable throwable) {
      System.out.print(".onError(" + throwable + ")");
    }

    @Override
    public void onComplete() {
      System.out.print(".onComplete()");
    }
  }

  public static void main(String[] args) {
    System.out.println("happy path");
    new SimplePublisher(4, false)
      .subscribe(new MySubscriber());
    // output: happy path
    //.onSubscribe().onNext(0).onNext(1).onNext(2).onNext(3).onComplete()

    System.out.println("\nexceptional path");
    new SimplePublisher(4, true)
      .subscribe(new MySubscriber());
    // output: exceptional path
    // .onSubscribe().onNext(0).onNext(1).onNext(2).onError(java.lang.RuntimeException: boom)
  }


}
