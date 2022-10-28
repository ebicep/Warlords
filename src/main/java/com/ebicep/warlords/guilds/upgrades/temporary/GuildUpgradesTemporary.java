package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerDropWeaponEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.UnaryOperator;

public enum GuildUpgradesTemporary implements GuildUpgrade {

    COINS_BOOST(
            "Coins Boost",
            "Increases the coins gained at the end of the game",
            Material.GOLD_INGOT,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? 4 : 1 + .25 * tier;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return NumberFormat.formatOptionalHundredths(getValueFromTier(tier)) + "x";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsPlayerCoinSummaryEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }
                    LinkedHashMap<String, Long> currencyToAdd = event.getCurrencyToAdd();
                    currencyToAdd.forEach((s, aLong) -> currencyToAdd.put(s, (long) (aLong * getValueFromTier(tier))));
                }

            });
        }
    },
    INSIGNIA_BOOST(
            "Insignia Boost",
            "Increases the insignia gained throughout the game",
            Material.NETHER_STAR,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? 1.5 : 1 + .05 * tier;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return getValueFromTier(tier) + "x";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsPlayerAddCurrencyEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }
                    event.getCurrencyToAdd().set((int) (event.getCurrencyToAdd().get() * getValueFromTier(tier)));
                }

            });
        }
    },
    WEAPON_DROP_RATE(
            "Weapon Drop Rate",
            "Increases the chance of a mob dropping a weapon",
            Material.WOOD_AXE,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return 1 + (tier == 9 ? 100 : 10 * tier) * .01;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + Math.round((getValueFromTier(tier) - 1) * 100) + "%";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsPlayerDropWeaponEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }
                    event.getDropRate().set(event.getDropRate().get() * getValueFromTier(tier));
                }

            });
        }
    },
    RESPAWN_TIME_REDUCTION(
            "Respawn Time Reduction",
            "Reduces the time it takes to respawn",
            Material.WATCH,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? -10 : -tier;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return (int) getValueFromTier(tier) + "s";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsPlayerGiveRespawnEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }
                    event.getRespawnTimer().set((int) (event.getRespawnTimer().get() + getValueFromTier(tier)));
                }

            });
        }
    },

    ;

    public static final GuildUpgradesTemporary[] VALUES = values();
    public final String name;
    public final String description;
    public final Material material;
    public final UnaryOperator<Instant> expirationDate;

    GuildUpgradesTemporary(String name, String description, Material material, UnaryOperator<Instant> expirationDate) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.expirationDate = expirationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public GuildUpgradeTemporary createUpgrade(int tier) {
        return new GuildUpgradeTemporary(this, tier);
    }

    public long getCost(int tier) {
        switch (tier) {
            case 1:
                return 10000;
            case 2:
                return 20000;
            case 3:
                return 40000;
            case 4:
                return 80000;
            case 5:
                return 120000;
            case 6:
                return 180000;
            case 7:
                return 270000;
            case 8:
                return 405000;
            case 9:
                return 607500;
            default:
                return Long.MAX_VALUE;
        }
    }

}
