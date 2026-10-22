package cz.blocky;

import static cz.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

    @Test
    void formatsWithCommaAndTwoDecimals() {
        assertEquals("12,50", Money.format(dec("12.5")));
        assertEquals("0,00", Money.format(dec("0")));
        assertEquals("1 234 567,89", Money.format(dec("1234567.89")));
    }

    @Test
    void formatRoundsHalfUp() {
        assertEquals("1,01", Money.format(dec("1.005")));
    }

    @Test
    void formatsQuantityWithoutTrailingZeros() {
        assertEquals("2", Money.formatQuantity(dec("2.000")));
        assertEquals("0,5", Money.formatQuantity(dec("0.50")));
        assertEquals("100", Money.formatQuantity(dec("1E+2")));
    }

    @Test
    void parsesCommaDotAndSpaces() {
        assertEquals(dec("12.5"), Money.parse("12,5"));
        assertEquals(dec("12.5"), Money.parse(" 12.5 "));
        assertEquals(dec("1234.5"), Money.parse("1 234,5"));
        assertEquals(dec("1234"), Money.parse("1 234"));
        assertEquals(dec("0.5"), Money.parse(",5"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "1,2,3", "1e5", "12 Kč", "NaN", "-"})
    void rejectsInvalidNumbers(String input) {
        assertThrows(NumberFormatException.class, () -> Money.parse(input));
    }

    @Test
    void rejectsNull() {
        assertThrows(NumberFormatException.class, () -> Money.parse(null));
    }
}
