package cz.blocky;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Převody mezi čísly a textem v českém formátu (desetinná čárka).
 */
public final class Money {

    private Money() {
    }

    /** Naformátuje částku na dvě desetinná místa, např. 1 234,50. */
    public static String format(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        symbols.setMinusSign('-');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /** Naformátuje množství bez zbytečných nul, např. 2 nebo 0,5. */
    public static String formatQuantity(BigDecimal quantity) {
        return quantity.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    /**
     * Načte číslo zadané uživatelem. Přijímá desetinnou čárku i tečku a mezery mezi tisíci.
     *
     * @throws NumberFormatException pokud text není číslo
     */
    public static BigDecimal parse(String text) {
        if (text == null) {
            throw new NumberFormatException("null");
        }
        String normalized = text.replace(" ", "").replace(" ", "").strip().replace(',', '.');
        if (!normalized.matches("[+-]?(\\d+(\\.\\d*)?|\\.\\d+)")) {
            throw new NumberFormatException("Neplatné číslo: " + text);
        }
        return new BigDecimal(normalized);
    }
}
