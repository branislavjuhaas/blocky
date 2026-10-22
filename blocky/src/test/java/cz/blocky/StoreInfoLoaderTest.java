package cz.blocky;

import static cz.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StoreInfoLoaderTest {

    @TempDir
    Path dir;

    @Test
    void loadsAllValuesFromUtf8File() throws IOException {
        Path file = dir.resolve("store.properties");
        Files.writeString(file, """
                store.name=Večerka Žluťoučký kůň
                store.address=Náměstí 5, Brno
                store.ico=87654321
                store.dic=CZ87654321
                store.phone=+420 999 888 777
                receipt.footer=Díky!
                receipt.currency=EUR
                receipt.width=48
                vat.rate=12
                output.dir=out
                """, StandardCharsets.UTF_8);

        StoreInfo s = StoreInfoLoader.load(file);

        assertEquals("Večerka Žluťoučký kůň", s.name());
        assertEquals("Náměstí 5, Brno", s.address());
        assertEquals("87654321", s.ico());
        assertEquals("CZ87654321", s.dic());
        assertEquals("+420 999 888 777", s.phone());
        assertEquals("Díky!", s.footer());
        assertEquals("EUR", s.currency());
        assertEquals(48, s.width());
        assertEquals(dec("12"), s.vatRate());
        assertEquals("out", s.outputDir());
    }

    @Test
    void usesDefaultsForOptionalValues() throws IOException {
        StoreInfo s = StoreInfoLoader.load(new StringReader("store.name=Obchod"));
        assertEquals("", s.address());
        assertEquals("Kč", s.currency());
        assertEquals(40, s.width());
        assertEquals(dec("21"), s.vatRate());
        assertEquals("uctenky", s.outputDir());
    }

    @Test
    void acceptsDecimalCommaInVatRate() throws IOException {
        assertEquals(dec("10.5"), StoreInfoLoader.load(new StringReader("store.name=A\nvat.rate=10,5")).vatRate());
    }

    @Test
    void defaultConfigurationIsBundled() throws IOException {
        assertFalse(StoreInfoLoader.loadDefault().name().isBlank());
    }

    @Test
    void failsOnMissingFile() {
        assertThrows(IOException.class, () -> StoreInfoLoader.load(dir.resolve("neexistuje.properties")));
    }

    @Test
    void failsOnMissingName() {
        assertThrows(IOException.class, () -> StoreInfoLoader.load(new StringReader("store.address=X")));
    }

    @Test
    void failsOnInvalidNumbers() {
        assertThrows(IOException.class, () -> StoreInfoLoader.load(new StringReader("store.name=A\nreceipt.width=abc")));
        assertThrows(IOException.class, () -> StoreInfoLoader.load(new StringReader("store.name=A\nvat.rate=x")));
        assertThrows(IOException.class, () -> StoreInfoLoader.load(new StringReader("store.name=A\nreceipt.width=10")));
        assertThrows(IOException.class, () -> StoreInfoLoader.load(new StringReader("store.name=A\nvat.rate=-1")));
    }
}
