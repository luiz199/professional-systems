package com.avatarworld.game;

import java.util.Arrays;
import java.util.List;

public class ChatFilter {
    private static ChatFilter instance;
    private List<String> blockedWords = Arrays.asList(
        "fuck", "shit", "ass", "damn", "bitch", "crap", "dick", "porn", "sex"
    );

    private ChatFilter() {}

    public static synchronized ChatFilter getInstance() {
        if (instance == null) instance = new ChatFilter();
        return instance;
    }

    public String filter(String message) {
        String filtered = message;
        for (String word : blockedWords) {
            String regex = "(?i)" + java.util.regex.Pattern.quote(word);
            filtered = filtered.replaceAll(regex, "***");
        }
        return filtered;
    }

    public boolean isAllowed(String message) {
        for (String word : blockedWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) return false;
        }
        return true;
    }
}
