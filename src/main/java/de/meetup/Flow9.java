package de.meetup;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class Flow9 {
// tag::publisher[]
  public static class MyPublisher 
      extends SubmissionPublisher<Long> implements AutoCloseable {
    final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    final ScheduledFuture<?> periodicTask;
    int counter = 0; long period = 500; TimeUnit unit = TimeUnit.MILLISECONDS;
    int maxCount; Optional<Integer> exceptionOn;

    MyPublisher(Executor executor, int maxBufferCapacity,
                      int maxCount, Optional<Integer> exceptionOn) {
      super(executor, maxBufferCapacity);
      this.maxCount = maxCount; this.exceptionOn = exceptionOn;
      periodicTask = scheduler.scheduleAtFixedRate(this::emit, 0, period, unit);
    }
    public void emit() {
      try {
        if(exceptionOn.map( n -> n == counter).orElse(false)) {
          throw new RuntimeException("boom"); 
        } 
        if(counter < maxCount) { 
          submit(System.currentTimeMillis());
          counter++;
        } else { close(); } 
      } catch (Exception e) { closeExceptionally(e); }
    }
    public void close() {
      periodicTask.cancel(false); 
       scheduler.shutdown();
      super.close();
    }
  }
// end::publisher[]
// tag::subscriber[]
  public static class MySubscriber implements Subscriber<Long> {
    private Subscription subscription;
    private CountDownLatch latch;
    private int counter = 4;

    public MySubscriber(CountDownLatch latch) { this.latch = latch; }

    @Override public void onSubscribe(final Subscription subscription) {
      System.out.print(".onSubscribe()");
      this.subscription = subscription;
      this.subscription.request(1);
    }

    @Override public void onNext(final Long item) {
      System.out.print(".onNext(" + item + ")");
      if(counter > 0) { subscription.request(1); counter--; } 
      else { subscription.cancel();  latch.countDown(); }
    }
    
    @Override public void onError(final Throwable throwable) {
      System.out.print(".onError(" + throwable + ")");
      latch.countDown();
    }
    
    @Override public void onComplete() {
      System.out.print(".onComplete()");
      latch.countDown();
    }
  }
  // end::subscriber[]

  public static void main(String[] args) {
    // tag::flow1[]
    try (MyPublisher  publisher = 
      new MyPublisher(ForkJoinPool.commonPool(), 10, // maxBufferCapacity
        3, Optional.empty() // producer stops after 3 rounds, no exception
        )){

      System.out.println("\nnormal run:");
      CountDownLatch latch = new CountDownLatch(1);
      publisher.subscribe(new MySubscriber(latch));
      // normal run:
      // .onSubscribe().onNext(1569232282069).onNext(1569232283068)
      // .onNext(1569232284067).onComplete()
      
      latch.await();
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    }
    // end::flow1[]
    // tag::flow2[]
    try (MyPublisher  publisher = 
      new MyPublisher(ForkJoinPool.commonPool(), 10, // maxBufferCapacity
        5, Optional.empty() // producer stops after 5 rounds, no exception
        )){

      System.out.println("\nsubscriber canceled:");
      CountDownLatch latch = new CountDownLatch(1);
      publisher.subscribe(new MySubscriber(latch));
      // subscriber canceled:
      // .onSubscribe().onNext(1569236384984).onNext(1569236385488)
      // .onNext(1569236385989).onNext(1569236386484).onNext(1569236386988)
      
      latch.await();
    } catch (Exception e) { System.err.println(e); e.printStackTrace(); }
    // end::flow2[]
    // tag::flow3[]
    try (MyPublisher  publisher = 
    new MyPublisher(ForkJoinPool.commonPool(), 10, // maxBufferCapacity
      5, Optional.of(2) // exception in producer
      )){

    System.out.println("\nproducer explodes:");
    CountDownLatch latch = new CountDownLatch(1);
    publisher.subscribe(new MySubscriber(latch));
    // producer explodes:
    // .onSubscribe().onNext(1569236722503).onNext(1569236723007)
    // .onError(java.lang.RuntimeException: boom)
    
    latch.await();
  } catch (Exception e) { System.err.println(e); e.printStackTrace(); }
  // end::flow3[]
  System.exit(0);
  }


}
