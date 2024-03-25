package ru.grak.nexigntask.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {

    /**
     * Генерирует случайную продолжительность звонка в секундах.
     *
     * @param maxCallDurationInMinutes Максимальная продолжительность звонка в минутах.
     * @return Продолжительность звонка в секундах.
     */
    public static int generateRandomCallDuration(int maxCallDurationInMinutes) {
        return ThreadLocalRandom.current().nextInt(maxCallDurationInMinutes * 60) + 1;
    }

    /**
     * Генерирует случайную дату и время в указанном месяце и году.
     *
     * @param year  Год.
     * @param month Месяц.
     * @return Дата и время в формате Unix time.
     */
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

    /**
     * Преобразует LocalDateTime в Unix time.
     *
     * @param dateTime Объект LocalDateTime.
     * @return Unix time.
     */
    public static long convertToUnixTime(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * Генерирует случайный номер телефона.
     *
     * @return Случайный номер телефона.
     */
    public static String generatePhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("7");
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(ThreadLocalRandom.current().nextInt(10));
        }
        return phoneNumber.toString();
    }
}
