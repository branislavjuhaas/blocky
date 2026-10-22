package cz.blocky;

import java.math.BigDecimal;

final class TestData {

    private TestData() {
    }

    static StoreInfo store() {
        return new StoreInfo("Potraviny Test", "Testovací 1, Praha", "12345678", "CZ12345678",
                "+420 111 222 333", "Děkujeme za nákup!", "Kč", 40, new BigDecimal("21"), "uctenky");
    }

    static BigDecimal dec(String value) {
        return new BigDecimal(value);
    }
}
