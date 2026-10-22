package cz.blocky;

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
            throw new IllegalArgumentException("Název položky nesmí být prázdný");
        }
        if (quantity.signum() <= 0) {
            throw new IllegalArgumentException("Množství musí být kladné");
        }
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Cena nesmí být záporná");
        }
    }

    /** Cena za položku celkem, zaokrouhlená na haléře. */
    public BigDecimal total() {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
