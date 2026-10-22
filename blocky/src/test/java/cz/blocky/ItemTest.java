package cz.blocky;

import static cz.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void totalIsQuantityTimesPrice() {
        assertEquals(dec("7.50"), new Item("Rohlík", dec("3"), dec("2.50")).total());
    }

    @Test
    void totalIsRoundedToHundredths() {
        assertEquals(dec("16.62"), new Item("Sýr", dec("0.333"), dec("49.90")).total());
        assertEquals(dec("0.03"), new Item("Bonbon", dec("0.5"), dec("0.05")).total());
    }

    @Test
    void freeItemIsAllowed() {
        assertEquals(dec("0.00"), new Item("Taška", dec("1"), dec("0")).total());
    }

    @Test
    void nameIsTrimmed() {
        assertEquals("Mléko", new Item("  Mléko ", dec("1"), dec("20")).name());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new Item(" ", dec("1"), dec("1")));
        assertThrows(IllegalArgumentException.class, () -> new Item("A", dec("0"), dec("1")));
        assertThrows(IllegalArgumentException.class, () -> new Item("A", dec("-1"), dec("1")));
        assertThrows(IllegalArgumentException.class, () -> new Item("A", dec("1"), dec("-0.01")));
        assertThrows(NullPointerException.class, () -> new Item(null, dec("1"), dec("1")));
    }
}
