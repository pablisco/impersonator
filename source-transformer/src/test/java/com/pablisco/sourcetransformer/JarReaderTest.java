package com.pablisco.sourcetransformer;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

import org.assertj.core.api.Condition;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JarReaderTest {

    private static final String LIBRARY_JAR_NAME = "library.jar";

    @Test
    public void shouldListNoEntry() throws Exception {
        Path jarPath = givenPath_whenJarHasTypes(LIBRARY_JAR_NAME);

        Set<Class<?>> results = new JarReader().read(jarPath);

        assertThat(results).isEmpty();
    }

    @Test
    public void shouldListOneEntry() throws Exception {
        Path jarPath = givenPath_whenJarHasTypes(LIBRARY_JAR_NAME, "com.example.Example");

        Set<Class<?>> results = new JarReader().read(jarPath);

        assertThat(results).hasSize(1);
        assertThat(results).haveExactly(1, classWithName("com.example.Example"));
    }

    @Test
    public void shouldList_withMultipleEntries() throws Exception {
        Path jarPath = givenPath_whenJarHasTypes(LIBRARY_JAR_NAME, "com.example.Example1", "com.example.Example2", "com.example.Example3");

        Set<Class<?>> results = new JarReader().read(jarPath);

        assertThat(results).hasSize(3);
        assertThat(results).haveExactly(1, classWithName("com.example.Example1"));
        assertThat(results).haveExactly(1, classWithName("com.example.Example2"));
        assertThat(results).haveExactly(1, classWithName("com.example.Example3"));
    }

    @Test
    public void shouldFail_whenClassNotFound() throws Exception {


    }

    private Condition<Class<?>> classWithName(final String typeName) {
        return new Condition<Class<?>>() {
            @Override
            public boolean matches(Class<?> value) {
                return value.getName().equals(typeName);
            }
        };
    }

    private Path givenPath_whenJarHasTypes(String jarName, String... typeNames) throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path jarPath = fileSystem.getPath(jarName);

        JarBuilder jarBuilder = new JarBuilder(jarPath);
        for (String typeName : typeNames) {
            appendTypeWithName(jarBuilder, typeName);
        }
        jarBuilder.close();
        return jarPath;
    }

    private void appendTypeWithName(JarBuilder jarBuilder, String typeName) throws IOException {
        DynamicType.Loaded<Object> exampleType = new ByteBuddy()
                .subclass(Object.class)
                .name(typeName)
                .make().load(getClass().getClassLoader());
        jarBuilder.append(exampleType);
    }

}