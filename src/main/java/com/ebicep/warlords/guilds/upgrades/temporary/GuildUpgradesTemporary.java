package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.events.player.pve.WarlordsPlayerAddCurrencyEvent;
import com.ebicep.warlords.events.player.pve.WarlordsPlayerCoinSummaryEvent;
import com.ebicep.warlords.events.player.pve.WarlordsPlayerDropWeaponEvent;
import com.ebicep.warlords.events.player.pve.WarlordsPlayerGiveRespawnEvent;
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
    public final Material material;
    public final UnaryOperator<Instant> expirationDate;

    GuildUpgradesTemporary(String name, Material material, UnaryOperator<Instant> expirationDate) {
        this.name = name;
        this.material = material;
        this.expirationDate = expirationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    public GuildUpgradeTemporary createUpgrade(int tier) {
        return new GuildUpgradeTemporary(this, tier);
    }

    public long getCost(int tier) {
        switch (tier) {
            case 1:
                return 250000;
            case 2:
                return 500000;
            case 3:
                return 1000000;
            case 4:
                return 2000000;
            case 5:
                return 3000000;
            case 6:
                return 4500000;
            case 7:
                return 6750000;
            case 8:
                return 10125000;
            case 9:
                return 15187500;
            default:
                return Long.MAX_VALUE;
        }
    }

}
