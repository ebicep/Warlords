package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;

public enum Raid {

    REGNUM_OF_TWO_CROWNS(
            "Regnum of Two Crowns",
            "A long time ago the Envoy king and queen of Illusion, Physira and Mithra reigned over the vanguard's legions." +
                    " During the old war Physira got corrupted by trying to stop an unknown obsidian force from taking his queen." +
                    " All that's left now is a trail of his ashes.",
            70
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 500_000L);
                put(GuildSpendable.GUILD_COIN, 15_000L);
                put(Currencies.LEGEND_FRAGMENTS, 5000L);
                put(Currencies.ETHEREUM_CRYSTAL, 8L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 2L);
            }};
        }
    },
    OATH_OF_THE_FIRST_HEIR(
            "Oath of the First Heir",
            "PLACEHOLDER",
            75
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
            80
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
            85
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
            90
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
            95
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
            100
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getNormalRewards() {
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
        public LinkedHashMap<Spendable, Long> getOblivionRewards() {
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
    private final String name;
    private final String description;
    private final int minimumClassLevel;

    Raid(String name, String description, int minimumClassLevel) {
        this.name = name;
        this.description = description;
        this.minimumClassLevel = minimumClassLevel;
    }

    public abstract LinkedHashMap<Spendable, Long> getNormalRewards();

    public abstract LinkedHashMap<Spendable, Long> getOblivionRewards();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinimumClassLevel() {
        return minimumClassLevel;
    }
}
