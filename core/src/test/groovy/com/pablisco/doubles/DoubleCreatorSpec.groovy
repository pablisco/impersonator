package com.pablisco.doubles

import com.example.Subject

class DoubleCreatorSpec extends spock.lang.Specification {

    def "should create double"() {
        given: def doubleCreator = new DoubleCreator()
        when: doubleCreator.createFromClass("com.example.Subject", "/tmp/example")
        then:
        def classloader = new URLClassLoader([new URL("file:///tmp/example")] as URL[])
        def createdClass = classloader.loadClass("com.example.Subject");
        Subject subject = createdClass.newInstance() as Subject
        subject.simpleMethod()
        println()
    }
}
