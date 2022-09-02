package com.ebicep.warlords.guilds.upgrades;

import com.ebicep.warlords.game.Game;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.UnaryOperator;

public enum GuildUpgrades {

    //TEMPORARY UPGRADES
    COINS_BOOST(
            "Coins Boost",
            false,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? 4 : 1 + .25 * tier;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },
    INSIGNIA_BOOST(
            "Insignia Boost",
            false,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return 1 + .05 * tier;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },
    RESPAWN_TIME_REDUCTION(
            "Respawn Time Reduction",
            false,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? -10 : -tier;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },
    WEAPON_DROP_RATE(
            "Weapon Drop Rate",
            false,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return 1 + (tier == 9 ? 100 : 10 * tier) * .01;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },

    //PERMANENT UPGRADES
    SPEC_CLASS_EXP_BONUS(
            "Spec/Class EXP Bonus",
            true,
            null
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return 1 + .05 * tier;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },
    GUILD_COIN_CONVERSION_RATE(
            "Guild Coin Conversion Rate",
            true,
            null
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return (tier == 9 ? 5 : 2 + .25 * tier) * .01;
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs) {

        }
    },

    ;

    public final String name;
    public final boolean isPermanent;
    public final UnaryOperator<Instant> expirationDate;

    GuildUpgrades(String name, boolean isPermanent, UnaryOperator<Instant> expirationDate) {
        this.name = name;
        this.isPermanent = isPermanent;
        this.expirationDate = expirationDate;
    }

    public abstract double getValueFromTier(int tier);

    /**
     * @param game       the game to modify - main purpose is adding listeners
     * @param validUUIDs uuids that are allowed to get the effect of this upgrade (since listener will affect all players we need to filter out players which guilds have this upgrade)
     */
    public abstract void onGame(Game game, HashSet<UUID> validUUIDs);

    public long getCost(int tier) {
        if (isPermanent) {
            switch (tier) {
                case 0:
                    return 250000;
                case 1:
                    return 500000;
                case 2:
                    return 1000000;
                case 3:
                    return 2000000;
                case 4:
                    return 3000000;
                case 5:
                    return 4500000;
                case 6:
                    return 6750000;
                case 7:
                    return 10125000;
                case 8:
                    return 15187500;
                default:
                    return Long.MAX_VALUE;
            }
        } else {
            switch (tier) {
                case 0:
                    return 10000;
                case 1:
                    return 20000;
                case 2:
                    return 40000;
                case 3:
                    return 80000;
                case 4:
                    return 120000;
                case 5:
                    return 180000;
                case 6:
                    return 270000;
                case 7:
                    return 405000;
                case 8:
                    return 607500;
                default:
                    return Long.MAX_VALUE;
            }
        }
    }

}
