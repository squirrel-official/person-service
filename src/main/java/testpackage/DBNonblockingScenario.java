package testpackage;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DBNonblockingScenario {

   public void reactiveDBConnection(){
       Mono blockingWrapper= Mono.fromCallable(()-> Mono.empty());
       blockingWrapper = blockingWrapper.subscribeOn(Schedulers.boundedElastic());
   }
}
