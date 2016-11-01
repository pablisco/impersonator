package com.pablisco.sourcetransformer;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static net.bytebuddy.dynamic.ClassFileLocator.CLASS_FILE_EXTENSION;

/**
 * Used to create a new JAR file or add new entries to an existing one.
 */
class JarBuilder implements Closeable {

    private final JarOutputStream outputStream;

    JarBuilder(Path jarPath) throws IOException {
        this(jarPath, defaultManifest());
    }

    JarBuilder(Path jarPath, Manifest manifest) throws IOException {
        if (Files.exists(jarPath)) {
            Path tmpPath = moveJarToTempLocation(jarPath);
            JarInputStream tmpJarStream = new JarInputStream(Files.newInputStream(tmpPath));
            outputStream = new JarOutputStream(
                    Files.newOutputStream(jarPath),
                    mergeManifests(tmpJarStream.getManifest(), manifest)
            );
            copyJarEntries(tmpJarStream, outputStream);
            Files.delete(tmpPath);
        } else {
            outputStream = new JarOutputStream(Files.newOutputStream(jarPath), manifest);
        }
    }

    private static Manifest defaultManifest() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        return manifest;
    }

    private Path moveJarToTempLocation(Path jarPath) throws IOException {
        Path tmpPath = Files.createTempFile(null, "jar");
        Files.copy(jarPath, tmpPath, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(jarPath);
        return tmpPath;
    }

    private static void copyJarEntries(JarInputStream source, JarOutputStream outputStream) throws IOException {
        JarEntry entry = source.getNextJarEntry();
        while (entry != null) {
            outputStream.putNextEntry(entry);
            entry = source.getNextJarEntry();
        }
    }

    private static Manifest mergeManifests(Manifest firstManifest, Manifest secondManifest) {
        Manifest result = new Manifest();
        if (firstManifest != null) {
            result.getMainAttributes().putAll(firstManifest.getMainAttributes());
        }
        if (secondManifest != null) {
            result.getMainAttributes().putAll(secondManifest.getMainAttributes());
        }
        return result;
    }

    private static JarEntry entry(TypeDescription description) {
        return new JarEntry(description.getInternalName() + CLASS_FILE_EXTENSION);
    }

    JarBuilder append(DynamicType dynamicType) throws IOException {
        for (Map.Entry<TypeDescription, byte[]> entry : dynamicType.getAuxiliaryTypes().entrySet()) {
            append(entry(entry.getKey()), entry.getValue());
        }
        append(entry(dynamicType.getTypeDescription()), dynamicType.getBytes());
        return this;
    }

    private void append(ZipEntry entry, byte[] bytes) throws IOException {
        outputStream.putNextEntry(entry);
        outputStream.write(bytes);
        outputStream.closeEntry();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

}
