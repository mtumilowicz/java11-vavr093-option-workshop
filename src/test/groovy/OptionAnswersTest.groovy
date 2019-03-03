import io.vavr.control.Option
import spock.lang.Specification

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
        Runnable action = {-> counter.increment()}
        
        when:
        empty.onEmpty(action)
        notEmpty.onEmpty(action)
        
        then:
        counter.get() == 1
    }
}