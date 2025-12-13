package com.nimdec;

import com.nimdec.util.FilesWalkUtil;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;

import static com.nimdec.util.UUIDUtil.writeUUIDToJsonFile;
import static com.nimdec.util.UUIDUtil.writeUUIDToOutfitJson;

public class Main {

    public static void main(String[] args) {
//        var rootDirectory = Path.of("G:\\My Drive\\Wardrobe\\JSON Catalogue");
//        var files = FilesWalkUtil.jsonFiles(
//                rootDirectory,
//                (Path path) -> Set.of("dress_shirt_18.json").contains(path.getFileName().toString())
//        );
//
//        for(var file : files) {
//            writeUUIDToJsonFile(file);
//        }

        var rootDirectory = Path.of("G:\\My Drive\\Wardrobe\\JSON Catalogue\\Outfits");
        var files = FilesWalkUtil.jsonFiles(
                rootDirectory,
                (Path path) -> {
                    try {
                        return Files.size(path) != 0;
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
        );
        for (var file : files) {
            writeUUIDToOutfitJson(file);
        }

    }
}