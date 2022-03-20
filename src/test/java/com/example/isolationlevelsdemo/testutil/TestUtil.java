package com.example.isolationlevelsdemo.testutil;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;

import static java.lang.Thread.currentThread;
import static java.time.ZoneId.systemDefault;
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
