package sk.blocky;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Jedna položka nákupu.
 */
public record Item(String name, BigDecimal quantity, BigDecimal unitPrice) {

    public Item {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(unitPrice, "unitPrice");
        name = name.strip();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Názov položky nesmie byť prázdny");
        }
        if (quantity.signum() <= 0) {
            throw new IllegalArgumentException("Množstvo musí byť kladné");
        }
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Cena nesmie byť záporná");
        }
    }

    /** Cena za položku spolu, zaokrúhlená na centy. */
    public BigDecimal total() {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
