package sk.blocky;

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
        Files.writeString(dir.resolve("store.json"), """
                {"store": {"name": "Trafika U Testu"}, "outputDir": "bloky"}
                """);

        int code = run("Noviny\n2\n35\n\n100\n");

        assertEquals(0, code, err.toString());
        Path saved = dir.resolve("bloky/uctenka-20261022-170509.txt").toAbsolutePath().normalize();
        assertTrue(Files.exists(saved));
        String content = Files.readString(saved, StandardCharsets.UTF_8);
        assertTrue(content.contains("Trafika U Testu"));
        assertTrue(content.contains("70,00 €"));
        assertTrue(content.contains("30,00 €"));
        assertTrue(out().contains(content.strip()), "účtenka sa musí vypísať aj do konzoly");
        assertTrue(out().contains("Účtenka bola uložená do súboru: " + saved));
    }

    @Test
    void usesConfigFileFromArgument() throws IOException {
        Files.writeString(dir.resolve("jiny.json"), "{\"store\": {\"name\": \"Iný obchod\"}}", StandardCharsets.UTF_8);
        assertEquals(0, run("A\n1\n1\n\n\n", "jiny.json"));
        assertTrue(out().contains("Iný obchod"));
    }

    @Test
    void fallsBackToBundledConfig() {
        assertEquals(0, run("A\n1\n1\n\n\n"));
        assertTrue(out().contains("Potraviny Blocky"));
    }

    @Test
    void nothingIsSavedWithoutItems() throws IOException {
        assertEquals(0, run("\n"));
        assertTrue(out().contains("Nebola zadaná žiadna položka"));
        try (Stream<Path> files = Files.list(dir)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void reportsMissingConfigFile() {
        assertEquals(1, run("", "neexistuje.json"));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("Konfiguračný súbor sa nenašiel"));
    }

    @Test
    void printsVersionAndHelp() {
        assertEquals(0, run("", "--version"));
        assertTrue(out().startsWith("Blocky v"));
        assertEquals(0, run("", "--help"));
        assertTrue(out().contains("Použitie"));
    }

    @Test
    void rejectsTooManyArguments() {
        assertEquals(2, run("", "a", "b"));
    }
}
