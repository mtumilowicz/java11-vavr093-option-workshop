import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Counter {
    AtomicInteger counter = new AtomicInteger();

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
