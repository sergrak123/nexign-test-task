import org.junit.Test;
import ru.grak.nexigntask.dto.CallDataRecord;
import ru.grak.nexigntask.service.CallDataGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestClass {

    @Test
    public void testCreateCdrForMonth() throws IOException, SQLException {
        int testMonth = 1;

        CallDataGenerator.createCdrForMonth(testMonth);

        assertTrue(Files.exists(Paths.get("cdr/cdr_1.txt")));
    }

    @Test
    public void testGenerateCdrListForMonth() throws SQLException {
        int testMonth = 1;

        List<CallDataRecord> cdrList = CallDataGenerator.generateCdrListForMonth(testMonth);

        assertFalse(cdrList.isEmpty());

        // Проверка на то, что список cdr упорядочен по времени начала звонка
        boolean isSorted = true;
        for (int i = 0; i < cdrList.size() - 1; i++) {
            if (cdrList.get(i).getDateTimeStartCall() > cdrList.get(i + 1).getDateTimeStartCall()) {
                isSorted = false;
                break;
            }
        }
        assertTrue(isSorted);
    }
}
