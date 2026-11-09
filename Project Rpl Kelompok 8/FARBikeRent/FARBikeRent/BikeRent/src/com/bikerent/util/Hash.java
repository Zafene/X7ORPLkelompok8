package com.bikerent.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hash {

    private Hash() {}

    public static String sha256(String teks) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hasil = md.digest(teks.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hasil) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
