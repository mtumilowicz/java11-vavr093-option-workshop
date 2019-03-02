import io.vavr.Function1;
import io.vavr.PartialFunction;

/**
 * Created by mtumilowicz on 2019-03-03.
 */
public class Functions {
    PartialFunction<Integer, Integer> div() {
        return Function1.<Integer, Integer>of(i -> 5 / i)
                .partial(i -> i != 0);
    }

    PartialFunction<Integer, Integer> add() {
        return Function1.<Integer, Integer>of(i -> 5 + i)
                .partial(i -> true);
    }
}
