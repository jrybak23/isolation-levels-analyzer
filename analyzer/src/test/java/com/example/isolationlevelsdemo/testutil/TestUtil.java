package com.example.isolationlevelsdemo.testutil;

import lombok.SneakyThrows;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.doReturn;

public class TestUtil {

    @SneakyThrows
    public static String readFileContent(String filePath) {
        URI uri = getClassLoader().getResource(filePath).toURI();
        return Files.readString(Path.of(uri));
    }

    private static ClassLoader getClassLoader() {
        return currentThread().getContextClassLoader();
    }

    private TestUtil() {
    }
}
