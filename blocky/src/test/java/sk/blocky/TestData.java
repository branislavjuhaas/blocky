package sk.blocky;

import java.math.BigDecimal;

final class TestData {

    private TestData() {
    }

    static StoreInfo store() {
        return new StoreInfo("Potraviny Test", "Testovacia 1, Bratislava", "12345678", "2020123456",
                "+421 900 111 222", "Ďakujeme za nákup!", "€", 40, new BigDecimal("23"), "uctenky");
    }

    static BigDecimal dec(String value) {
        return new BigDecimal(value);
    }
}
