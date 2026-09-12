package com.ebicep.warlords.tablist;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabLayoutEngineTest {

    private static final UUID VIEWER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final int ROWS = 20;
    private static final int COLS = 4;

    @Test
    void secondSubgroupFillsRemainingCellsOfPartialColumn() {
        TabGroup group = new TabGroup("main", 0, 0, 2, 40);
        TabSubgroup first = new TabSubgroup(0);
        TabSubgroup second = new TabSubgroup(1);
        first.addAll(nSources(25, "a"));
        second.addAll(nSources(15, "b"));
        group.addSubgroup(first).addSubgroup(second);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(group), VIEWER, ROWS, COLS);

        assertEquals(40, result.size());
        // Column 0: a0..a19, column 1: a20..a24 then b0..b14
        assertEquals("a0", logical(result, 0));
        assertEquals("a19", logical(result, 19));
        assertEquals("a20", logical(result, 20));
        assertEquals("a24", logical(result, 24));
        assertEquals("b0", logical(result, 25));
        assertEquals("b14", logical(result, 39));
    }

    @Test
    void maxColumnsClipsLowerPrioritySubgroup() {
        TabGroup group = new TabGroup("main", 0, 0, 1, 40);
        TabSubgroup first = new TabSubgroup(0);
        TabSubgroup second = new TabSubgroup(1);
        first.addAll(nSources(20, "a"));
        second.addAll(nSources(5, "b"));
        group.addSubgroup(first).addSubgroup(second);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(group), VIEWER, ROWS, COLS);

        assertEquals(20, result.size());
        assertTrue(result.entries().stream().allMatch(e -> e.entry().logicalId().startsWith("a")));
    }

    @Test
    void maxEntriesClipsEvenWhenColumnsRemain() {
        TabGroup group = new TabGroup("main", 0, 0, 2, 10);
        TabSubgroup first = new TabSubgroup(0);
        first.addAll(nSources(25, "a"));
        group.addSubgroup(first);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(group), VIEWER, ROWS, COLS);

        assertEquals(10, result.size());
        assertEquals("a9", logical(result, 9));
    }

    @Test
    void lowerPriorityGroupClippedWhenColumnsExhausted() {
        // Reserve all 4 columns for the high-priority group even if only 3 are filled
        TabGroup high = new TabGroup("high", 0, 4, 4, 60);
        TabGroup low = new TabGroup("low", 1, 0, 2, 40);
        TabSubgroup highSub = new TabSubgroup(0);
        TabSubgroup lowSub = new TabSubgroup(0);
        highSub.addAll(nSources(60, "h"));
        lowSub.addAll(nSources(10, "l"));
        high.addSubgroup(highSub);
        low.addSubgroup(lowSub);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(high, low), VIEWER, ROWS, COLS);

        assertEquals(60, result.size());
        assertTrue(result.entries().stream().allMatch(e -> e.entry().logicalId().startsWith("h")));
    }

    @Test
    void minColumnsReservesBandForNextGroup() {
        TabGroup first = new TabGroup("first", 0, 2, 2, 5);
        TabGroup second = new TabGroup("second", 1, 0, 2, 20);
        TabSubgroup a = new TabSubgroup(0);
        TabSubgroup b = new TabSubgroup(0);
        a.addAll(nSources(5, "a"));
        b.addAll(nSources(3, "b"));
        first.addSubgroup(a);
        second.addSubgroup(b);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(first, second), VIEWER, ROWS, COLS);

        assertEquals(8, result.size());
        // first uses col 0 (5 entries), minColumns=2 reserves col 0-1; second starts at col 2
        assertEquals(0, result.entries().get(0).slotIndex());
        assertEquals(2 * ROWS, result.entries().get(5).slotIndex());
        assertEquals("b0", logicalAtSlot(result, 2 * ROWS));
    }

    @Test
    void nullSourcesDoNotConsumeSlots() {
        TabGroup group = new TabGroup("main", 0, 0, 1, 20);
        TabSubgroup sub = new TabSubgroup(0);
        sub.add(v -> null);
        sub.add(v -> TabEntry.of(Component.text("x")).withLogicalId("x"));
        sub.add(v -> null);
        group.addSubgroup(sub);

        TabLayoutEngine.LayoutResult result = TabLayoutEngine.pack(List.of(group), VIEWER, ROWS, COLS);

        assertEquals(1, result.size());
        assertEquals("x", logical(result, 0));
    }

    private static List<TabEntrySource> nSources(int n, String prefix) {
        List<TabEntrySource> list = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String id = prefix + i;
            list.add(v -> TabEntry.of(Component.text(id)).withLogicalId(id));
        }
        return list;
    }

    private static String logical(TabLayoutEngine.LayoutResult result, int orderedIndex) {
        return result.entries().get(orderedIndex).entry().logicalId();
    }

    private static String logicalAtSlot(TabLayoutEngine.LayoutResult result, int slotIndex) {
        return result.entries().stream()
                .filter(e -> e.slotIndex() == slotIndex)
                .findFirst()
                .orElseThrow()
                .entry()
                .logicalId();
    }
}
