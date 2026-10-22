package sk.blocky;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Prevedie {@link Receipt} na text, ktorý vyzerá ako účtenka z pokladnice.
 */
public final class ReceiptFormatter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final AppInfo appInfo;

    public ReceiptFormatter(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public String format(Receipt receipt) {
        StoreInfo store = receipt.store();
        int width = store.width();
        String cur = store.currency().isEmpty() ? "" : " " + store.currency();
        List<String> lines = new ArrayList<>();

        lines.add("=".repeat(width));
        lines.add(center(store.name(), width));
        addCentered(lines, store.address(), width);
        String ids = join("IČO: ", store.ico(), "DIČ: ", store.dic());
        addCentered(lines, ids, width);
        addCentered(lines, store.phone().isEmpty() ? "" : "Tel.: " + store.phone(), width);
        lines.add("=".repeat(width));
        lines.add("Účtenka č.: " + receipt.number());
        lines.add("Dátum:      " + receipt.issuedAt().format(DATE_FORMAT));
        lines.add("-".repeat(width));

        for (Item item : receipt.items()) {
            lines.add(truncate(item.name(), width));
            String detail = "  " + Money.formatQuantity(item.quantity()) + " x " + Money.format(item.unitPrice());
            lines.add(leftRight(detail, Money.format(item.total()), width));
        }

        lines.add("-".repeat(width));
        lines.add(leftRight("SPOLU", Money.format(receipt.total()) + cur, width));
        lines.add("-".repeat(width));
        lines.add(leftRight("Zaplatené", Money.format(receipt.paid()) + cur, width));
        lines.add(leftRight("Vrátené", Money.format(receipt.change()) + cur, width));
        lines.add("-".repeat(width));
        String rate = Money.formatQuantity(store.vatRate()) + " %";
        lines.add(leftRight("Základ DPH " + rate, Money.format(receipt.totalWithoutVat()) + cur, width));
        lines.add(leftRight("DPH " + rate, Money.format(receipt.vat()) + cur, width));
        lines.add("=".repeat(width));
        addCentered(lines, store.footer(), width);
        lines.add(center(appInfo.toString(), width));

        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    private static void addCentered(List<String> lines, String text, int width) {
        if (!text.isEmpty()) {
            lines.add(center(text, width));
        }
    }

    private static String join(String label1, String value1, String label2, String value2) {
        List<String> parts = new ArrayList<>();
        if (!value1.isEmpty()) {
            parts.add(label1 + value1);
        }
        if (!value2.isEmpty()) {
            parts.add(label2 + value2);
        }
        return String.join("  ", parts);
    }

    static String center(String text, int width) {
        String t = truncate(text, width);
        int padding = (width - t.length()) / 2;
        return " ".repeat(padding) + t;
    }

    /** Text vľavo, suma zarovnaná vpravo. Suma sa nikdy neoreže. */
    static String leftRight(String left, String right, int width) {
        int maxLeft = Math.max(0, width - right.length() - 1);
        String l = truncate(left, maxLeft);
        int spaces = Math.max(1, width - l.length() - right.length());
        return l + " ".repeat(spaces) + right;
    }

    static String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        if (max <= 1) {
            return text.substring(0, max);
        }
        return text.substring(0, max - 1) + "…";
    }
}
