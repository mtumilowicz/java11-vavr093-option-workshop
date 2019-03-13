import io.vavr.Function1;
import io.vavr.PartialFunction;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
class Functions {
    static PartialFunction<Integer, Integer> div() {
        return Function1.<Integer, Integer>of(i -> 5 / i)
                .partial(i -> i != 0);
    }

    static PartialFunction<Integer, Integer> add() {
        return Function1.<Integer, Integer>of(i -> 5 + i)
                .partial(i -> true);
    }
}
