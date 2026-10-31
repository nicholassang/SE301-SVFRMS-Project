package org.example.report;

import org.example.logging.AppLogger;
import java.util.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class TextReportExporter implements ReportExporter {

    private static final Path ARCHIVE_DIR = Paths.get("archive");

    private static final Logger logger = AppLogger.getLogger();

    @Override
    public void export(String fileName, String content) {
        try {
            Files.createDirectories(ARCHIVE_DIR);

            Path reportFile = ARCHIVE_DIR.resolve(fileName);

            Files.writeString(
                    reportFile,
                    content,
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
}
