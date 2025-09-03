package com.harshith.dsa_question_picker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class Utility {
    public static ObjectMapper objectMapper;

    @Autowired
    public static MongoTemplate mongoTemplate;

    public static boolean isValidUrl(String url) {
        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
