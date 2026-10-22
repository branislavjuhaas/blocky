package sk.blocky;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Účtenka: obchod, položky, zaplatená suma a čas vystavenia.
 * Ceny sú vrátane DPH.
 */
public record Receipt(StoreInfo store, List<Item> items, BigDecimal paid, LocalDateTime issuedAt) {

    private static final DateTimeFormatter NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public Receipt {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(paid, "paid");
        Objects.requireNonNull(issuedAt, "issuedAt");
        items = List.copyOf(items);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Účtenka musí obsahovať aspoň jednu položku");
        }
        paid = paid.setScale(2, RoundingMode.HALF_UP);
        if (paid.compareTo(total(items)) < 0) {
            throw new IllegalArgumentException("Zaplatená suma je nižšia ako celková cena");
        }
    }

    public BigDecimal total() {
        return total(items);
    }

    public BigDecimal change() {
        return paid.subtract(total());
    }

    /** DPH obsiahnutá v celkovej cene: spolu * sadzba / (100 + sadzba). */
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
