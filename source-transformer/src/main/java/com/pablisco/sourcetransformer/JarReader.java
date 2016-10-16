package com.pablisco.sourcetransformer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarReader {

    public Set<Class<?>> read(final Path path) throws IOException, ClassNotFoundException {
        return readClasses(pathClassLoader(path), new JarInputStream(Files.newInputStream(path)));
    }

    private static Set<Class<?>> readClasses(ClassLoader classLoader, JarInputStream input) throws IOException, ClassNotFoundException {
        Set<Class<?>> result = new HashSet<>();
        for(JarEntry entry = input.getNextJarEntry(); entry != null; entry = input.getNextJarEntry()) {
            if (hasClassName(entry)) result.add(loadType(classLoader, entry.getName()));
        }
        return result;
    }

    private static URLClassLoader pathClassLoader(Path path) throws MalformedURLException {
        return new URLClassLoader(new URL[] { path.toUri().toURL() }, null);
    }

    private static boolean hasClassName(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private static Class<?> loadType(ClassLoader classLoader, String name) throws ClassNotFoundException {
        String className = name.replace(File.separatorChar, '.');
        className = className.substring(0, className.indexOf(".class"));
        return classLoader.loadClass(className);
    }

}
