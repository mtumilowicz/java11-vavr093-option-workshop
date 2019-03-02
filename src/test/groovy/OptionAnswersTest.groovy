import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification


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
        def statistics = new Statistics()
        
        expect:
        // return Option.sequence(List.of(stats1(), stats2(), stats3(), stats4()));
        Option.some(List.of(BigDecimal.TEN, BigInteger.TWO, 1)) == statistics.get()
        // return Option.sequence(List.of(stats1(), stats2(), stats3()));
        Option.none() == statistics.getAll()
    }
}