import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors 
/**
 * Created by mtumilowicz on 2019-03-02.
 */
class OptionTest extends Specification {

    def "create empty option"() {
        given:
        def notEmpty = Option.some()

        expect:
        notEmpty.isEmpty()
    }

    def "create not empty option"() {
        given:
        def notEmpty = Option.none()

        expect:
        notEmpty.isDefined()
    }

    def "optional -> option"() {
        given:
        def emptyOptional = Optional.empty()
        def notEmptyOptional = Optional.of(1)

        when:
        def emptyOption = emptyOptional // convert here to option
        def notEmptyOption = notEmptyOptional// convert here to option

        then:
        emptyOption == Option.none()
        notEmptyOption == Option.some(1)
    }

    def "option -> optional"() {
        given:
        def emptyOption = Option.none()
        def notEmptyOption = Option.some(1)

        when:
        def emptyOptional = emptyOption // convert here to optional
        def notEmptyOptional = notEmptyOption // convert here to optional

        then:
        emptyOptional == Optional.empty()
        notEmptyOptional == Optional.of(1)
    }

    def "list of options -> option of values"() {
        given:
        def statistics = new Statistics()

        expect:
        [BigDecimal.TEN, BigInteger.TWO, 1] == statistics.stats().get().collect(Collectors.toList())
        Option.none() == statistics.statsAll()
    }

    /*
        if the function returns empty option for some element -> option should be empty
     */

    def "transform a list of values into option of values using function value -> Option(value)"() {
        given:
        Function<Integer, Option<Integer>> convert = { i -> i > 10 ? Option.some(i) : Option.none() }
        Function<Integer, Option<Integer>> convert2 = { i -> i >= 5 ? Option.some(i) : Option.none() }
        def ints = List.of(5, 10, 15)

        when:
        def traversed = Option.some(1) // convert here
        def traversed2 = Option.none() // convert here

        then:
        traversed == Option.none()
        traversed2 == Option.some(List.of(5, 10, 15))
    }

    def "load additional data only when person has age > 18"() {
        given:
        def adult = new Person(25)
        def kid = new Person(10)
        Supplier<AdditionalData> loader = { -> new AdditionalData() }

        when:
        def forAdult = Option.<AdditionalData> none() // convert here
        def forKid = Option.some() // convert here

        then:
        forAdult.isDefined()
        forAdult.get().data == "additional data"
        forKid.isEmpty()
    }

    def "map value with a partial function; if not defined -> empty"() {
        given:
        def option = Option.some(0)

        when:
        def dived = Option.some() // convert here
        def summed = Option.none() // convert here

        then:
        dived == Option.none()
        summed == Option.some(5)
    }

    def "if empty - do action, otherwise do nothing"() {
        given:
        def empty = Option.none()
        def notEmpty = Option.some(5)
        and:
        def counter = new Counter()
        assert counter.get() == 0
        and:
        Runnable action = { -> counter.increment() }

        when:
        empty // perform action here
        notEmpty // perform action here

        then:
        counter.get() == 1
    }

    def "check if option is empty / not empty"() {
        given:
        def empty = Option.none()
        def notEmpty = Option.some()

        expect:
        !empty.isEmpty() // check here
        !notEmpty.isDefined() // check here
    }

    def "if option has an adult as a value do nothing, otherwise empty"() {
        given:
        def adult = Option.some(new Person(20))
        def kid = Option.some(new Person(15))

        when:
        def checkedAdult = Option.none() // transform here
        def checkedKid = Option.some() // transform here

        then:
        checkedAdult == adult
        checkedKid == Option.none()
    }

    def "find by id, otherwise try to find by name, otherwise empty"() {
        given:
        def realId = 1
        def realName = "Michal"
        and:
        def fakeId = 2
        def fakeName = "fakeMichal"

        when:
        def foundById = Option.none() // search here using Repository (realId, realName)
        def foundByName = Option.none() // search here using Repository (fakeId, realName)
        def notFound = Option.some() // search here using Repository (fakeId, fakeName)

        then:
        Option.some("found-by-id") == foundById
        Option.some("found-by-name") == foundByName
        Option.none() == notFound
    }

    def "throw IllegalStateException if option is empty, otherwise get value"() {
        given:
        def empty = Option.none()

        when:
        empty // perform get or throw here

        then:
        thrown(IllegalStateException)
    }

    def "flatten Option<Option> -> Option"() {
        given:
        def id = Option.some(1)

        when:
        def found = id // perform mapping on id, use Repository.findById

        then:
        found.get() == "found-by-id"
    }

    def "increment counter by option value"() {
        given:
        def empty = Option.<Integer>none()
        def five = Option.some(5)
        and:
        def counter = new Counter()

        when:
        empty // increment counter here
        five // increment counter here
        
        then:
        counter.get() == 5
    }

    def "convert option containing number to the string of that number (or empty)"() {
        given:
        def empty = Option.<Integer>none()
        def five = Option.some(5)
        and:
        Function<Option<Integer>, String> transformer = { option -> option.isEmpty() ? "" : option.get().toString()}

        when:
        def transformedEmpty = empty // perform transformation here
        def transformerFive = five // perform transformation here

        then:
        transformedEmpty == ""
        transformerFive == "5"
    }

    def "sum all values in the list"() {
        given:
        def accumulator = new AtomicInteger()
        def list = List.of(List.of(1, 2, 3), Set.of(4, 5), Option.some(7))

        when:
        list // perform summing here

        then:
        accumulator.get() == 22
    }

    def "check if somewhere in the list is 7"() {
        given:
        def existing = 7
        def notExisting = 10
        def list = List.of(List.of(1, 2, 3), Set.of(4, 5), Option.some(existing))

        when:
        def exists = list // perform searching here
        def notExists = list // perform searching here

        then:
        exists
        !notExists
    }
}