package com.ivangochev.raceratingapi.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ResourceFileReader {
    public static String readJsonFileFromClasspath(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        byte[] byteArray = Files.readAllBytes(resource.getFile().toPath());
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
