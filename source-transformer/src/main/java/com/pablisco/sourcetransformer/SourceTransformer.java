package com.pablisco.sourcetransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SourceTransformer {

    public OngoingTransformation transform(Path source) {
        return new OngoingTransformation(source);
    }

    public static class OngoingTransformation {
        private final Path source;

        public OngoingTransformation(Path source) {
            this.source = source;
        }

        public void to(Path target) throws IOException {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
