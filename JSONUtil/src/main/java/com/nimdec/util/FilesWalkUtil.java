package com.nimdec.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public final class FilesWalkUtil {

    public static List<Path> jsonFiles(Path rootDirectory, Predicate<Path> filter) {
        try (var filesStream = Files.walk(rootDirectory)) {

            return filesStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .filter(filter)
                    .toList();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static List<Path> jsonFiles(Path rootDirectory) {
        return jsonFiles(rootDirectory, p -> true);
    }
}
