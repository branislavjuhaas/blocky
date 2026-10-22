package sk.blocky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Vstupný bod programu Blocky.
 *
 * <pre>
 * java -jar blocky.jar [cesta/k/store.json]
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.in, System.out, System.err, Clock.systemDefaultZone(), Path.of(""));
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, InputStream stdin, PrintStream out, PrintStream err, Clock clock, Path workDir) {
        AppInfo app = AppInfo.load();

        if (args.length > 0 && (args[0].equals("--version") || args[0].equals("-v"))) {
            out.println(app);
            return 0;
        }
        if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h"))) {
            printHelp(app, out);
            return 0;
        }
        if (args.length > 1) {
            err.println("Priveľa argumentov. Použite --help.");
            return 2;
        }

        StoreInfo store;
        try {
            store = loadStore(args, workDir);
        } catch (IOException e) {
            err.println("Chyba konfigurace: " + e.getMessage());
            return 1;
        }

        out.println(app + " – tlač účteniek");
        out.println();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin, stdinCharset()));
        ReceiptInput input = new ReceiptInput(reader, out, store.currency());
        try {
            List<Item> items = input.readItems();
            if (items.isEmpty()) {
                out.println("Nebola zadaná žiadna položka, účtenka sa nevytlačí.");
                return 0;
            }
            BigDecimal total = items.stream().map(Item::total).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paid = input.readPayment(total);

            LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
            Receipt receipt = new Receipt(store, items, paid, now);
            String text = new ReceiptFormatter(app).format(receipt);

            out.println();
            out.print(text);
            out.println();

            Path file = new ReceiptWriter(workDir.resolve(store.outputDir())).write(receipt.number(), text);
            out.println("Účtenka bola uložená do súboru: " + file);
            return 0;
        } catch (IOException e) {
            err.println("Chyba pri práci so súbormi: " + e.getMessage());
            return 1;
        }
    }

    private static StoreInfo loadStore(String[] args, Path workDir) throws IOException {
        if (args.length == 1) {
            return StoreInfoLoader.load(workDir.resolve(args[0]));
        }
        Path local = workDir.resolve(StoreInfoLoader.DEFAULT_FILE);
        if (Files.isRegularFile(local)) {
            return StoreInfoLoader.load(local);
        }
        return StoreInfoLoader.loadDefault();
    }

    private static Charset stdinCharset() {
        String name = System.getProperty("stdin.encoding");
        try {
            return name != null ? Charset.forName(name) : Charset.defaultCharset();
        } catch (IllegalArgumentException e) {
            return Charset.defaultCharset();
        }
    }

    private static void printHelp(AppInfo app, PrintStream out) {
        out.println(app);
        out.println("Použitie: java -jar blocky.jar [konfiguracia.json]");
        out.println();
        out.println("  Bez argumentu sa použije ./store.json, prípadne predvolená konfigurácia v JAR.");
        out.println("  -v, --version   vypíše verziu");
        out.println("  -h, --help      vypíše túto nápovedu");
    }
}
