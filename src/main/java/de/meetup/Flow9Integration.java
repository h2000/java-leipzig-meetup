package de.meetup;
// tag::flowIntImports[]
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.JavaFlowSupport;
import io.reactivex.Flowable;
import org.reactivestreams.FlowAdapters;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Publisher;
// end::flowIntImports[]
public class Flow9Integration {
    // tag::flowInt[]
    public static void main(String[] args) {
        Publisher<Long> reactorPublisher = reactorPublisher();
        Processor<Long, Long> akkaStreamsProcessor = akkaStreamsProcessor();

        reactorPublisher.subscribe(akkaStreamsProcessor);

        Flowable
            .fromPublisher(FlowAdapters.toProcessor(akkaStreamsProcessor))
            .subscribe(System.out::println);
    }

    private static Publisher<Long> reactorPublisher() {
        Flux<Long> numberFlux = Flux.interval(Duration.ofSeconds(1));
        return JdkFlowAdapter.publisherToFlowPublisher(numberFlux);
    }

    private static Processor<Long, Long> akkaStreamsProcessor() {
        Flow<Long, Long, akka.NotUsed> negatingFlow = Flow.of(Long.class).map(i -> -i);
        return JavaFlowSupport.Flow.toProcessor(negatingFlow).run(materializer);
    }

    private static ActorSystem actorSystem = ActorSystem.create();
    private static ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
    // end::flowInt[]
}