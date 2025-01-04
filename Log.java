import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static Log instance;
    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private Log() {
        // Private constructor for Singleton pattern
    }

    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public void addEntry(String entry) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String timestamp = LocalDateTime.now().format(dtf);
            out.println(timestamp + " - " + entry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public String getLog() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        return content.toString();
    }
}