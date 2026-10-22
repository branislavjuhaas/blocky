package cz.blocky;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Ukládá účtenky do textových souborů.
 */
public final class ReceiptWriter {

    private final Path directory;

    public ReceiptWriter(Path directory) {
        this.directory = directory;
    }

    /**
     * Uloží text účtenky do nového souboru a vrátí jeho absolutní cestu.
     * Existující soubory se nikdy nepřepisují.
     */
    public Path write(String receiptNumber, String content) throws IOException {
        Files.createDirectories(directory);
        for (int attempt = 0; ; attempt++) {
            String suffix = attempt == 0 ? "" : "-" + attempt;
            Path file = directory.resolve("uctenka-" + receiptNumber + suffix + ".txt");
            try {
                Files.writeString(file, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                return file.toAbsolutePath().normalize();
            } catch (FileAlreadyExistsException e) {
                // zkusíme další název
            }
        }
    }
}
