package sk.blocky;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Údaje, ktoré sa opakujú na každej účtenke (obchod, DIČ, päta...).
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
            throw new IllegalArgumentException("Názov obchodu nesmie byť prázdny");
        }
        if (width < MIN_WIDTH) {
            throw new IllegalArgumentException("Šírka účtenky musí byť aspoň " + MIN_WIDTH + " znakov");
        }
        if (vatRate.signum() < 0) {
            throw new IllegalArgumentException("Sadzba DPH nesmie byť záporná");
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
