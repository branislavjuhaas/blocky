package cz.blocky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppInfoTest {

    @Test
    void loadsNameAndSemanticVersionFromPom() {
        AppInfo info = AppInfo.load();
        assertEquals("Blocky", info.name());
        assertTrue(info.version().matches("\\d+\\.\\d+\\.\\d+(-[\\w.]+)?"), info.version());
    }

    @Test
    void toStringContainsNameAndVersion() {
        assertEquals("Blocky v1.2.3", new AppInfo("Blocky", "1.2.3").toString());
    }
}
