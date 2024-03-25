package ru.grak.nexigntask.service;

import ru.grak.nexigntask.dao.TelecomDao;
import ru.grak.nexigntask.dto.CallDataRecord;
import ru.grak.nexigntask.enums.TypeCall;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static ru.grak.nexigntask.utils.RandomDataGenerator.*;

/**
 * Генератор данных о звонках (CDR - Call Data Records).
 */
public class CallDataGenerator {

    private static final int NUMBER_OF_MONTHS = 12;
    private static final int NUMBER_OF_ABONENTS = 10;
    private static final int MAX_CALLS_PER_MONTH = 10;
    private static final int MAX_CALL_DURATION_MINUTES = 60;
    private static final int REPORT_YEAR = 2023;

    private static final String CDR_FOLDER_PATH = "cdr/";
    private static final String CDR_FILE_PREFIX = "cdr";
    private static final String CDR_FILE_EXTENSION = ".txt";

    /**
     * Создает CDR файлы для всех месяцев.
     *
     * @throws IOException  Если возникает ошибка ввода/вывода при создании файлов.
     * @throws SQLException Если возникает ошибка при взаимодействии с базой данных.
     */
    public static void createCdr() throws IOException, SQLException {

        ReportService.createDirectory(CDR_FOLDER_PATH);

        Connection connection = DbConnectionManager.getConnection();
        TelecomDao.createTables(connection, NUMBER_OF_ABONENTS);

        for (int month = 1; month <= NUMBER_OF_MONTHS; month++) {
            createCdrForMonth(month);
        }

        DbConnectionManager.closeConnection();
    }

    /**
     * Создает CDR файл для указанного месяца.
     *
     * @param month Номер месяца.
     * @throws IOException  Если возникает ошибка ввода/вывода при создании файла.
     * @throws SQLException Если возникает ошибка при взаимодействии с базой данных.
     */
    public static void createCdrForMonth(int month) throws IOException, SQLException {

        String cdrFileName = CDR_FOLDER_PATH + CDR_FILE_PREFIX + "_" + month + CDR_FILE_EXTENSION;

        List<CallDataRecord> chronologicalCdrList = generateCdrListForMonth(month);

        try (PrintWriter writer = new PrintWriter(new FileWriter(cdrFileName))) {

            for (CallDataRecord callDataRecord : chronologicalCdrList) {
                writer.println(cdrFormat(callDataRecord));
                TelecomDao.saveTransaction(DbConnectionManager.getConnection(), callDataRecord);
            }
        }
    }

    /**
     * Генерирует рандомный список данных о звонках для указанного месяца в хронологическом порядке.
     *
     * @param month Номер месяца.
     * @return Список объектов CallDataRecord.
     * @throws SQLException Если возникает ошибка при взаимодействии с базой данных.
     */
    public static List<CallDataRecord> generateCdrListForMonth(int month) throws SQLException {
        List<CallDataRecord> callDataRecordList = new ArrayList<>();

        List<String> msisdnList = TelecomDao.getMsisdnList(DbConnectionManager.getConnection());
        for (String msisdn : msisdnList) {

            int numberOfCalls = ThreadLocalRandom.current().nextInt(MAX_CALLS_PER_MONTH);
            for (int i = 0; i < numberOfCalls; i++) {

                long startDateTime = generateRandomDateTime(REPORT_YEAR, month);
                long endDateTime = startDateTime + generateRandomCallDuration(MAX_CALL_DURATION_MINUTES);
                TypeCall typeCall = ThreadLocalRandom.current().nextBoolean()
                        ? TypeCall.OUTGOING
                        : TypeCall.INCOMING;

                callDataRecordList.add(new CallDataRecord(
                        typeCall,
                        String.valueOf(msisdn),
                        startDateTime,
                        endDateTime
                ));
            }
        }

        return callDataRecordList
                .stream()
                .sorted(Comparator.comparing(CallDataRecord::getDateTimeStartCall))
                .collect(Collectors.toList());
    }

    /**
     * Форматирует данные о звонке в строку в формате CDR.
     *
     * @param dataRecord Объект CallDataRecord.
     * @return Строка в формате CDR.
     */
    private static String cdrFormat(CallDataRecord dataRecord) {
        return dataRecord.getTypeCall().getNumericValueOfType() + ", "
                + dataRecord.getMsisdn() + ", "
                + dataRecord.getDateTimeStartCall() + ", "
                + dataRecord.getDateTimeEndCall();
    }
}
