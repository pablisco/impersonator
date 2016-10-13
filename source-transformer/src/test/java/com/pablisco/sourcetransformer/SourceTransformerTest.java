package com.pablisco.sourcetransformer;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public class SourceTransformerTest {

    @Test
    public void shouldTransformSimpleMethod() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path originalJarPath = fileSystem.getPath("original.jar");
        DynamicType.Loaded<Object> example = new ByteBuddy().subclass(Object.class)
                .name("com.test.Example")
                .make().load(ClassLoader.getSystemClassLoader());
        DynamicType.Loaded<Object> example2 = new ByteBuddy().subclass(Object.class)
                .name("com.test.Example2")
                .make().load(ClassLoader.getSystemClassLoader());

        new JarBuilder(originalJarPath).append(example).append(example2).close();

//        SourceTransformer transformer = new SourceTransformer();
//
//        DoubleTypePair typePair = transformer.transform(TypeWithSimpleMethod.class);
//
//        Class<?> transformedType = typePair.type();
//
//        Object subject = transformedType.newInstance();
//
//        assertThat(subject).isNotNull();
//        ((TypeWithSimpleMethod)subject).simpleMethod();
    }

}