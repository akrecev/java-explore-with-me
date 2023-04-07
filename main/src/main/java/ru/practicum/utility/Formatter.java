package ru.practicum.utility;

import java.time.format.DateTimeFormatter;

public class Formatter {
    private Formatter() {
        throw new IllegalStateException("Utility class");
    }

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
