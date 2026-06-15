package com.rmh.itemmagnet.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MessagesConfig {

    private final String prefix;
    private final Map<String, String> messages;

    public MessagesConfig(String prefix, Map<String, String> messages) {
        this.prefix = prefix;
        this.messages = Collections.unmodifiableMap(new HashMap<>(messages));
    }

    public String getPrefix() {
        return prefix;
    }

    public String get(String key) {
        return messages.getOrDefault(key, key);
    }

    public String format(String key, Map<String, String> placeholders) {
        String template = get(key);
        Map<String, String> all = new HashMap<>(placeholders);
        all.putIfAbsent("prefix", prefix);
        for (Map.Entry<String, String> entry : all.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    public List<String> getOrderedLines(String keyPrefix) {
        List<String> lines = new ArrayList<>();
        for (int index = 1; index <= 10; index++) {
            String key = keyPrefix + index;
            if (!messages.containsKey(key)) {
                break;
            }
            String line = messages.get(key);
            if (line != null && !line.isBlank()) {
                lines.add(line);
            }
        }
        return lines;
    }
}
