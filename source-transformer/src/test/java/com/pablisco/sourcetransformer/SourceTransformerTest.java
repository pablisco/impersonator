package com.pablisco.sourcetransformer;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.ExceptionMethod;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class SourceTransformerTest {

    private static final String NAME_PUBLIC_VOID_NO_ARGS_MEMBER = "publicVoidMemberNoArgsMethod";
    private static final String NAME_PUBLIC_VOID_ONE_ARG_MEMBER = "publicVoidMemberOneArgMethod";
    private static final String ORIGINAL_JAR = "original.jar";
    private static final String TRANSFORMED_JAR = "transformed.jar";
    private static final String TYPE_NAME = "com.test.Example";

    private final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    @Test
    public void shouldStubPublicVoidNoArgsMemberMethod() throws Exception {
        SourceTransformer transformer = new SourceTransformer();
        Path originalJarPath = jarPathWithType(fileSystem, createType_withPublicVoidNoArgsMemberMethod());

        transformer.transform(originalJarPath).to(fileSystem.getPath(TRANSFORMED_JAR));

        Class<?> type = loadTypeFromTransformedJar(TYPE_NAME);
        type.getDeclaredMethod(NAME_PUBLIC_VOID_NO_ARGS_MEMBER).invoke(type.newInstance());
    }

    @Test
    public void shouldStubPublicVoidOneArgMemberMethod() throws Exception {
        SourceTransformer transformer = new SourceTransformer();
        Path originalJarPath = jarPathWithType(fileSystem, createType_withPublicVoidOneArgMemberMethod());

        transformer.transform(originalJarPath).to(fileSystem.getPath(TRANSFORMED_JAR));

        Class<?> type = loadTypeFromTransformedJar(TYPE_NAME);
        type.getDeclaredMethod(NAME_PUBLIC_VOID_ONE_ARG_MEMBER).invoke(type.newInstance());
    }

    private Class<?> loadTypeFromTransformedJar(String typeName) throws MalformedURLException, ClassNotFoundException {
        return classPathFromUrl(TRANSFORMED_JAR).loadClass(typeName);
    }

    private URLClassLoader classPathFromUrl(String jarPath) throws MalformedURLException {
        return new URLClassLoader(new URL[]{ fileSystem.getPath(jarPath).toUri().toURL() }, null);
    }

    private DynamicType.Loaded<Object> createType_withPublicVoidOneArgMemberMethod() throws MalformedURLException {
        return new ByteBuddy().subclass(Object.class)
                .name(TYPE_NAME)
                .defineMethod(NAME_PUBLIC_VOID_ONE_ARG_MEMBER, Void.class, Modifier.PUBLIC)
                .intercept(ExceptionMethod.throwing(RuntimeException.class, "Nope."))
                .make().load(emptyClassLoader());
    }

    private DynamicType.Loaded<Object> createType_withPublicVoidNoArgsMemberMethod() throws MalformedURLException {
        return new ByteBuddy().subclass(Object.class)
                    .name(TYPE_NAME)
                    .defineMethod(NAME_PUBLIC_VOID_NO_ARGS_MEMBER, Void.class, Modifier.PUBLIC)
                        .intercept(ExceptionMethod.throwing(RuntimeException.class, "Nope."))
                    .make().load(emptyClassLoader());
    }

    private static URLClassLoader emptyClassLoader() {
        return new URLClassLoader(new URL[0]);
    }

    private Path jarPathWithType(FileSystem fileSystem, DynamicType.Loaded<Object> example) throws IOException {
        Path originalJarPath = fileSystem.getPath(ORIGINAL_JAR);
        new JarBuilder(originalJarPath).append(example).close();
        return originalJarPath;
    }

}