import io.vavr.collection.HashSet
import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification

import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * Created by mtumilowicz on 2019-03-02.
 */
class Workshop extends Specification {

    def "create empty option"() {
        given:
        def notEmpty = Option.some() // create here

        expect:
        notEmpty.isEmpty()
    }

    def "create not empty option"() {
        given:
        def notEmpty = Option.none() // create here

        expect:
        notEmpty.isDefined()
    }

    def "conversion: optional -> option"() {
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

    def "conversion: option -> optional"() {
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

    def "conversion: List<Option<X>> -> Option<List<X>>"() {
        given:
        def statistics = new Statistics()

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
        def forAdult = Option.<AdditionalData>none() // convert here
        def forKid = Option.some() // convert here

        then:
        forAdult.isDefined()
        forAdult.get().data == "additional data"
        forKid.isEmpty()
    }

    def "map value with a partial function; if not defined -> Option.none()"() {
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
        Runnable action = { counter.increment() }

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
        !empty // check here
        !notEmpty // check here
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
        def foundById = Option.none() // search here using Repository and (realId, realName)
        def foundByName = Option.none() // search here using Repository and (fakeId, realName)
        def notFound = Option.some() // search here using Repository and (fakeId, fakeName)

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

    def "find engine for a given car id"() {
        given:
        def existingCarId = 1
        def notExistingCarId = 2

        when:
        Option<Engine> engineFound = Option.none() // find using Repository.findCarById, Repository.findEngineById 
        Option<Engine> engineNotFound = Option.none() // find using Repository.findCarById, Repository.findEngineById 

        then:
        engineFound.defined
        engineNotFound.empty
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

    def "convert: Option<Integer> -> String, Option.none() -> empty string"() {
        given:
        def empty = Option.<Integer> none()
        def five = Option.some(5)
        and:
        Function<Option<Integer>, String> transformer = { it.isEmpty() ? "" : it.get().toString() }

        when:
        def transformedEmpty = empty // perform transformation here
        def transformerFive = five // perform transformation here

        then:
        transformedEmpty == ""
        transformerFive == "5"
    }

    def "sum all values in the list"() {
        given:
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(7))

        when:
        def sum = list // perform summing here

        then:
        sum == 22
    }

    def "check if somewhere in the list is 7"() {
        given:
        def existing = 7
        def notExisting = 10
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(existing))

        when:
        def exists = list // perform searching here
        def notExists = list // perform searching here

        then:
        exists
        !notExists
    }

    def "check if all values in the list are < 10"() {
        given:
        def list = List.of(List.of(1, 2, 3), HashSet.of(4, 5), Option.some(7))

        when:
        def lessThan10 = list // perform action here

        then:
        lessThan10
    }

    def "square value or do nothing if empty"() {
        given:
        def defined = Option.some(2)
        def empty = Option.<Integer> none()

        when:
        def definedMapped = Option.none() // map here
        def emptyMapped = Option.some(1) // map here

        then:
        definedMapped.defined
        definedMapped.get() == 4
        emptyMapped.empty
    }
}