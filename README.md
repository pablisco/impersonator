:rotating_light: WORK IN PROGRESS :rotating_light:

## Impersonator
This tool allows the dependencies of a project to be replaced with alternatives that are easier to use as doubles for tests. 
This allows to have static, final and super methods to be used as part of tests doubles.

## Usage

    apply plugin: 'com.pablisco.impersonator'
    
    // ...
    
    dependencies {
        
        // ...
        
        testCompile impersonate('com.groupid:dependency:1.2.3')
    }
