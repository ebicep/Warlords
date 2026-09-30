package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DebuffDiscovery {

    public enum Category {
        DEBUFF(
                "Debuff",
                Material.FERMENTED_SPIDER_EYE,
                NamedTextColor.RED,
                "Removed by a standard cleanse."
        ),
        STRONG_DEBUFF(
                "Strong Debuff",
                Material.REDSTONE,
                CooldownTypes.HIGH_LEVEL_DEBUFF_COLOR,
                "A standard cleanse only shortens this. A strong cleanse removes it."
        ),
        TRUE_DEBUFF(
                "True Debuff",
                Material.WITHER_ROSE,
                CooldownTypes.TRUE_DEBUFF_COLOR,
                "Only a strong cleanse shortens this."
        ),
        OTHER(
                "Other",
                Material.PAPER,
                NamedTextColor.GRAY,
                "Not a cooldown-type debuff, so normal cleanses do not remove it."
        ),
        ;

        public static final Category[] VALUES = values();

        public final String displayName;
        public final Material icon;
        public final TextColor textColor;
        public final String cleanseNote;

        Category(String displayName, Material icon, TextColor textColor, String cleanseNote) {
            this.displayName = displayName;
            this.icon = icon;
            this.textColor = textColor;
            this.cleanseNote = cleanseNote;
        }
    }

    public record Entry(String name, String abbreviation, Category category, Material icon, String effect) {
    }

    private static final List<Entry> ENTRIES = List.of(
            entry("Crippling", "CRIP", Category.DEBUFF, Material.WOODEN_SWORD,
                    "Reduces the damage you deal. The amount depends on the source. Crippling Strike starts at 10% and grows with extra strikes. Enavuris reduces the damage you deal by 25%."),
            entry("Wounding", "WND", Category.DEBUFF, Material.RED_DYE,
                    "Reduces healing you receive. Only the strongest wound applies. Wounding Strike reduces healing by 40% or 25%, depending on the specialization. Decay and Disaster Fragment reduce it by 25%."),
            entry("Leech", "LCH", Category.DEBUFF, Material.GHAST_TEAR,
                    "A share of the damage you take heals the player who applied it. Stacks raise that share, and in PvE each stack heals at most 300. The action bar shows the stack count."),
            entry("Silence", "SILENCE", Category.DEBUFF, Material.COBWEB,
                    "You cannot use your melee attack. Trying to shows that you have been silenced. Applied by Soul Shackle and by some bosses, items, and events."),
            entry("Burn", "BRN", Category.DEBUFF, Material.BLAZE_POWDER,
                    "Deals 0.5% of your max health each second and makes you take 15% more damage. Aspect of the Infernal's burn makes you take 20% more damage instead."),
            entry("Ignite", "IGN", Category.DEBUFF, Material.FIRE_CHARGE,
                    "When it ends, your nearby allies take 450 to 650 true damage."),
            entry("Bleed", "BLEED", Category.DEBUFF, Material.REDSTONE,
                    "Deals 0.5% of your max health each second and reduces healing you receive to 20%."),
            entry("Aftershock", "SHOCKED", Category.DEBUFF, Material.LIGHTNING_ROD,
                    "You take 20% more damage from the player who shocked you. If they kill you, their orange abilities lose half a second of cooldown."),
            entry("Splintered Ice", "SPLINT", Category.DEBUFF, Material.PACKED_ICE,
                    "Each stack makes you take 6% more damage, up to 3 stacks, and slows you by 35% for 2 seconds."),
            entry("Chilled", "CHILLED", Category.DEBUFF, Material.SNOWBALL,
                    "You deal 40% less damage."),
            entry("Ice Wall", "WALL", Category.DEBUFF, Material.BLUE_ICE,
                    "Slows you by 50% and makes you take 35% more damage."),
            entry("Weakening Hex", "WHEX", Category.DEBUFF, Material.AMETHYST_SHARD,
                    "Each stack makes you take 5% more damage, up to 4 stacks."),
            entry("Liquidizing Miasma", "LIQ", Category.DEBUFF, Material.SLIME_BALL,
                    "Lasts until removed and reduces the damage you deal by 25%. When it is removed, nearby enemies take 1% of your max health as damage."),
            entry("Incendiary Curse", "INCEN", Category.DEBUFF, Material.MAGMA_CREAM,
                    "You take 30% more damage. The same cast blinds you, nauseates you, and stuns mobs."),
            entry("Hammer of Disillusion", "", Category.DEBUFF, Material.MACE,
                    "You take 15% more damage. It has no action bar abbreviation."),
            entry("Mirror Blossom", "", Category.DEBUFF, Material.PINK_PETALS,
                    "Each Mirror hit you take is counted. Each stack makes Judgement deal 5% more damage to you, up to double."),
            entry("Court of Spirits", "JUDGE", Category.DEBUFF, Material.SOUL_LANTERN,
                    "For 3 seconds, the damage you deal is judged. If you damage the caster, you take 1000 damage when it ends. If you damage their ally, you become bound. If you damage neither, you gain 50% speed for 2 seconds."),
            entry("Vengeful Army", "", Category.DEBUFF, Material.BONE,
                    "When it ends, if you are alive, you take 500 damage for each second it lasted plus 2% of your max health. It has no action bar abbreviation."),
            entry("KB Increase", "KB", Category.DEBUFF, Material.PISTON,
                    "A marker applied when Wonder Trap is detonated early. It does not change your knockback or damage."),
            entry("Bubble Silence", "BUBBLE DEBUFF", Category.DEBUFF, Material.GLASS,
                    "While you are silenced inside a Prism Guard bubble, you take 10% more damage."),
            entry("Earthliving", "", Category.DEBUFF, Material.BIRCH_SAPLING,
                    "You are stunned for its duration. When it ends, nearby allies of the caster are healed for 10% of their missing health and gain energy."),
            entry("Soul Feast", "FEAST", Category.DEBUFF, Material.SOUL_SAND,
                    "Lasts until removed. Each stack reduces the damage you deal by 2.5%, up to 25%."),
            entry("Shimmer", "SHM", Category.DEBUFF, Material.SLIME_BLOCK,
                    "Deals 4% of your max health each second."),
            entry("Blighted Scorch", "BLI", Category.DEBUFF, Material.FIRE_CORAL,
                    "Deals 5% of your max health each second."),
            entry("Chaos", "CHAOS", Category.DEBUFF, Material.ENDER_EYE,
                    "Blinds you and slows you by 20%."),
            entry("Wedge", "1/4", Category.DEBUFF, Material.STONE,
                    "Tracks keystone stacks. The action bar shows your stacks out of the amount needed to settle. At the last stack it deals 900 to 1200 damage, and the attacker is slowed by 60%."),
            entry("Poison", "POISON", Category.DEBUFF, Material.SPIDER_EYE,
                    "Deals damage over time. Apothecary poison deals 50 damage each second and slows you by 10%. Cave spider poison deals 5 damage each second, cannot kill you, and is shown as POI."),

            entry("Vulnerable", "VULN", Category.STRONG_DEBUFF, Material.GLASS_BOTTLE,
                    "You take 10% more damage."),
            entry("Draining Miasma", "MIAS", Category.STRONG_DEBUFF, Material.LIME_DYE,
                    "Deals 50 damage plus 3% of your max health each second, slows you by 25%, and leeches you."),
            entry("Avenger's Mark", "AVE MARK", Category.STRONG_DEBUFF, Material.YELLOW_DYE,
                    "The single-target mark drains 8 energy per second. PvE marks are a normal debuff: one makes you deal 10% less damage and take 40% more from Avenger's Strike, and the other makes you take 20% more damage."),
            entry("Nerfinator", "CHAIN", Category.STRONG_DEBUFF, Material.IRON_CHAIN,
                    "Each stack reduces the damage you deal by 12%, up to 3 stacks."),
            entry("Wounding Strike (Defender)", "", Category.STRONG_DEBUFF, Material.IRON_SWORD,
                    "Reduces the damage you deal by 15%. The same hit also wounds you. It has no action bar abbreviation."),
            entry("Silt Weakness", "SW", Category.STRONG_DEBUFF, Material.MUD,
                    "You deal 20% less damage."),

            entry("Marked for Death", "AVE MARK", Category.TRUE_DEBUFF, Material.WITHER_ROSE,
                    "Upgrades Avenger's Mark into a true debuff. It deals 100 damage, slows you by 10%, and each Avenger's Strike extends it by 1 second, up to 3 extra seconds."),
            entry("Rift Ambush", "TETHER", Category.TRUE_DEBUFF, Material.ENDER_PEARL,
                    "After Soul Switch, the target is trapped in a 2 block radius and pulled back if they leave it. The swap also deals 400 damage."),

            entry("Poisonous Hex", "PHEX", Category.OTHER, Material.POISONOUS_POTATO,
                    "Deals damage over time and stacks. At max stacks the name is shown in bold. It uses the strong debuff color, but it is an ability effect."),
            entry("Freezing Cold", "COLD", Category.OTHER, Material.POWDER_SNOW_BUCKET,
                    "Slows you by 80% and makes you take 15% more damage."),
            entry("Reckless Rampage", "RECK", Category.OTHER, Material.GOLDEN_SWORD,
                    "Strikes deal 25% more damage to you while it lasts."),
            entry("Stun", "", Category.OTHER, Material.IRON_BARS,
                    "You cannot move, and mobs stop acting. Some abilities show IMMOBILIZED. It is not a cooldown.")
    );

    static {
        Set<String> names = new HashSet<>();
        for (Entry entry : ENTRIES) {
            if (!names.add(entry.name())) {
                throw new IllegalStateException("Duplicate debuff catalog name: " + entry.name());
            }
        }
        for (Category category : Category.VALUES) {
            if (entries(category).isEmpty()) {
                throw new IllegalStateException("Debuff category has no entries: " + category.displayName);
            }
        }
    }

    private DebuffDiscovery() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }

    public static List<Entry> entries(Category category) {
        return ENTRIES.stream()
                      .filter(entry -> entry.category() == category)
                      .toList();
    }

    private static Entry entry(String name, String abbreviation, Category category, Material icon, String effect) {
        return new Entry(name, abbreviation, category, icon, effect);
    }
}
