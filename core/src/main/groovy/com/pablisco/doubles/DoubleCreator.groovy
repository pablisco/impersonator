package com.pablisco.doubles

import javassist.ClassClassPath
import javassist.ClassPool

import static javassist.CtClass.voidType

class DoubleCreator {

    def createFromClass(String classPath, String targetLocation) {
        def pool = ClassPool.default
        pool.insertClassPath(new ClassClassPath(this.class))
        def ctClass = pool.get(classPath)
        ctClass.methods.findAll { it.returnType == voidType } each {
            switch (it.returnType) {
                case voidType:
                    it.body = "{}"
                    break
            }
        }
        ctClass.writeFile(targetLocation)
    }

}
