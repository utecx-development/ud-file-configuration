package dev.ufo;

import dev.ufo.ufodata.core.io.UfoFile;
import dev.ufo.ufodata.lib.checks.CheckType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CheckTest {

    @Test
    public void testTypeChecks() {
        assertFalse(CheckType.isNotPrimitive(Integer.class));
        assertFalse(CheckType.isNotPrimitive(int.class));
        assertTrue(CheckType.isNotPrimitive(UfoFile.class));
    }
}
