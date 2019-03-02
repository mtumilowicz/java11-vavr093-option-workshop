import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification

import java.util.function.Function
import java.util.function.Supplier 
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
        Option.some(List.of(BigDecimal.TEN, BigInteger.TWO, 1)) == statistics.get()
        Option.none() == statistics.getAll()
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
        def traversed = Option.traverse(ints, convert)
        def traversed2 = Option.traverse(ints, convert2)

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
}