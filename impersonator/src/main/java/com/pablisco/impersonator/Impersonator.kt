package com.pablisco.impersonator

import com.google.auto.service.AutoService
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(javax.annotation.processing.Processor::class)
class ImpersonatorProcessor : AbstractProcessor() {

    lateinit var typeUtils : Types
    lateinit var elementUtils: Elements
    lateinit var filer: Filer
    lateinit var messager: Messager
    val factoryClasses = LinkedHashMap<String, Any>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        typeUtils = processingEnv.typeUtils
        elementUtils = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        return false
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(typeName<Impersonate>())
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

}