import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by mtumilowicz on 2019-03-02.
 */
class Statistics {
    
    /*
    implement method stats() that will gather stats:
        stats1, stats2, stats3 and return them as a option(list(stats1, stats2, stats3)
        if any of stats returns empty then stats() should also return empty 
     */
    Option<Seq<Number>> stats() {
        return Option.none();
    }

    /*
    implement method statsAll() that will gather stats:
        stats1, stats2, stats3, stats4 and return them as a option(list(stats1, stats2, stats3, stats4)
        if any of stats returns empty then statsAll() should also return empty 
     */
    Option<Seq<Number>> statsAll() {
        return Option.some(List.of(1));
    }
    
    Option<BigDecimal> stats1() {
        return Option.of(BigDecimal.TEN);
    }    
    
    Option<BigInteger> stats2() {
        return Option.of(BigInteger.TWO);
    }    
    
    Option<Integer> stats3() {
        return Option.of(1);
    }
    
    Option<Long> stats4() {
        return Option.none();
    }
}
