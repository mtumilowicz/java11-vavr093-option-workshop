import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification

import java.util.function.Function


/**
 * Created by mtumilowicz on 2019-03-02.
 */
class OptionAnswersTest extends Specification {

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
}