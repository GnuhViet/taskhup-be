package com.taskhub.project.common;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonFunction {
    public static boolean isTheSameList(List<String> list1, List<String> list2) {
        var set1 = new HashSet<>(list1);
        var set2 = new HashSet<>(list1);

        return set1.equals(set2);
    }


    public static boolean isSameOrder(List list1, List list2) {
        var minLength = Math.min(list1.size(), list2.size());

        list1 = list1.subList(0, minLength);
        list2 = list2.subList(0, minLength);

        return list1.equals(list2);
    }

    private static final String SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 15;
    private static final Random RANDOM = new Random();

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(SOURCE.charAt(RANDOM.nextInt(SOURCE.length())));
        }
        return sb.toString();
    }

    public static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
