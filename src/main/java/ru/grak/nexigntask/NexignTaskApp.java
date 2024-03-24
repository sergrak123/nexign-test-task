package ru.grak.nexigntask;

import ru.grak.nexigntask.service.CallDataGenerator;
import ru.grak.nexigntask.service.ReportService;

import java.io.IOException;
import java.sql.SQLException;

public class NexignTaskApp {
    public static void main(String[] args) {

        try {
            CallDataGenerator.createCdr();
            ReportService.generateReport();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Completion of CDR and UDR generation");
    }
}
