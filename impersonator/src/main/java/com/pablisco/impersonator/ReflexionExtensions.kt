package com.pablisco.impersonator

inline fun <reified T> typeName() : String = T::class.java.canonicalName