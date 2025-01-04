public class Log {
    private StringBuilder log;

    public Log() {
        log = new StringBuilder();
    }

    public void addEntry(String entry) {
        log.append(entry).append("\n");
    }

    public String getLog() {
        return log.toString();
    }
}