package ru.grak.nexigntask.service;

import ru.grak.nexigntask.dto.CallDataRecord;
import ru.grak.nexigntask.enums.TypeCall;
import ru.grak.nexigntask.exceptions.FileReadingException;
import ru.grak.nexigntask.exceptions.AbonentNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.grak.nexigntask.utils.PrintUtil.*;

public class ReportService {

    private static final String CDR_FOLDER_PATH = "cdr_files/";
    private static final String REPORT_FOLDER_PATH = "reports/";
    private static final String REPORT_FILE_EXTENSION = ".json";

    public static void main(String[] args) {
        generateReport();
        generateReport("79876543221");
        generateReport("79876543221", 3);
    }

    private static List<CallDataRecord> getListFromCdrFile() {

        List<CallDataRecord> callDataRecordList = new ArrayList<>();

        var cdrFilePath = Paths.get("cdr.txt");
        try (Stream<String> stream = Files.lines(cdrFilePath)) {
            stream.forEach(l -> {
                var cdr = l.split(", ");

                callDataRecordList.add(new CallDataRecord(
                        TypeCall.fromNumericValueOfType(cdr[0]),
                        cdr[1],
                        Long.parseLong(cdr[2]),
                        Long.parseLong(cdr[3])
                ));
            });

        } catch (IOException e) {
            throw new FileReadingException("Ошибка чтения файла");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new FileReadingException("Неверный формат задания данных");
        }

        return callDataRecordList;
    }

    private static Map<String, Map<Integer, List<CallDataRecord>>> aggregateDataByMsisdnAndMonth
            (List<CallDataRecord> callDataRecordList) {

        return callDataRecordList.stream()
                .collect(Collectors.groupingBy(CallDataRecord::getMsisdn,
                        Collectors.groupingBy(t -> extractMonth(t.getDateTimeStartCall()))));
    }

    //  таблицу со всеми абонентами и итоговым временем звонков по всему тарифицируемому периоду каждого абонента;
    public static void generateReport() {

        List<CallDataRecord> callDataRecordList = getListFromCdrFile();

        var aggregatedMap = callDataRecordList
                .stream()
                .collect(Collectors.groupingBy(CallDataRecord::getMsisdn));

        printTableHeader();

        for (Map.Entry<String, List<CallDataRecord>> unit : aggregatedMap.entrySet()) {
            String msisdn = unit.getKey();
            long inComingCall = 0;
            long outComingCall = 0;

            for (CallDataRecord dataRecord : unit.getValue()) {
                long duration = dataRecord.getDateTimeEndCall() - dataRecord.getDateTimeStartCall();
                if (dataRecord.getTypeCall().equals(TypeCall.OUTGOING)) {
                    outComingCall += duration;
                } else {
                    inComingCall += duration;
                }
            }

            formattedPrint(msisdn, formatDuration(inComingCall), formatDuration(outComingCall));
        }
    }

    //  таблицу по одному абоненту и его итоговому времени звонков в каждом месяце;
    public static void generateReport(String msisdn) {
        List<CallDataRecord> callDataRecordList = getListFromCdrFile();

        Map<String, Map<Integer, List<CallDataRecord>>> aggregatedMap = aggregateDataByMsisdnAndMonth(callDataRecordList);

        if (!aggregatedMap.containsKey(msisdn))
            throw new AbonentNotFoundException("Данного абонента нет в базе");

        Map<Integer, List<CallDataRecord>> recordsForOneMsisdn = aggregatedMap.get(msisdn);

        printTableHeaderForEveryMonth(msisdn);

        for (int month = 1; month <= 12; month++) {
            long inComingCall = 0;
            long outComingCall = 0;

            if (recordsForOneMsisdn.containsKey(month)) {
                for (CallDataRecord dataRecord : recordsForOneMsisdn.get(month)) {
                    long duration = dataRecord.getDateTimeEndCall() - dataRecord.getDateTimeStartCall();
                    if (dataRecord.getTypeCall().equals(TypeCall.OUTGOING)) {
                        outComingCall += duration;
                    } else {
                        inComingCall += duration;
                    }
                }
            }
            formattedPrint(String.valueOf(month), formatDuration(inComingCall), formatDuration(outComingCall));
        }
    }

//  таблицу по одному абоненту и его итоговому времени звонков в указанном месяце.
    public static void generateReport(String msisdn, int month) {

        List<CallDataRecord> callDataRecordList = getListFromCdrFile();

        Map<String, Map<Integer, List<CallDataRecord>>> aggregatedMap = aggregateDataByMsisdnAndMonth(callDataRecordList);

        if (!aggregatedMap.containsKey(msisdn))
            throw new AbonentNotFoundException("Данного абонента нет в базе");

        Map<Integer, List<CallDataRecord>> recordsForOneMsisdn = aggregatedMap.get(msisdn);

        long inComingCall = 0;
        long outComingCall = 0;

        printTableHeaderForOneMonth(msisdn, month);
        if (recordsForOneMsisdn.containsKey(month)) {
            for (CallDataRecord dataRecord : recordsForOneMsisdn.get(month)) {
                long duration = dataRecord.getDateTimeEndCall() - dataRecord.getDateTimeStartCall();
                if (dataRecord.getTypeCall().equals(TypeCall.OUTGOING)) {
                    outComingCall += duration;
                } else {
                    inComingCall += duration;
                }
            }
        }

        formattedPrint(String.valueOf(month), formatDuration(inComingCall), formatDuration(outComingCall));
    }

    private static int extractMonth(long dateTime) {
        return LocalDate.
                ofInstant(Instant.ofEpochSecond(dateTime), ZoneOffset.UTC)
                .getMonthValue();
    }

    private static String formatDuration(long duration) {
        Duration d = Duration.ofSeconds(duration);
        return String.format("%d:%02d:%02d",
                d.toHours(),
                d.toMinutes() % 60,
                d.toSeconds() % 60);
    }


    private void generateUDR() {

    }

//    private void generateReportForMonth(int month) {
//        try {
//            Files.walk(Paths.get(CDR_FOLDER_PATH))
//                    .filter(Files::isRegularFile)
//                    .forEach(file -> {
//                        try {
//                            String cdrFileName = file.getFileName().toString();
//                            String msisdn = cdrFileName.substring(0, cdrFileName.indexOf('_'));
//                            generateReportForMonth(msisdn, month);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generateReportForMonth(String msisdn, int month) {
//        try {
//            String cdrFileName = CDR_FOLDER_PATH + msisdn + "_" + month + ".txt";
//            Files.lines(Paths.get(cdrFileName))
//                    .map(line -> line.split(","))
//                    .forEach(parts -> {
//                        int callDuration = Integer.parseInt(parts[4]) - Integer.parseInt(parts[3]);
//                        callTimes.computeIfAbsent(msisdn, k -> new HashMap<>())
//                                .merge(month, callDuration, Integer::sum);
//                    });
//            saveReport(msisdn, month);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void saveUDR() {
//        callDurations.forEach((phoneNumber, callStats) -> {
//            JSONObject udr = new JSONObject();
//            JSONObject incomingCall = new JSONObject();
//            JSONObject outgoingCall = new JSONObject();
//
//            incomingCall.put("totalTime", formatDuration(callStats.getOrDefault("incomingCall", 0)));
//            outgoingCall.put("totalTime", formatDuration(callStats.getOrDefault("outgoingCall", 0)));
//
//            udr.put("msisdn", phoneNumber);
//            udr.put("incomingCall", incomingCall);
//            udr.put("outgoingCall", outgoingCall);
//
//            String udrFileName = REPORT_FOLDER_PATH + phoneNumber + UDR_FILE_EXTENSION;
//            try (FileWriter fileWriter = new FileWriter(udrFileName)) {
//                fileWriter.write(udr.toString(4));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private void printReportTable() {
//        System.out.println("MSISDN\t\tMonth\t\tTotal Call Duration (minutes)");
//        callTimes.forEach((msisdn, monthMap) -> {
//            monthMap.forEach((month, duration) ->
//                    System.out.println(msisdn + "\t\t" + month + "\t\t" + duration));
//        });
//    }


}
