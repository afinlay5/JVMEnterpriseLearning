package com.nimdec;

import com.google.common.collect.Sets;
import com.nimdec.util.FilesWalkUtil;
import com.nimdec.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nimdec.util.UUIDUtil.*;

public class Main {

    private static final Set<String> IMAGE_EXTENSIONS =
            Set.of("jpg", "jpeg", "heic", "dng");

    public static void main(String[] args) {
//        updateMediaRegistry();
        writeGarmentUUID();
//        writeOutfitUUID();
    }




    private static void reportMissingMediaRegistry() {
        // TODO
    }

    private static void updateMediaObjectToIncludePrimaryField() {
        // TODO
    }



    private static void updateMediaRegistry() {

        // Garment name to Pics mapping
        var imgPrefixToPathsMapping = generateImgPrefixToPathMapping(getImgPaths());
        var imgGarmentShortNames = imgPrefixToPathsMapping.keySet();

        // Garments
        var relevantGarments = loadGarments().stream()
                .filter(path -> imgGarmentShortNames.contains(genSimpleGarmentName(path)) )
                .toList();

        for (var garment : relevantGarments ) {

            var garmentSimpleName = genSimpleGarmentName(garment);

            // set of imgs
            var imgsPresent = imgPrefixToPathsMapping.get(garmentSimpleName);

            //Load the file as JSON map
            var jsonMap = UUIDUtil.<LinkedHashMap<String, Object>>readJsonFileAsObject(garment);

            //Does it have media key?
            var containsMedia = jsonMap.containsKey("media");

            var mediaList = new ArrayList<String>();

            var mediaDirectory = garment.getParent().resolve("Media");

            if (containsMedia) {
                mediaList.addAll((List<String>) jsonMap.get("media"));



                for (var img : imgsPresent) {

                    var fileName = img.getFileName().toString().toLowerCase();

                    // Add new imgs to imgs list in file
                    var imgSimpleFileName = img.getFileName().toString().toLowerCase();
                    if (!mediaList.contains(imgSimpleFileName)) {
                        mediaList.add(imgSimpleFileName);

                        // move new img to media location
                        var garmentMediaFile = mediaDirectory.resolve(fileName);
                        cutAndPaste(img, garmentMediaFile);
                    }
                }

                //write JSON back
                jsonMap.put("media", mediaList);

                // write JSON map
                writeJsonObjectToFile(garment, jsonMap);

                System.out.println();

            }
            else {
                for (var img : imgsPresent) {
                    var fileName = img.getFileName().toString().toLowerCase();

                    // populate media list
                    mediaList.add(fileName);

                    if (!Files.exists(mediaDirectory)) {
                        try {
                            Files.createDirectory(mediaDirectory);
                        } catch (IOException ioe) {
                            throw new UncheckedIOException("Could not make Media dir", ioe);
                        }
                    }

                    // move new img to media location
                    var garmentMediaFile = mediaDirectory.resolve(fileName);
                    cutAndPaste(img, garmentMediaFile);
                }

                jsonMap = putMediaInPlace(jsonMap, mediaList);

                // write JSON map
                writeJsonObjectToFile(garment, jsonMap);

                System.out.println();
            }

            // verify each garment has all their media.
            try (var mediaFilesStream = Files.list(mediaDirectory)) {

                var mediaFilesInJSON = new HashSet<>(mediaList);

                var mediaFilesOnDisk = mediaFilesStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(garmentSimpleName))
                    .map(path -> path.getFileName().toString()  )
                    .collect(Collectors.toUnmodifiableSet());

                if (mediaFilesOnDisk.equals(mediaFilesInJSON)) continue;

                var whatsInOurJsonButNotOnDisk = Sets.difference(mediaFilesInJSON, mediaFilesOnDisk);
                if (!whatsInOurJsonButNotOnDisk.isEmpty()) {
                    System.out.println("WARNING!! We have these files in our JSON but they aren't on disk:\n" + whatsInOurJsonButNotOnDisk);
                }
                var whatsOnDiskButNotInOurJson = Sets.difference(mediaFilesOnDisk, mediaFilesInJSON);
                if (!whatsOnDiskButNotInOurJson.isEmpty()) {
                    System.out.println("WARNING!! We have these files on disk but they are not in our JSON:\n" + whatsOnDiskButNotInOurJson);
                }
            }
            catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }

        }


    }

    private static LinkedHashMap<String, Object> putMediaInPlace(LinkedHashMap<String, Object> jsonMap, ArrayList<String> mediaList) {
        var jsonMapAsList = new ArrayList<>(jsonMap.entrySet());

        var targetIndex = -1;
        for (int i=0; i<jsonMapAsList.size(); i++) {
            var entry = jsonMapAsList.get(i);
            if (entry.getKey().equals("notes")) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) throw new IllegalArgumentException("coud not insert" + mediaList.toString());

        Map.Entry<String, Object> entry = new AbstractMap.SimpleImmutableEntry<>("media", mediaList);

        jsonMapAsList.add(targetIndex, entry);

        return jsonMapAsList.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            return b;
                        }, //gets the latest val
                        LinkedHashMap::new
                ));
    }

    private static void cutAndPaste(Path img, Path garmentMediaFolder) {
        try {
            Files.move(img, garmentMediaFolder, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private static String genSimpleGarmentName(Path path) {
        var garmentFileName = path.getFileName().toString();
        return garmentFileName.substring(0, garmentFileName.indexOf(".json")).toLowerCase();
    }

    private static Map<String, Set<Path>> generateImgPrefixToPathMapping(List<Path> imgPaths) {

        var imgPrefixToPathMapping = new HashMap<String, Set<Path>>();

        for (Path path : imgPaths) {
            var fileName = path.getFileName().toString().toLowerCase();
            var simpleName =  fileName.substring(0, fileName.indexOf("_img"));

            var setForPrefix = imgPrefixToPathMapping.get(simpleName);

            if (setForPrefix == null){
                var set = new  HashSet<Path>();
                set.add(path);

                imgPrefixToPathMapping.put(simpleName, set);
            }
            else {
                setForPrefix.add(path);
            }

        }

        return imgPrefixToPathMapping;
    }

    private static List<Path> getImgPaths() {
        Path mediaDir = Path.of("G:\\My Drive\\Wardrobe\\Media");

        try (var paths = Files.list(mediaDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().contains("_img_"))
                    .filter(p -> {
                        var filename = p.getFileName().toString().toLowerCase();
                        var extensionIndex = filename.lastIndexOf('.');

                        var extension =  filename.substring(extensionIndex + 1);

                        return IMAGE_EXTENSIONS.contains(extension);
                        }
                    )
                    .toList();

        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private static List<Path> loadGarments() {

        var rootDirectory = Path.of("G:\\My Drive\\Wardrobe\\JSON Catalogue");

        return FilesWalkUtil.jsonFiles(
                rootDirectory,
                (Path path) -> !path.getFileName().toString().startsWith("Occasions")
                        && !path.getFileName().toString().toLowerCase().startsWith("outfit")
                        && !path.getFileName().toString().startsWith("template")
        );
    }

    private static void writeGarmentUUID() {
        var rootDirectory = Path.of("G:\\My Drive\\Wardrobe\\JSON Catalogue");
        var files = FilesWalkUtil.jsonFiles(
                rootDirectory
                ,(Path path) -> path.getFileName().toString().contains("pendant_4.json")
//                ,(Path path) -> !StringUtils.containsAny(path.toString(),
//                        "Outfits\\",
//                        "Template\\",
//                        "Occasions\\",
//                        "Temporal.json")
        );

        for(var file : files) {
            writeUUIDToJsonFile(file);
        }
    }

    private static void writeOutfitUUID() {
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