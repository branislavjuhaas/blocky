package sk.blocky;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Prevody medzi číslami a textom v slovenskom formáte (desatinná čiarka).
 */
public final class Money {

    private Money() {
    }

    /** Naformátuje sumu na dve desatinné miesta, napr. 1 234,50. */
    public static String format(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        symbols.setMinusSign('-');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /** Naformátuje množstvo bez zbytočných núl, napr. 2 alebo 0,5. */
    public static String formatQuantity(BigDecimal quantity) {
        return quantity.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    /**
     * Načíta číslo zadané používateľom. Prijíma desatinnú čiarku aj bodku a medzery medzi tisícami.
     *
     * @throws NumberFormatException ak text nie je číslo
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
