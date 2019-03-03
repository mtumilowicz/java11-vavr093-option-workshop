import io.vavr.control.Option;

import java.util.Objects;

/**
 * Created by mtumilowicz on 2018-11-26.
 */
class Repository {
    static Option<String> findById(int id) {
        return Option.when(id == 1, "found-by-id");
    }

    static Option<String> findByName(String name) {
        return Option.when(Objects.equals(name, "Michal"), "found-by-name");
    }
}
