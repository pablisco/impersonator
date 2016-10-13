package com.pablisco.sourcetransformer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SourceTransformer {

    public OngoingTransformation tranform(Path source) {
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



    List<String> listClassPaths(JarFile jarFile) {
        List<String> files = new LinkedList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class")) {
                files.add(name);
            }
        }
        return files;
    }

    List<String> listClassNames(JarFile jarFile) {
        List<String> classPaths = listClassPaths(jarFile);
        List<String> names = new LinkedList<>();
        for (String classPath : classPaths) {
            String className = classPath.replace(File.separatorChar, '.');
            className = className.substring(0, className.indexOf(".class"));
            names.add(className);
        }
        return names;
    }

    List<Class<?>> listClasses(File file) throws IOException, ClassNotFoundException {
        JarFile jarFile = new JarFile(file);
        List<String> classNames = listClassNames(jarFile);
        List<Class<?>> classes = new LinkedList<>();

        ClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() }, getClass().getClassLoader());

        for (String className : classNames) {
            Class.forName(className, true, classLoader);
            classes.add(classLoader.loadClass(className));
        }

        return classes;
    }

}
