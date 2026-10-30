package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class FileManager {
    public static final Path ARCHIVE_DIR = Paths.get("archive");
    public static final Path REPORT_FILE = ARCHIVE_DIR.resolve("daily_report.txt");

    private static final Logger logger = AppLogger.getLogger();

    public static void saveReport(List<String> reportLines) {
        try {
            Files.createDirectories(ARCHIVE_DIR);
            Files.write(
                REPORT_FILE,
                reportLines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
            logger.info("Report saved to: " + REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            logger.warning("Could not save report: " + e.getMessage());
        }
    }
}