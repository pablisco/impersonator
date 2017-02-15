package com.pablisco.impersonator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

class ImpersonatorPlugin : Plugin<Project> {

    override fun apply(project : Project) {
        project.convention.plugins.put("utilities", ImpersonatorPluginConvention())
    }
}

class ImpersonatorPluginConvention {

    fun impersonate(dependency : Dependency) : Dependency {
        return dependency
    }

    fun impersonate(project : Project) : Dependency {
        return project.dependencies.create(project.dependencies.project(mapOf("path" to ":good-library")))
    }

}