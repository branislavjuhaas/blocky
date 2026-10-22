package cz.blocky;

import static cz.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReceiptFormatterTest {

    private final ReceiptFormatter formatter = new ReceiptFormatter(new AppInfo("Blocky", "1.0.0"));

    private Receipt receipt(List<Item> items, String paid) {
        return new Receipt(TestData.store(), items, dec(paid), LocalDateTime.of(2026, 10, 22, 17, 5, 9));
    }

    @Test
    void containsAllImportantParts() {
        String text = formatter.format(receipt(List.of(
                new Item("Rohlík", dec("3"), dec("2.50")),
                new Item("Mléko", dec("1"), dec("24.90"))), "50"));

        assertTrue(text.contains("Potraviny Test"));
        assertTrue(text.contains("IČO: 12345678  DIČ: CZ12345678"));
        assertTrue(text.contains("Účtenka č.: 20261022-170509"));
        assertTrue(text.contains("22.10.2026 17:05:09"));
        assertTrue(text.contains("Rohlík"));
        assertTrue(text.contains("3 x 2,50"));
        assertTrue(text.contains("CELKEM"));
        assertTrue(text.contains("32,40 Kč"));
        assertTrue(text.contains("17,60 Kč"));
        assertTrue(text.contains("DPH 21 %"));
        assertTrue(text.contains("Děkujeme za nákup!"));
        assertTrue(text.contains("Blocky v1.0.0"));
    }

    @Test
    void noLineIsWiderThanReceipt() {
        String text = formatter.format(receipt(List.of(
                new Item("Extrémně dlouhý název položky, který se na účtenku nevejde", dec("1000"), dec("99999.99"))),
                "100000000"));
        for (String line : text.split("\\R")) {
            assertTrue(line.length() <= 40, "Příliš dlouhý řádek: '" + line + "'");
        }
    }

    @Test
    void amountsAreRightAligned() {
        String text = formatter.format(receipt(List.of(new Item("A", dec("1"), dec("5"))), "5"));
        String totalLine = text.lines().filter(l -> l.startsWith("CELKEM")).findFirst().orElseThrow();
        assertEquals(40, totalLine.length());
        assertTrue(totalLine.endsWith("5,00 Kč"));
    }

    @Test
    void helperFunctions() {
        assertEquals("  ab", ReceiptFormatter.center("ab", 6));
        assertEquals("abc…", ReceiptFormatter.truncate("abcdef", 4));
        assertEquals("abc", ReceiptFormatter.truncate("abc", 4));
        assertEquals("a    1", ReceiptFormatter.leftRight("a", "1", 6));
        assertEquals("abc… 12", ReceiptFormatter.leftRight("abcdefgh", "12", 7));
    }
}
