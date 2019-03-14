import io.vavr.collection.HashSet
import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification

import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

import static java.util.Objects.nonNull

/**
 * Created by mtumilowicz on 2019-03-02.
 */
class Answers extends Specification {

    def "create empty option"() {
        given:
        Option none = Option.none()

        expect:
        none.isEmpty()
    }

    def "create not empty option with not null value"() {
        given:
        Option<Integer> some = Option.some(5)

        expect:
        some.isDefined()
        some.get()
    }

    def "create not empty option with null value"() {
        given:
        Option<Integer> some = Option.some()

        expect:
        some.isDefined()
        !some.get()
    }

    def "check if option is empty / not empty"() {
        given:
        def empty = Option.none()
        def notEmpty = Option.some()

        expect:
        empty.isEmpty()
        notEmpty.isDefined()
    }

    def "conversion: optional -> option"() {
        given:
        Optional<Integer> emptyOptional = Optional.empty()
        Optional<Integer> notEmptyOptional = Optional.of(1)

        when:
        Option<Integer> emptyOption = Option.ofOptional(emptyOptional)
        Option<Integer> notEmptyOption = Option.ofOptional(notEmptyOptional)

        then:
        emptyOption == Option.none()
        notEmptyOption == Option.some(1)
    }

    def "conversion: option -> optional"() {
        given:
        Option<Integer> emptyOption = Option.none()
        Option<Integer> notEmptyOption = Option.some(1)

        when:
        Optional<Integer> emptyOptional = emptyOption.toJavaOptional()
        Optional<Integer> notEmptyOptional = notEmptyOption.toJavaOptional()

        then:
        emptyOptional == Optional.empty()
        notEmptyOptional == Optional.of(1)
    }

    def "conversion: List<Option<X>> -> Option<List<X>>"() {
        given:
        Statistics statistics = new StatisticsAnswer()

        expect:
        [BigDecimal.TEN, BigInteger.TWO, 1] == statistics.stats().get().collect(Collectors.toList())
        Option.none() == statistics.statsAll()
    }

    def "load additional data only when person has age > 18"() {
        given:
        def adult = new Person(25)
        def kid = new Person(10)
        Supplier<AdditionalData> loader = { new AdditionalData() }

        when:
        Option<AdditionalData> forAdult = Option.when(adult.isAdult(), loader)
        Option<AdditionalData> forKid = Option.when(kid.isAdult(), loader)

        then:
        forAdult.isDefined()
        forAdult.get().data == 'additional data'
        forKid.isEmpty()
    }

    def "map value with a partial function; if not defined -> Option.none()"() {
        given:
        Option<Integer> zero = Option.some(0)

        when:
        Option<Integer> dived = zero.collect(Functions.div())
        Option<Integer> summed = zero.collect(Functions.add())

        then:
        dived == Option.none()
        summed == Option.some(5)
    }

    def "if empty - do action, otherwise do nothing"() {
        given:
        Option<Integer> empty = Option.none()
        Option<Integer> notEmpty = Option.some(5)
        and:
        def counter = new Counter()
        assert counter.get() == 0
        and:
        Runnable action = { counter.increment() }

        when:
        empty.onEmpty(action)
        notEmpty.onEmpty(action)

        then:
        counter.get() == 1
    }

    def "if option has an adult as a value do nothing, otherwise empty"() {
        given:
        Option<Person> adult = Option.some(new Person(20))
        Option<Person> kid = Option.some(new Person(15))

        when:
        Option<Person> checkedAdult = adult.filter({ it.isAdult() })
        Option<Person> checkedKid = kid.filter({ it.isAdult() })

        then:
        checkedAdult == adult
        checkedKid == Option.none()
    }

    def "find in cache, otherwise try to find in the database, otherwise empty"() {
        given:
        def fromCacheId = 1
        def fromDatabaseId = 2
        def fakeId = 3

        when:
        def fromCache = RepositoryAnswer.findById(fromCacheId)
        def fromDatabase = RepositoryAnswer.findById(fromDatabaseId)
        def notFound = RepositoryAnswer.findById(fakeId)

        then:
        Option.some('from cache') == fromCache
        Option.some('from database') == fromDatabase
        Option.none() == notFound
    }

    def "throw IllegalStateException if option is empty, otherwise get value"() {
        given:
        def empty = Option.none()

        when:
        empty.getOrElseThrow({ new IllegalStateException() })

        then:
        thrown(IllegalStateException)
    }

    def "square value then convert to String, if empty - do nothing, null should be treated as 0"() {
        given:
        Option<Integer> defined = Option.some(2)
        Option<Integer> definedNull = Option.some()
        Option<Integer> empty = Option.none()

        when:
        Option<String> definedMapped = defined.map({ nonNull(it) ? it * it : 0 })
                .map({ it.toString() })
        Option<String> definedNullMapped = definedNull.map({ nonNull(it) ? it * it : 0 })
                .map({ it.toString() })
        Option<String> emptyMapped = empty.map({ nonNull(it) ? it * it : 0 })
                .map({ it.toString() })

        then:
        definedMapped.defined
        definedMapped.get() == '4'
        definedNullMapped.defined
        definedNullMapped.get() == '0'
        emptyMapped.empty
    }

    def "flatten Option, basics"() {
        given:
        Option<Integer> id = Option.some(1)

        when:
        Option<Integer> found = id.flatMap({ RepositoryAnswer.findById(it) })

        then:
        found.get() == 'from cache'
    }

    def "flatten Option: find engine for a given car id"() {
        given:
        def existingCarId = 1
        def notExistingCarId = 2

        when:
        Option<Engine> engineFound = Repository.findCarById(existingCarId)
                .flatMap({ Repository.findEngineById(it.engineId) })
        Option<Engine> engineNotFound = Repository.findCarById(notExistingCarId)
                .flatMap({ Repository.findEngineById(it.engineId) })

        then:
        engineFound == Option.some(new Engine(1))
        engineNotFound.empty
    }

    def "increment counter by option value"() {
        given:
        Option<Integer> empty = Option.none()
        Option<Integer> five = Option.some(5)
        and:
        def counter = new Counter()

        when:
        empty.peek({ counter.increment(it) })
        five.peek({ counter.increment(it) })

        then:
        counter.get() == 5
    }

    def "convert: Option<Integer> -> String, Option.none() -> empty string"() {
        given:
        Option<Integer> empty = Option.none()
        Option<Integer> five = Option.some(5)
        and:
        Function<Option<Integer>, String> transformer = { it.isEmpty() ? '' : it.get().toString() }

        when:
        def transformedEmpty = empty.transform(transformer)
        def transformerFive = five.transform(transformer)

        then:
        transformedEmpty == ''
        transformerFive == '5'
    }

    def "sum all values in the list"() {
        given:
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(7))

        when:
        def sum = list.foldLeft(0, { sum, element -> sum + element.inject(0, { acc, value -> acc + value }) })

        then:
        sum == 22
    }

    def "check if somewhere in the list is 7"() {
        given:
        def existing = 7
        def notExisting = 10
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(existing))

        when:
        def exists = list.exists({ it.contains(existing) })
        def notExists = list.exists({ it.contains(notExisting) })

        then:
        exists
        !notExists
    }

    def "check if all values in the list are < 10"() {
        given:
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(7))

        when:
        def lessThan10 = list.forAll({ it.forAll({ it < 10 }) })

        then:
        lessThan10
    }

    def "function composition, monadic law; example of option.map(f g) = option.map(f).map(g)"() {
        given:
        Function<Integer, Integer> nullFunction = { null }
        Function<Integer, String> safeToString = { nonNull(it) ? String.valueOf(it) : 'null' }
        Function<Integer, String> composition = nullFunction.andThen(safeToString)

        expect:
        Optional.of(1).map(composition) != Optional.of(1).map(nullFunction).map(safeToString)
        Optional.of(1).stream().map(composition).findAny() == Optional.of(1).stream().map(nullFunction).map(safeToString).findAny()
        Option.of(1).map(composition) == Option.of(1).map(nullFunction).map(safeToString)
    }
}