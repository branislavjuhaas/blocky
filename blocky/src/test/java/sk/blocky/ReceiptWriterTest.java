package sk.blocky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReceiptWriterTest {

    @TempDir
    Path dir;

    @Test
    void writesReceiptToNewDirectory() throws IOException {
        Path target = dir.resolve("a/b");
        Path file = new ReceiptWriter(target).write("20261022-170509", "Účtenka ✓");

        assertTrue(file.isAbsolute());
        assertEquals("uctenka-20261022-170509.txt", file.getFileName().toString());
        assertEquals("Účtenka ✓", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void neverOverwritesExistingReceipt() throws IOException {
        ReceiptWriter writer = new ReceiptWriter(dir);
        Path first = writer.write("1", "prvá");
        Path second = writer.write("1", "druhá");

        assertNotEquals(first, second);
        assertEquals("uctenka-1-1.txt", second.getFileName().toString());
        assertEquals("prvá", Files.readString(first));
        assertEquals("druhá", Files.readString(second));
    }
}
