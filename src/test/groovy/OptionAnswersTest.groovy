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
class OptionAnswersTest extends Specification {

    def "create empty option"() {
        given:
        def notEmpty = Option.none()

        expect:
        notEmpty.isEmpty()
    }

    def "create not empty option"() {
        given:
        def notEmpty = Option.some()

        expect:
        notEmpty.isDefined()
    }

    def "optional -> option"() {
        given:
        def emptyOptional = Optional.empty()
        def notEmptyOptional = Optional.of(1)

        when:
        def emptyOption = Option.ofOptional(emptyOptional)
        def notEmptyOption = Option.ofOptional(notEmptyOptional)

        then:
        emptyOption == Option.none()
        notEmptyOption == Option.some(1)
    }

    def "option -> optional"() {
        given:
        def emptyOption = Option.none()
        def notEmptyOption = Option.some(1)

        when:
        def emptyOptional = emptyOption.toJavaOptional()
        def notEmptyOptional = notEmptyOption.toJavaOptional()

        then:
        emptyOptional == Optional.empty()
        notEmptyOptional == Optional.of(1)
    }

    def "list of options -> option of list"() {
        given:
        def statistics = new StatisticsAnswer()

        expect:
        [BigDecimal.TEN, BigInteger.TWO, 1] == statistics.stats().get().collect(Collectors.toList())
        Option.none() == statistics.statsAll()
    }

    def "load additional data only when person has age > 18"() {
        given:
        def adult = new Person(25)
        def kid = new Person(10)
        Supplier<AdditionalData> loader = { -> new AdditionalData() }

        when:
        def forAdult = Option.when(adult.isAdult(), loader)
        def forKid = Option.when(kid.isAdult(), loader)

        then:
        forAdult.isDefined()
        forAdult.get().data == "additional data"
        forKid.isEmpty()
    }

    def "map value with a partial function; if not defined -> empty"() {
        given:
        def option = Option.some(0)

        when:
        def dived = option.collect(new Functions().div())
        def summed = option.collect(new Functions().add())

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
        empty.onEmpty(action)
        notEmpty.onEmpty(action)

        then:
        counter.get() == 1
    }

    def "check if option is empty / not empty"() {
        given:
        def empty = Option.none()
        def notEmpty = Option.some()

        expect:
        empty.isEmpty()
        notEmpty.isDefined()
    }

    def "if option has an adult as a value do nothing, otherwise empty"() {
        given:
        def adult = Option.some(new Person(20))
        def kid = Option.some(new Person(15))

        when:
        def checkedAdult = adult.filter({ p -> p.isAdult() })
        def checkedKid = kid.filter({ p -> p.isAdult() })

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
        def foundById = Repository.findById(realId).orElse({ -> Repository.findByName(realName) })
        def foundByName = Repository.findById(fakeId).orElse({ -> Repository.findByName(realName) })
        def notFound = Repository.findById(fakeId).orElse({ -> Repository.findByName(fakeName) })

        then:
        Option.some("found-by-id") == foundById
        Option.some("found-by-name") == foundByName
        Option.none() == notFound
    }
    
    def "throw IllegalStateException if option is empty, otherwise get value"() {
        given:
        def empty = Option.none()
        
        when:
        empty.getOrElseThrow({-> new IllegalStateException()})
        
        then:
        thrown(IllegalStateException)
    }
    
    def "flatten Option<Option> -> Option"() {
        given:
        def id = Option.some(1)
        
        when:
        def found = id.flatMap({ value -> Repository.findById(value) })
        
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
        empty.peek({value -> counter.increment(value)})
        five.peek({value -> counter.increment(value)})
        
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
        def transformedEmpty = empty.transform(transformer)
        def transformerFive = five.transform(transformer)
        
        then:
        transformedEmpty == ""
        transformerFive == "5"
    }
    
    def "sum all values in the list"() {
        given:
        def accumulator = new AtomicInteger()
        def list = List.of(List.of(1, 2, 3), Set.of(4, 5), Option.some(7))
        
        when:
        list.forEach({iterable -> iterable.forEach({i -> accumulator.addAndGet(i)})})
        
        then:
        accumulator.get() == 22
    }

    def "check if somewhere in the list is 7"() {
        given:
        def existing = 7
        def notExisting = 10
        def list = List.of(List.of(1, 2, 3), Set.of(4, 5), Option.some(existing))

        when:
        def exists = list.exists({iterable -> iterable.contains(existing)})
        def notExists = list.exists({iterable -> iterable.contains(notExisting)})
        
        then:
        exists
        !notExists
    }
}