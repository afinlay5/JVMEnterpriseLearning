package com.nimdec.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UUIDUtil {


    /*
      Overwrite JSON files with generated SHA-256 UUID
     */
    public static void writeUUIDToJsonFile(Path jsonPath) {

        var jsonDocument = UUIDUtil.<Map<String, Object>>readJsonFileAsObject(jsonPath);

        updateDocument(jsonDocument);

        writeJsonObjectToFile(jsonPath, jsonDocument);
    }

    public static void writeUUIDToOutfitJson(Path jsonPath) {
        var outfits = UUIDUtil.<List<Map<String, Object>>>readJsonFileAsObject(jsonPath);
        for (var outfit : outfits) {
            var components = (Map<String, Object>) outfit.get("components");
            var componentsSequence = generateComponentsJsonSequence(components);
            var uuid = generateUUID(componentsSequence);
            outfit.put("uuid", uuid);
        }

        writeJsonObjectToFile(jsonPath, outfits);

        System.out.println();
    }

    private static void updateDocument(Map<String, Object> jsonDocument) {

        var uuid = jsonDocument.get("uuid");

//        if (null == uuid) {
            var item = (Map<String, Object>)(jsonDocument.get("item"));
            var jsonSequence = generateItemsJsonSequence(item);
            jsonDocument.put("uuid", generateUUID(jsonSequence));
            jsonDocument.put("last_updated", easternTimeNow());
//        }
    }

    private static String easternTimeNow() {

        return OffsetDateTime.now(ZoneId.of("America/New_York"))
                .withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
    }

    public static <T> T readJsonFileAsObject(Path jsonPath) {

        try {
            return new ObjectMapper()
                    .readValue(jsonPath.toFile(), new TypeReference<>() {});

        } catch (IOException ioException) {
            throw new UncheckedIOException("For path: %s".formatted(jsonPath), ioException);
        }
    }

    private static String generateUUID(String jsonSequence) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(jsonSequence.getBytes(StandardCharsets.UTF_8));

            // Take first 8 bytes (64 bits) and hex-encode → 16-char string
            StringBuilder sb = new StringBuilder(16);
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {

            // SHA-256 is guaranteed in every modern JDK, so this is basically impossible
            throw new RuntimeException("SHA-256 not available", e);
        }

    }

    /**
     * Canonical JSON Sequence Contract is: type|sub_type|variant|set|quantity|id
     * All of these items are derived from the top level item object.
     */
    private static String generateItemsJsonSequence(Map<String, Object> itemJsonMap) {

        var set = itemJsonMap.getOrDefault("set", "N/A");
        var quantity = itemJsonMap.getOrDefault("quantity", "1");

        return new StringBuilder()
                .append(itemJsonMap.get("type"))
                .append("|")
                .append(itemJsonMap.get("sub_type"))
                .append("|")
                .append(itemJsonMap.get("variant"))
                .append("|")
                .append(set)
                .append("|")
                .append(quantity)
                .append("|")
                .append(itemJsonMap.get("id"))
                .toString();

    }

    /**
     * Canonical JSON Sequence Contract is: |field|uuid (recurring)
     * All of these items are derived from the top level components object.
     */
    private static String generateComponentsJsonSequence(Map<String, Object> componentsJsonMap) {

        var orderedMap = new LinkedHashMap<>(componentsJsonMap);

        var sb = new StringBuilder();
        for (var set : orderedMap.entrySet()) {
            sb.append("|");
            sb.append(set.getKey());
            sb.append("|");
            sb.append(set.getValue());
        }

        return sb.toString();
    }

    public static <T> void writeJsonObjectToFile(Path jsonPath, T jsonDocument) {
        var gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        try(var writer = Files.newBufferedWriter(jsonPath)) {
            gson.toJson(jsonDocument, writer);
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }


}
