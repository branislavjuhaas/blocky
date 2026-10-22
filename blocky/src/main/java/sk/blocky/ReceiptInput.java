package sk.blocky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interaktívne načítava položky nákupu a platbu od používateľa.
 */
public final class ReceiptInput {

    private final BufferedReader in;
    private final PrintStream out;
    private final String currency;

    public ReceiptInput(BufferedReader in, PrintStream out, String currency) {
        this.in = in;
        this.out = out;
        this.currency = currency;
    }

    /**
     * Načítava položky, kým používateľ nezadá prázdny názov (alebo neskončí vstup).
     */
    public List<Item> readItems() throws IOException {
        List<Item> items = new ArrayList<>();
        out.println("Zadávajte položky nákupu. Prázdny názov ukončí zadávanie.");
        while (true) {
            out.print("Názov položky " + (items.size() + 1) + ": ");
            out.flush();
            String name = in.readLine();
            if (name == null || name.isBlank()) {
                return items;
            }
            Optional<BigDecimal> quantity = readNumber("  Množstvo [1]: ", BigDecimal.ONE, true);
            if (quantity.isEmpty()) {
                return items;
            }
            Optional<BigDecimal> price = readNumber("  Cena za kus (" + currency + "): ", null, false);
            if (price.isEmpty()) {
                return items;
            }
            Item item = new Item(name, quantity.get(), price.get());
            items.add(item);
            out.println("  + " + item.name() + " = " + Money.format(item.total()) + " " + currency);
        }
    }

    /**
     * Načíta zaplatenú sumu. Prázdny vstup znamená, že sa platí presne.
     */
    public BigDecimal readPayment(BigDecimal total) throws IOException {
        out.println("Spolu na úhradu: " + Money.format(total) + " " + currency);
        while (true) {
            out.print("Zaplatené (" + currency + ") [" + Money.format(total) + "]: ");
            out.flush();
            String line = in.readLine();
            if (line == null || line.isBlank()) {
                return total;
            }
            try {
                BigDecimal paid = Money.parse(line);
                if (paid.compareTo(total) >= 0) {
                    return paid;
                }
                out.println("  Zaplatená suma nesmie byť menšia ako " + Money.format(total) + " " + currency + ".");
            } catch (NumberFormatException e) {
                out.println("  Neplatné číslo, skúste to znova.");
            }
        }
    }

    /**
     * Opakovane sa pýta na číslo, kým nie je platné. Prázdne vráti predvolenú hodnotu (ak existuje).
     * Prázdny Optional znamená koniec vstupu.
     */
    private Optional<BigDecimal> readNumber(String prompt, BigDecimal defaultValue, boolean positive) throws IOException {
        while (true) {
            out.print(prompt);
            out.flush();
            String line = in.readLine();
            if (line == null) {
                return Optional.empty();
            }
            if (line.isBlank() && defaultValue != null) {
                return Optional.of(defaultValue);
            }
            try {
                BigDecimal value = Money.parse(line);
                if (positive && value.signum() <= 0) {
                    out.println("  Hodnota musí byť väčšia ako 0.");
                } else if (value.signum() < 0) {
                    out.println("  Hodnota nesmie byť záporná.");
                } else {
                    return Optional.of(value);
                }
            } catch (NumberFormatException e) {
                out.println("  Neplatné číslo, skúste to znova.");
            }
        }
    }
}
