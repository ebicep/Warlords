package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.gems.GemTier;
import com.ebicep.warlords.pve.newitems.gems.GemType;

import java.util.LinkedHashMap;

public enum Raid {

    REGNUM_OF_TWO_CROWNS(
            "Regnum of Two Crowns",
            "Conquer the Heir of Two Crowns.",
            70,
            GemTier.ONE
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 200_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 400_000L);
                put(GuildSpendable.GUILD_COIN, 15_000L);
                put(Currencies.ASCENDANT_SHARD, 20L);
                put(Currencies.LEGEND_FRAGMENTS, 10000L);
                put(Currencies.ETHEREUM_CRYSTAL, 10L);
                put(Currencies.ASCENDANT_STAR_PIECE, 3L);
                put(Currencies.LIMIT_BREAKER, 3L);
                put(Currencies.ITEM_LOCK_SCROLL, 1L);
            }};
        }
    },
    OATH_OF_THE_FIRST_HEIR(
            "Oath of the First Heir",
            "PLACEHOLDER",
            75,
            GemTier.ONE
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    THE_EVERGREEN_MANSION(
            "The Evergreen Mansion",
            "PLACEHOLDER",
            80,
            GemTier.TWO
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    SHADOWS_OF_THE_UNDERGROUND(
            "Shadows of the Underground",
            "PLACEHOLDER",
            85,
            GemTier.TWO
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    THE_STAIRWAY_OF_ILLUSION(
            "The Stairway of Illusion",
            "PLACEHOLDER",
            90,
            GemTier.THREE
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    THE_HALLS_OF_ASCENSION(
            "The Halls of Ascension",
            "PLACEHOLDER",
            95,
            GemTier.THREE
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    THE_FINAL_VEIL(
            "The Final Veil",
            "PLACEHOLDER",
            100,
            GemTier.FOUR
    ) {
        @Override
        protected LinkedHashMap<Spendable, Long> createNormalRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }

        @Override
        protected LinkedHashMap<Spendable, Long> createOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 300_000L);
                put(GuildSpendable.GUILD_COIN, 10_000L);
                put(Currencies.LEGEND_FRAGMENTS, 3000L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },

    ;

    public static final Raid[] VALUES = values();

    private static void putGems(LinkedHashMap<Spendable, Long> rewards, GemTier tier, long amount) {
        for (GemType type : GemType.VALUES) {
            rewards.merge(Gem.of(type, tier), amount, Long::sum);
        }
    }

    private final String name;
    private final String description;
    private final int minimumClassLevel;
    private final GemTier gemTier;

    Raid(String name, String description, int minimumClassLevel, GemTier gemTier) {
        this.name = name;
        this.description = description;
        this.minimumClassLevel = minimumClassLevel;
        this.gemTier = gemTier;
    }

    public final LinkedHashMap<Spendable, Long> getNormalRewards() {
        LinkedHashMap<Spendable, Long> rewards = createNormalRewards();
        putGems(rewards, gemTier, 1);
        return rewards;
    }

    /**
     * Oblivion pays out more of the raid's own gem tier, plus a taste of the next tier up when there is one.
     */
    public final LinkedHashMap<Spendable, Long> getOblivionRewards() {
        LinkedHashMap<Spendable, Long> rewards = createOblivionRewards();
        putGems(rewards, gemTier, 2);
        GemTier nextTier = gemTier.next();
        if (nextTier != null) {
            putGems(rewards, nextTier, 1);
        }
        return rewards;
    }

    protected abstract LinkedHashMap<Spendable, Long> createNormalRewards();

    protected abstract LinkedHashMap<Spendable, Long> createOblivionRewards();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinimumClassLevel() {
        return minimumClassLevel;
    }

    public GemTier getGemTier() {
        return gemTier;
    }
}
