package sk.blocky;

import static sk.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReceiptTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2026, 10, 22, 17, 5, 9);

    private final List<Item> items = List.of(
            new Item("Rohlík", dec("3"), dec("2.50")),
            new Item("Mlieko", dec("1"), dec("24.90")));

    @Test
    void computesTotalAndChange() {
        Receipt r = new Receipt(TestData.store(), items, dec("50"), TIME);
        assertEquals(dec("32.40"), r.total());
        assertEquals(dec("50.00"), r.paid());
        assertEquals(dec("17.60"), r.change());
    }

    @Test
    void exactPaymentGivesZeroChange() {
        Receipt r = new Receipt(TestData.store(), items, dec("32.4"), TIME);
        assertEquals(0, r.change().signum());
    }

    @Test
    void computesVatIncludedInPrice() {
        Receipt r = new Receipt(TestData.store(), List.of(new Item("A", dec("1"), dec("123"))), dec("123"), TIME);
        assertEquals(dec("23.00"), r.vat());
        assertEquals(dec("100.00"), r.totalWithoutVat());
    }

    @Test
    void zeroVatRate() {
        StoreInfo s = TestData.store();
        StoreInfo noVat = new StoreInfo(s.name(), s.address(), s.ico(), s.dic(), s.phone(), s.footer(),
                s.currency(), s.width(), BigDecimal.ZERO, s.outputDir());
        Receipt r = new Receipt(noVat, items, dec("50"), TIME);
        assertEquals(0, r.vat().signum());
        assertEquals(r.total(), r.totalWithoutVat());
    }

    @Test
    void numberIsDerivedFromTime() {
        assertEquals("20261022-170509", new Receipt(TestData.store(), items, dec("50"), TIME).number());
    }

    @Test
    void itemsAreCopied() {
        List<Item> mutable = new ArrayList<>(items);
        Receipt r = new Receipt(TestData.store(), mutable, dec("50"), TIME);
        mutable.clear();
        assertEquals(2, r.items().size());
    }

    @Test
    void rejectsEmptyItemsAndUnderpayment() {
        assertThrows(IllegalArgumentException.class, () -> new Receipt(TestData.store(), List.of(), dec("1"), TIME));
        assertThrows(IllegalArgumentException.class, () -> new Receipt(TestData.store(), items, dec("32.39"), TIME));
    }
}
