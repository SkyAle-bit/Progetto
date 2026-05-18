package com.project.tesi.util;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Utilities {

    private Utilities() {}

    public static String getTimeAgo(LocalDateTime dateTime) {
        long minutes = java.time.Duration.between(dateTime, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "adesso";
        if (minutes < 60) return minutes + " min fa";
        long hours = minutes / 60;
        if (hours < 24) return hours + " or" + (hours == 1 ? "a" : "e") + " fa";
        long days = hours / 24;
        if (days == 1) return "ieri";
        if (days < 7) return days + " giorni fa";
        long weeks = days / 7;
        return weeks + " settiman" + (weeks == 1 ? "a" : "e") + " fa";
    }

    public static String generateJitsiLink() {
        return "https://meet.jit.si/kore-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
