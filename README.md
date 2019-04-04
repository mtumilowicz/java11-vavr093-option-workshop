[![Build Status](https://travis-ci.com/mtumilowicz/java11-vavr093-option-workshop.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-vavr093-option-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# java11-vavr093-option-workshop

# project description
* https://www.vavr.io/vavr-docs/#_option
* https://static.javadoc.io/io.vavr/vavr/0.9.3/io/vavr/control/Option.html
* https://github.com/mtumilowicz/java11-vavr-option
* https://github.com/mtumilowicz/java11-category-theory-optional-is-not-functor
* on the workshop we will try to fix failing `Workshop`
* answers: `Answers` (same tests as in `Workshop` but correctly solved)

# theory in a nutshell
* similar to Optional, but with bigger, more flexible API
* `interface Option<T> extends Value<T>, Serializable`
    * `interface Value<T> extends Iterable<T>`
* `Option` is isomorphic to singleton list (either has element or not, so it could be treated as collection)
* two implementations:
    * `final class Some<T>`
    * `final class None<T>`
* `None` instance is a singleton
* returning `null` inside `map` doesn't cause `Option` switch to `None` (contrary to `Optional`)
    ```
    expect:
    Option.some(2).map(alwaysNull).defined
    Optional.of(2).map(alwaysNull).empty
    ```
* excellent for modelling exists / not exists (Spring Data - `findById`)
    * however not every "exceptional" behaviour could be modelled as exists / not exists
# conclusions in a nutshell
* **we omit methods that `Optional` has**
* easy conversion `Option` <-> `Optional`
    * `Option.ofOptional`
    * `option.toJavaOptional()`
* handy conversion `List<Option<T>> -> Option<List<T>>`
    * `Option.sequence(list)`
    * if any of the `Options` are `None` - returns `None`
* conditional supplier
    * `Option<T> when(boolean condition, Supplier<? extends T> supplier)`
* mapping with partial function
    * `Option<R> collect(PartialFunction<? super T, ? extends R> partialFunction)`
    * if function is not defined at a value - returns `None`
* side effects on `None`
    * `Option<T> onEmpty(Runnable action)`
* lazy alternative (in `Optional` since 11)
    * `Option<T> orElse(Supplier<? extends Option<? extends T>> supplier)`
* well written equals
    * Some
        ```
        return (obj == this) || (obj instanceof Some && Objects.equals(value, ((Some<?>) obj).value));
        ```
    * None
        ```
        return o == this
        ```