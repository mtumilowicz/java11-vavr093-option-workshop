import io.vavr.control.Option;

/**
 * Created by mtumilowicz on 2019-03-13.
 */
public class RepositoryAnswer extends Repository {
    static Option<String> findById(int id) {
        return CacheRepository.findById(id).orElse(() -> DatabaseRepository.findById(id));
    }
}
