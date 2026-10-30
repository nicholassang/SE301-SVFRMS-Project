package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class FileManager {

    private static final Logger logger = AppLogger.getLogger();

    public static void saveReport(List<String> reportLines) {
        try {
            Files.createDirectories(SVFRMS.ARCHIVE_DIR);

            Files.write(
                SVFRMS.REPORT_FILE,
                reportLines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );

            logger.info("Report saved to: "
                    + SVFRMS.REPORT_FILE.toAbsolutePath());

        } catch (Exception e) {
            logger.warning("Could not save report: " + e.getMessage());
        }
    }
}