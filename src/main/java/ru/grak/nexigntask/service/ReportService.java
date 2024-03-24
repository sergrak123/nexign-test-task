package ru.grak.nexigntask.service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.grak.nexigntask.dto.CallDataRecord;
import ru.grak.nexigntask.enums.TypeCall;
import ru.grak.nexigntask.exceptions.FileReadingException;
import ru.grak.nexigntask.exceptions.AbonentNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.grak.nexigntask.utils.PrintUtil.*;

public class ReportService {

    private static final String REPORT_FOLDER_PATH = "reports/";
    private static final String CDR_FOLDER_PATH = "cdr/";
    private static final String REPORT_FILE_EXTENSION = ".json";

    private static List<CallDataRecord> getListFromCdrFile() throws IOException {

        List<CallDataRecord> callDataRecordList = new ArrayList<>();

        List<Path> cdrFiles = Files.list(Path.of(CDR_FOLDER_PATH)).toList();

        for (Path cdrFile : cdrFiles) {
            var cdrFilePath = Paths.get(CDR_FOLDER_PATH + cdrFile.getFileName());
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
        }

        return callDataRecordList;
    }

    private static Map<String, Map<Integer, List<CallDataRecord>>> aggregateDataByMsisdnAndMonth
            (List<CallDataRecord> callDataRecordList) {

        return callDataRecordList.stream()
                .collect(Collectors.groupingBy(CallDataRecord::getMsisdn,
                        Collectors.groupingBy(t -> extractMonth(t.getDateTimeStartCall()))));
    }

    private static void generateUDR(List<CallDataRecord> callDataRecordList) {

        Map<String, Map<Integer, List<CallDataRecord>>> aggregatedMap =
                aggregateDataByMsisdnAndMonth(callDataRecordList);

        createDirectory(REPORT_FOLDER_PATH);

        for (var unit : aggregatedMap.entrySet()) {
            String msisdn = unit.getKey();

            for (int month = 1; month <= 12; month++) {
                long inComingCall = 0;
                long outComingCall = 0;

                if (unit.getValue().containsKey(month)) {
                    for (CallDataRecord dataRecord : unit.getValue().get(month)) {
                        long duration = dataRecord.getDateTimeEndCall() - dataRecord.getDateTimeStartCall();
                        if (dataRecord.getTypeCall().equals(TypeCall.OUTGOING)) {
                            outComingCall += duration;
                        } else {
                            inComingCall += duration;
                        }
                    }
                }
                saveUdrRecord(msisdn, month,formatDuration(inComingCall), formatDuration(outComingCall));
            }
        }
    }

    private static void saveUdrRecord(String msisdn, int month, String incomingCall, String outcomingCall) {

        String reportFileName = REPORT_FOLDER_PATH + msisdn + "_" + month + REPORT_FILE_EXTENSION;

        Map<String, Object> udrMap = new LinkedHashMap<>();
        udrMap.put("msisdn", msisdn);
        udrMap.put("incomingCall", Collections.singletonMap("totalTime", incomingCall));
        udrMap.put("outgoingCall", Collections.singletonMap("totalTime", outcomingCall));

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        try {
            writer.writeValue(Paths.get(reportFileName).toFile(), udrMap);
        } catch (IOException e) {
            System.err.println("Ошибка при записи udr: " + e.getMessage());
        }
    }

    //  таблицу со всеми абонентами и итоговым временем звонков по всему тарифицируемому периоду каждого абонента;
    public static void generateReport() throws IOException {

        List<CallDataRecord> callDataRecordList = getListFromCdrFile();
        generateUDR(callDataRecordList);

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
    public static void generateReport(String msisdn) throws IOException {
        List<CallDataRecord> callDataRecordList = getListFromCdrFile();
        generateUDR(callDataRecordList);

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
    public static void generateReport(String msisdn, int month) throws IOException {

        List<CallDataRecord> callDataRecordList = getListFromCdrFile();
        generateUDR(callDataRecordList);

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

    public static void createDirectory(String path) {
        new File(path).mkdirs();
    }
}
