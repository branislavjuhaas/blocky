package cz.blocky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interaktivně načítá položky nákupu a platbu od uživatele.
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
     * Načítá položky, dokud uživatel nezadá prázdný název (nebo neskončí vstup).
     */
    public List<Item> readItems() throws IOException {
        List<Item> items = new ArrayList<>();
        out.println("Zadávejte položky nákupu. Prázdný název ukončí zadávání.");
        while (true) {
            out.print("Název položky " + (items.size() + 1) + ": ");
            out.flush();
            String name = in.readLine();
            if (name == null || name.isBlank()) {
                return items;
            }
            Optional<BigDecimal> quantity = readNumber("  Množství [1]: ", BigDecimal.ONE, true);
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
     * Načte zaplacenou částku. Prázdný vstup znamená, že se platí přesně.
     */
    public BigDecimal readPayment(BigDecimal total) throws IOException {
        out.println("Celkem k úhradě: " + Money.format(total) + " " + currency);
        while (true) {
            out.print("Zaplaceno (" + currency + ") [" + Money.format(total) + "]: ");
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
                out.println("  Zaplacená částka nesmí být menší než " + Money.format(total) + " " + currency + ".");
            } catch (NumberFormatException e) {
                out.println("  Neplatné číslo, zkuste to znovu.");
            }
        }
    }

    /**
     * Opakovaně se ptá na číslo, dokud není platné. Prázdné vrátí výchozí hodnotu (pokud existuje).
     * Prázdný Optional znamená konec vstupu.
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
                    out.println("  Hodnota musí být větší než 0.");
                } else if (value.signum() < 0) {
                    out.println("  Hodnota nesmí být záporná.");
                } else {
                    return Optional.of(value);
                }
            } catch (NumberFormatException e) {
                out.println("  Neplatné číslo, zkuste to znovu.");
            }
        }
    }
}
