package com.origin.urlshortener.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {
    
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(BASE62.length);
            sb.append(BASE62[index]);
        }

        return sb.toString();
    }
}
