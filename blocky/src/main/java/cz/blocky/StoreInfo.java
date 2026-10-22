package cz.blocky;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Údaje, které se opakují na každé účtence (obchod, DIČ, patička...).
 */
public record StoreInfo(
        String name,
        String address,
        String ico,
        String dic,
        String phone,
        String footer,
        String currency,
        int width,
        BigDecimal vatRate,
        String outputDir) {

    public static final int MIN_WIDTH = 30;

    public StoreInfo {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Název obchodu nesmí být prázdný");
        }
        if (width < MIN_WIDTH) {
            throw new IllegalArgumentException("Šířka účtenky musí být alespoň " + MIN_WIDTH + " znaků");
        }
        if (vatRate.signum() < 0) {
            throw new IllegalArgumentException("Sazba DPH nesmí být záporná");
        }
        address = Objects.requireNonNullElse(address, "");
        ico = Objects.requireNonNullElse(ico, "");
        dic = Objects.requireNonNullElse(dic, "");
        phone = Objects.requireNonNullElse(phone, "");
        footer = Objects.requireNonNullElse(footer, "");
        currency = Objects.requireNonNullElse(currency, "");
        outputDir = outputDir == null || outputDir.isBlank() ? "uctenky" : outputDir;
    }
}
