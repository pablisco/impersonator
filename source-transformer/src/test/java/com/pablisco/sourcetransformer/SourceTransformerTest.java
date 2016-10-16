package com.pablisco.sourcetransformer;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.ExceptionMethod;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class SourceTransformerTest {

    @Test
    public void shouldTransformSimpleMethod() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path originalJarPath = fileSystem.getPath("original.jar");
        Path transformedJarPath = fileSystem.getPath("transformed.jar");
        DynamicType.Loaded<Object> example = new ByteBuddy().subclass(Object.class)
                .name("com.test.Example")
                .defineMethod("simpleMethod", Void.class, Modifier.PUBLIC)
                    .intercept(ExceptionMethod.throwing(RuntimeException.class, "Nope."))
                .make().load(ClassLoader.getSystemClassLoader());

        new JarBuilder(originalJarPath).append(example).close();

        SourceTransformer transformer = new SourceTransformer();

        transformer.transform(originalJarPath).to(transformedJarPath);

        URLClassLoader classLoader = new URLClassLoader(new URL[]{ transformedJarPath.toUri().toURL() }, null);

        Class<?> type = classLoader.loadClass("com.test.Example");

        Object exampleObject = type.newInstance();

        type.getDeclaredMethod("simpleMethod").invoke(exampleObject);

        Assertions.assertThat(exampleObject).isNotNull();

//        Class<?> transformedType = typePair.type();
//
//        Object subject = transformedType.newInstance();
//
//        assertThat(subject).isNotNull();
//        ((TypeWithSimpleMethod)subject).simpleMethod();
    }

}