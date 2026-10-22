package sk.blocky;

import static sk.blocky.TestData.dec;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReceiptInputTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private ReceiptInput input(String text) {
        return new ReceiptInput(new BufferedReader(new StringReader(text)),
                new PrintStream(output, true, StandardCharsets.UTF_8), "€");
    }

    private String output() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void readsItemsUntilEmptyName() throws IOException {
        List<Item> items = input("Rohlík\n3\n2,50\nMlieko\n\n24.90\n\n").readItems();

        assertEquals(List.of(
                new Item("Rohlík", dec("3"), dec("2.50")),
                new Item("Mlieko", dec("1"), dec("24.90"))), items);
    }

    @Test
    void repeatsQuestionOnInvalidNumber() throws IOException {
        List<Item> items = input("Chlieb\nabc\n0\n-1\n2\nxx\n-5\n30\n\n").readItems();

        assertEquals(List.of(new Item("Chlieb", dec("2"), dec("30"))), items);
        assertTrue(output().contains("Neplatné číslo"));
        assertTrue(output().contains("musí byť väčšia ako 0"));
        assertTrue(output().contains("nesmie byť záporná"));
    }

    @Test
    void endOfInputStopsReading() throws IOException {
        assertEquals(List.of(), input("").readItems());
        assertEquals(List.of(), input("Rohlík\n2\n").readItems());
        assertEquals(1, input("Rohlík\n2\n3\n").readItems().size());
    }

    @Test
    void paymentDefaultsToTotal() throws IOException {
        assertEquals(dec("32.40"), input("\n").readPayment(dec("32.40")));
        assertEquals(dec("32.40"), input("").readPayment(dec("32.40")));
    }

    @Test
    void paymentMustCoverTotal() throws IOException {
        assertEquals(dec("100"), input("10\nabc\n100\n").readPayment(dec("32.40")));
        assertTrue(output().contains("nesmie byť menšia"));
        assertTrue(output().contains("Neplatné číslo"));
    }
}
