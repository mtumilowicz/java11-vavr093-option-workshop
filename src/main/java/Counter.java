import io.vavr.control.Option;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
public class Counter {
    private final AtomicInteger counter = new AtomicInteger();
    
    void increment() {
        counter.incrementAndGet();
    }
    
    void increment(int value) {
        counter.addAndGet(value);
    }
    
    int get() {
        return counter.get();
    }
}
