package ru.grak.nexigntask.dao;

import ru.grak.nexigntask.dto.CallDataRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ru.grak.nexigntask.utils.RandomDataGenerator.generatePhoneNumber;

public class TelecomDao {

    public static void createTables(Connection conn, int numberOfAbonents) throws SQLException {

        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS abonents (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, msisdn VARCHAR(11))");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, call_type VARCHAR(2), msisdn VARCHAR(11), start_time BIGINT, end_time BIGINT)");

        // Добавляем тестовых абонентов
        for (int i = 0; i < numberOfAbonents; i++) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO abonents (msisdn) VALUES (?)");
            stmt.setString(1, generatePhoneNumber());
            stmt.executeUpdate();
        }
    }

    public static void saveTransaction(Connection conn, CallDataRecord dataRecord) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO transactions (call_type, msisdn, start_time, end_time) VALUES (?, ?, ?, ?)");
        stmt.setString(1, dataRecord.getTypeCall().getNumericValueOfType());
        stmt.setString(2, dataRecord.getMsisdn());
        stmt.setLong(3, dataRecord.getDateTimeStartCall());
        stmt.setLong(4, dataRecord.getDateTimeEndCall());
        stmt.executeUpdate();
    }

    public static List<String> getMsisdnList(Connection conn) throws SQLException {

        List<String> msisdnList = new ArrayList<>();

        Statement st = conn.createStatement();
        ResultSet resultSet = st.executeQuery("SELECT * FROM abonents");

        while (resultSet.next()){
            msisdnList.add(resultSet.getString("msisdn"));
        }

        return msisdnList;
    }
}
