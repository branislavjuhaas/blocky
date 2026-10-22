package sk.blocky;

import static sk.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StoreInfoLoaderTest {

    @TempDir
    Path dir;

    private static StoreInfo load(String json) throws IOException {
        return StoreInfoLoader.load(new StringReader(json));
    }

    @Test
    void loadsAllValuesFromUtf8File() throws IOException {
        Path file = dir.resolve("store.json");
        Files.writeString(file, """
                {
                  "store": {
                    "name": "Potraviny Ľalia Šťastná",
                    "address": "Námestie 5, Košice",
                    "ico": "87654321",
                    "dic": "CZ87654321",
                    "phone": "+420 999 888 777"
                  },
                  "receipt": {
                    "footer": "Vďaka!",
                    "currency": "EUR",
                    "width": 48
                  },
                  "vatRate": 12,
                  "outputDir": "out"
                }
                """, StandardCharsets.UTF_8);

        StoreInfo s = StoreInfoLoader.load(file);

        assertEquals("Potraviny Ľalia Šťastná", s.name());
        assertEquals("Námestie 5, Košice", s.address());
        assertEquals("87654321", s.ico());
        assertEquals("CZ87654321", s.dic());
        assertEquals("+420 999 888 777", s.phone());
        assertEquals("Vďaka!", s.footer());
        assertEquals("EUR", s.currency());
        assertEquals(48, s.width());
        assertEquals(dec("12"), s.vatRate());
        assertEquals("out", s.outputDir());
    }

    @Test
    void usesDefaultsForOptionalValues() throws IOException {
        StoreInfo s = load("{\"store\": {\"name\": \"Obchod\"}}");
        assertEquals("", s.address());
        assertEquals("€", s.currency());
        assertEquals(40, s.width());
        assertEquals(dec("23"), s.vatRate());
        assertEquals("uctenky", s.outputDir());
    }

    @Test
    void nullMeansDefault() throws IOException {
        StoreInfo s = load("{\"store\": {\"name\": \"A\", \"ico\": null}, \"receipt\": null, \"vatRate\": null}");
        assertEquals("", s.ico());
        assertEquals(40, s.width());
        assertEquals(dec("23"), s.vatRate());
    }

    @Test
    void acceptsDecimalVatRate() throws IOException {
        assertEquals(dec("10.5"), load("{\"store\": {\"name\": \"A\"}, \"vatRate\": 10.5}").vatRate());
    }

    @Test
    void defaultConfigurationIsBundled() throws IOException {
        assertFalse(StoreInfoLoader.loadDefault().name().isBlank());
    }

    @Test
    void bundledConfigurationMatchesExampleFile() throws IOException {
        assertEquals(StoreInfoLoader.loadDefault(), StoreInfoLoader.load(Path.of("store.json")));
    }

    @Test
    void failsOnMissingFile() {
        assertThrows(IOException.class, () -> StoreInfoLoader.load(dir.resolve("neexistuje.json")));
    }

    @Test
    void failsOnMissingName() {
        IOException e = assertThrows(IOException.class, () -> load("{\"store\": {\"address\": \"X\"}}"));
        assertTrue(e.getMessage().contains("store.name"));
        assertThrows(IOException.class, () -> load("{}"));
        assertThrows(IOException.class, () -> load("{\"store\": {\"name\": \"  \"}}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "[]",
            "\"text\"",
            "{\"store\": {\"name\": \"A\"}",
            "{store: {name: 'A'}}",
            "{\"store\": {\"name\": \"A\"},}",
            "{\"store\": \"A\"}",
            "{\"store\": {\"name\": 5}}",
            "{\"store\": {\"name\": \"A\"}, \"receipt\": {\"width\": \"40\"}}",
            "{\"store\": {\"name\": \"A\"}, \"receipt\": {\"width\": 40.5}}",
            "{\"store\": {\"name\": \"A\"}, \"receipt\": {\"width\": 10}}",
            "{\"store\": {\"name\": \"A\"}, \"vatRate\": -1}",
            "{\"store\": {\"name\": \"A\"}, \"vatRate\": \"21\"}",
            "{\"store\": {\"name\": \"A\"}, \"outputDir\": []}"
    })
    void failsOnInvalidConfiguration(String json) {
        assertThrows(IOException.class, () -> load(json));
    }
}
