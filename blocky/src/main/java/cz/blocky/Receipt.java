package cz.blocky;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Účtenka: obchod, položky, zaplacená částka a čas vystavení.
 * Ceny jsou včetně DPH.
 */
public record Receipt(StoreInfo store, List<Item> items, BigDecimal paid, LocalDateTime issuedAt) {

    private static final DateTimeFormatter NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public Receipt {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(paid, "paid");
        Objects.requireNonNull(issuedAt, "issuedAt");
        items = List.copyOf(items);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Účtenka musí obsahovat alespoň jednu položku");
        }
        paid = paid.setScale(2, RoundingMode.HALF_UP);
        if (paid.compareTo(total(items)) < 0) {
            throw new IllegalArgumentException("Zaplacená částka je nižší než celková cena");
        }
    }

    public BigDecimal total() {
        return total(items);
    }

    public BigDecimal change() {
        return paid.subtract(total());
    }

    /** DPH obsažená v celkové ceně: celkem * sazba / (100 + sazba). */
    public BigDecimal vat() {
        BigDecimal rate = store.vatRate();
        return total().multiply(rate)
                .divide(BigDecimal.valueOf(100).add(rate), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalWithoutVat() {
        return total().subtract(vat());
    }

    public String number() {
        return issuedAt.format(NUMBER_FORMAT);
    }

    private static BigDecimal total(List<Item> items) {
        return items.stream().map(Item::total).reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }
}
