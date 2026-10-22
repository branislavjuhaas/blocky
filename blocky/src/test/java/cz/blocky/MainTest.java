package cz.blocky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-22T15:05:09Z"), ZoneId.of("Europe/Prague"));

    @TempDir
    Path dir;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int run(String stdin, String... args) {
        return Main.run(args, new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8), CLOCK, dir);
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    void printsAndSavesReceipt() throws IOException {
        Files.writeString(dir.resolve("store.properties"), "store.name=Trafika U Testu\noutput.dir=bloky\n");

        int code = run("Noviny\n2\n35\n\n100\n");

        assertEquals(0, code, err.toString());
        Path saved = dir.resolve("bloky/uctenka-20261022-170509.txt").toAbsolutePath().normalize();
        assertTrue(Files.exists(saved));
        String content = Files.readString(saved, StandardCharsets.UTF_8);
        assertTrue(content.contains("Trafika U Testu"));
        assertTrue(content.contains("70,00 Kč"));
        assertTrue(content.contains("30,00 Kč"));
        assertTrue(out().contains(content.strip()), "účtenka se musí vypsat i do konzole");
        assertTrue(out().contains("Účtenka byla uložena do souboru: " + saved));
    }

    @Test
    void usesConfigFileFromArgument() throws IOException {
        Files.writeString(dir.resolve("jiny.properties"), "store.name=Jiný obchod\n");
        assertEquals(0, run("A\n1\n1\n\n\n", "jiny.properties"));
        assertTrue(out().contains("Jiný obchod"));
    }

    @Test
    void fallsBackToBundledConfig() {
        assertEquals(0, run("A\n1\n1\n\n\n"));
        assertTrue(out().contains("Potraviny Blocky"));
    }

    @Test
    void nothingIsSavedWithoutItems() throws IOException {
        assertEquals(0, run("\n"));
        assertTrue(out().contains("Nebyla zadána žádná položka"));
        try (Stream<Path> files = Files.list(dir)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void reportsMissingConfigFile() {
        assertEquals(1, run("", "neexistuje.properties"));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("Konfigurační soubor nenalezen"));
    }

    @Test
    void printsVersionAndHelp() {
        assertEquals(0, run("", "--version"));
        assertTrue(out().startsWith("Blocky v"));
        assertEquals(0, run("", "--help"));
        assertTrue(out().contains("Použití"));
    }

    @Test
    void rejectsTooManyArguments() {
        assertEquals(2, run("", "a", "b"));
    }
}
