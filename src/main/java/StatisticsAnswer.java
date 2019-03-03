import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

/**
 * Created by mtumilowicz on 2019-03-02.
 */
class StatisticsAnswer extends Statistics {
    
    Option<Seq<Number>> stats() {
        return Option.sequence(List.of(stats1(), stats2(), stats3()));
    }

    Option<Seq<Number>> statsAll() {
        return Option.sequence(List.of(stats1(), stats2(), stats3(), stats4()));
    }
}
