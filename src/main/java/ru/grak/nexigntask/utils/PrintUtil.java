package ru.grak.nexigntask.utils;

public class PrintUtil {
    public static void printTableHeader() {
        System.out.printf("Report for all msisdn and their in/out-coming calls for all tariff period. %n%n");
        System.out.println("|     MSISDN    | InComing | OutComing");
        System.out.println("-----------------------------------\n");
    }

    public static void printTableHeaderForEveryMonth(String msisdn) {
        System.out.printf("Report for %s and his in/out-coming calls for every month in tariff period. %n%n", msisdn);
        System.out.println("|Month|InComing | OutComing");
        System.out.println("-----------------------------------");
    }
    public static void printTableHeaderForOneMonth(String msisdn, Integer month) {
        System.out.printf("Report for %s and his in/out-coming calls in %d month. %n%n", msisdn, month);
        System.out.println("|Month|InComing | OutComing");
        System.out.println("-----------------------------------");
    }

    public static void formattedPrint(String... values) {
        for (String value : values) {
            System.out.printf("|  %s  ", value);
        }
        System.out.printf("%n");
    }
}
