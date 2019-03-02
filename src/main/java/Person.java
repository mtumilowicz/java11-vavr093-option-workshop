/**
 * Created by mtumilowicz on 2019-03-03.
 */
class Person {
    final int age;

    Person(int age) {
        this.age = age;
    }
    
    boolean isAdult() {
        return age >= 18;
    }
}

class AdditionalData {
    final String data = "additional data";
}
