import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification


/**
 * Created by mtumilowicz on 2019-03-02.
 */
class OptionTest extends Specification {
    
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
    
    def "list of options -> option of list"() {
        given:
        def statistics = new Statistics()
        
        expect:
        Option.some(List.of(BigDecimal.TEN, BigInteger.TWO, 1)) == statistics.get()
        Option.none() == statistics.getAll()
    }
}