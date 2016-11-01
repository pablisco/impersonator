package com.pablisco.sourcetransformer;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class SourceTransformer {

    public OngoingTransformation transform(Path source) {
        return new OngoingTransformation(source);
    }

    public static class OngoingTransformation {
        private final Path source;

        public OngoingTransformation(Path source) {
            this.source = source;
        }

        public void to(Path target) throws IOException, ClassNotFoundException {
            JarReader jarReader = new JarReader();
            JarBuilder jarBuilder = new JarBuilder(target);
            Set<Class<?>> types = jarReader.read(source);
            for (Class<?> type : types) {
                DynamicType.Builder<?> modifiedType = new ByteBuddy()
                        .redefine(type)
                        .method(ElementMatchers.isDeclaredBy(type))
                        .intercept(FixedValue.nullValue());
                jarBuilder.append(modifiedType.make());
            }
            jarBuilder.close();
        }
    }

}
