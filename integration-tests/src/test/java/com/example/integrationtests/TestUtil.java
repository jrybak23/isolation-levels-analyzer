package com.example.integrationtests;

import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Thread.currentThread;

public class TestUtil {

    @SneakyThrows
    public static String readFileContent(String filePath) {
        return Files.readString(Paths.get(filePath));
    }

    @SneakyThrows
    public static String readClasspathFileContent(String filePath) {
        URI uri = getClassLoader().getResource(filePath).toURI();
        return Files.readString(Path.of(uri));
    }

    private static ClassLoader getClassLoader() {
        return currentThread().getContextClassLoader();
    }

    private TestUtil() {
    }

}
