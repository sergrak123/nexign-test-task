package ru.grak.nexigntask.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {

    public static int generateRandomCallDuration(int maxCallDurationInMinutes) {
        return ThreadLocalRandom.current().nextInt(maxCallDurationInMinutes * 60) + 1;
    }

    public static long generateRandomDateTime(int year, int month) {
        var dateTime = LocalDateTime.of(
                year,
                month,
                ThreadLocalRandom.current().nextInt(28) + 1,
                ThreadLocalRandom.current().nextInt(24),
                ThreadLocalRandom.current().nextInt(60),
                ThreadLocalRandom.current().nextInt(60));

        return convertToUnixTime(dateTime);
    }

    public static long convertToUnixTime(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static String generatePhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("7");
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(ThreadLocalRandom.current().nextInt(10));
        }
        return phoneNumber.toString();
    }
}
