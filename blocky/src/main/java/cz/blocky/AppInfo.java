package cz.blocky;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Název a verze aplikace. Hodnoty doplňuje Maven z pom.xml při buildu.
 */
public record AppInfo(String name, String version) {

    private static final String RESOURCE = "/blocky-app.properties";

    public static AppInfo load() {
        Properties props = new Properties();
        try (InputStream in = AppInfo.class.getResourceAsStream(RESOURCE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            // bez souboru použijeme výchozí hodnoty
        }
        return new AppInfo(
                props.getProperty("app.name", "Blocky"),
                props.getProperty("app.version", "unknown"));
    }

    @Override
    public String toString() {
        return name + " v" + version;
    }
}
