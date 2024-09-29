package dev.ufo;

import dev.ufo.ufodata.core.io.UfoFile;
import dev.ufo.ufodata.lib.checks.CheckType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CheckTest {

    @Test
    public void testPrimitiveCheck() {
        assertFalse(CheckType.isNotPrimitive(Integer.class));

        assertFalse(CheckType.isNotPrimitive(int.class));

        assertTrue(CheckType.isNotPrimitive(UfoFile.class));
    }

    @Test
    public void testListOrMapCheck() {
        final List<String> list = new ArrayList<>();
        assertTrue(CheckType.isListOrMap(list.getClass()));

        final Map<String, Integer> map = new HashMap<>();
        assertTrue(CheckType.isListOrMap(map.getClass()));

        assertFalse(CheckType.isListOrMap(UfoFile.class));
    }
}
