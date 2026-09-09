package com.ebicep.warlords.player.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecializationStatsLoreTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @Test
    void specializationStatsMatchesClassInformationLayout() {
        List<String> lore = toPlain(SpecializationStatsLore.specializationStats(7200, 355, 22.5f, 18, 10, 5));

        assertEquals(
                List.of(
                        "",
                        "Specialization Stats:",
                        "",
                        "Health: 7200",
                        "",
                        "Energy: 355 / +22.5 per sec / +18 per hit",
                        "Damage Reduction: 10%",
                        "Speed: 5%"
                ),
                lore
        );
    }

    @Test
    void specializationStatsRendersMissingPercentagesAsNone() {
        List<String> lore = toPlain(SpecializationStatsLore.specializationStats(5200, 305, 20, 14, 0, 0));

        assertTrue(lore.contains("Damage Reduction: None"));
        assertTrue(lore.contains("Speed: None"));
        assertFalse(lore.contains("Item Stats:"));
    }

    @Test
    void itemStatsAreOmittedWhenAllBonusesAreZero() {
        assertTrue(SpecializationStatsLore.itemStats(SpecializationStatsLore.ItemBonuses.none()).isEmpty());
    }

    @Test
    void itemStatsIncludeThornsAndCooldownReduction() {
        SpecializationStatsLore.ItemBonuses bonuses = new SpecializationStatsLore.ItemBonuses(
                12.5f,
                5,
                15,
                500,
                8,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
        List<String> lore = toPlain(SpecializationStatsLore.itemStats(bonuses));

        assertEquals("Item Stats:", lore.get(0));
        assertEquals("", lore.get(1));
        assertTrue(lore.contains("Cooldown Reduction: 12.5%"));
        assertTrue(lore.contains("Skill Energy Cost Reduction: 5"));
        assertTrue(lore.contains("Thorns: 15% (max 500)"));
        assertTrue(lore.contains("Damage: 8%"));
        assertFalse(lore.contains("Healing:"));
        assertFalse(lore.contains("Crit Chance:"));
    }

    private static List<String> toPlain(List<Component> components) {
        return components.stream().map(PLAIN::serialize).collect(Collectors.toList());
    }

}
