package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class FileManager {

    public static final Path ARCHIVE_DIR = Paths.get("archive");
    public static final Path ARCHIVE_REPORT_FILE =
            ARCHIVE_DIR.resolve("archive_report.txt");

    private static final Logger logger = AppLogger.getLogger();

    // Save an individual report
    public static void saveReport(String fileName, String reportContent) {
        try {
            Files.createDirectories(ARCHIVE_DIR);

            Path reportFile = ARCHIVE_DIR.resolve(fileName);

            Files.writeString(
                    reportFile,
                    reportContent,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            logger.info("Report saved to: " + reportFile.toAbsolutePath());

        } catch (Exception e) {
            logger.warning("Could not save report: " + e.getMessage());
        }
    }

    // Save the combined archive report
    public static void saveArchiveReport(List<String> reportLines) {
        try {
            Files.createDirectories(ARCHIVE_DIR);

            Files.write(
                    ARCHIVE_REPORT_FILE,
                    reportLines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            logger.info("Archive report saved to: "
                    + ARCHIVE_REPORT_FILE.toAbsolutePath());

        } catch (Exception e) {
            logger.warning("Could not save archive report: " + e.getMessage());
        }
    }
}