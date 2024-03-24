package ru.grak.nexigntask.service;

public class CDRGenerator {

    private static final String DB_URL = "jdbc:h2:./cdrdb";
    private static final String DB_USERNAME = "username";
    private static final String DB_PASSWORD = "password";

    private static final int NUMBER_OF_MONTHS = 12;
//    private static final int NUMBER_OF_SUBSCRIBERS = 10;
    private static final int MAX_CALLS_PER_MONTH = 10;
    private static final int MAX_CALL_DURATION_MINUTES = 60;

    private static final String CDR_FILE_PREFIX = "cdr";
    private static final String CDR_FILE_EXTENSION = ".txt";

    private long generateRandomTime(int month){
        return 1L;
    }

    private String generateCDRRecord(){
        return "";
    }

//    private static void generateCDRFile(int month) throws IOException, SQLException {
//        String cdrFileName = CDR_FILE_PREFIX + month + CDR_FILE_EXTENSION;
//        try (PrintWriter writer = new PrintWriter(new FileWriter(cdrFileName))) {
//            // Для каждого абонента генерируем случайное количество и длительность звонков
//            for (int subscriberId = 1; subscriberId <= NUMBER_OF_SUBSCRIBERS; subscriberId++) {
//                int numberOfCalls = random.nextInt(MAX_CALLS_PER_MONTH) + 1; // Генерируем случайное количество звонков
//                for (int i = 0; i < numberOfCalls; i++) {
//                    long startTime = generateRandomTime(month);
//                    long endTime = startTime + generateRandomDuration();
//                    writer.println(generateCDRRecord(subscriberId, startTime, endTime));
//                    saveTransaction(subscriberId, new Timestamp(startTime), new Timestamp(endTime));
//                }
//            }
//        }
//    }
//
//    private int generateRandomDuration(int month){
//        return 1;
//    }
//
//    private static void saveTransaction(int subscriberId, Timestamp startTime, Timestamp endTime) throws SQLException {
//        // Сохраняем данные о транзакции в базу данных
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
//            PreparedStatement stmt = conn.prepareStatement("INSERT INTO transactions (subscriber_id, start_time, end_time) VALUES (?, ?, ?)");
//            stmt.setInt(1, subscriberId);
//            stmt.setTimestamp(2, startTime);
//            stmt.setTimestamp(3, endTime);
//            stmt.executeUpdate();
//        }
//    }
//
//    private static void createTables(Connection conn) throws SQLException {
//        // Создаем таблицу абонентов
//        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS subscribers (id INT AUTO_INCREMENT PRIMARY KEY, number VARCHAR(20))");
//
//        // Создаем таблицу транзакций
//        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, subscriber_id INT, start_time TIMESTAMP, end_time TIMESTAMP)");
//
//        // Добавляем тестовых абонентов
//        for (int i = 1; i <= NUMBER_OF_SUBSCRIBERS; i++) {
//            PreparedStatement stmt = conn.prepareStatement("INSERT INTO subscribers (number) VALUES (?)");
//            stmt.setString(1, "Subscriber " + i);
//            stmt.executeUpdate();
//        }
//    }
//
//    private static String generatePhoneNumber() {
//        StringBuilder phoneNumber = new StringBuilder("7");
//        for (int i = 0; i < 10; i++) {
//            phoneNumber.append(random.nextInt(10));
//        }
//        return phoneNumber.toString();
//    }


}
