package com.bimawib.orderapi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogReaderController {
	private static final String LOG_FILE_NAME = "logs/log.log";

    @GetMapping("/logs-search")
    public ResponseEntity<Map<String, Long>> searchLog(@RequestParam String keywords) {
        try {
            String[] keywordArray = parseStringifiedArray(keywords);

            Resource resource = new ClassPathResource(LOG_FILE_NAME);
            try (InputStream inputStream = resource.getInputStream()) {
                String logContent = readInputStream(inputStream);

                Map<String, Long> keywordCounts = new HashMap<>();
                Arrays.stream(keywordArray)
                        .forEach(keyword -> {
                            long keywordCount = countOccurrences(logContent, keyword);
                            keywordCounts.put(keyword, keywordCount);
                        });

                return ResponseEntity.ok(keywordCounts);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private String[] parseStringifiedArray(String stringifiedArray) {
        return stringifiedArray.split(",");
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private long countOccurrences(String content, String keyword) {
        return Arrays.stream(content.split(keyword))
                .count() - 1;
    }
}
