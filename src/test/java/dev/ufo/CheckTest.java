package dev.ufo;

import dev.ufo.ufodata.core.io.UfoFile;
import dev.ufo.ufodata.lib.checks.CheckType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CheckTest {

    @Test
    public void testTypeChecks() {
        assertTrue(CheckType.isPrimitive(Integer.class));
        assertTrue(CheckType.isPrimitive(int.class));
        assertFalse(CheckType.isPrimitive(UfoFile.class));
    }
}
