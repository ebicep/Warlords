package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.events.EventFlags;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
                public void onEvent(WarlordsCoinSummaryEvent event) {
                    if (!validUUIDs.contains(event.getWarlordsEntity().getUuid())) {
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

                @EventHandler(priority = EventPriority.HIGHEST)
                public void onEvent(WarlordsAddCurrencyEvent event) {
                    if (!validUUIDs.contains(event.getWarlordsEntity().getUuid())) {
                        return;
                    }
                    if (!event.getEventFlags().contains(EventFlags.GUILD)) {
                        return;
                    }
                    float currencyToAdd = event.getCurrencyToAdd();
                    event.setCurrencyToAdd((int) (currencyToAdd * getValueFromTier(tier)));
                }

            });
        }
    },
    WEAPON_DROP_RATE(
            "Weapon Drop Rate",
            "Increases the chance of a mob dropping a weapon",
            Material.WOODEN_AXE,
            start -> start.plus(24, ChronoUnit.HOURS)
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return (tier == 9 ? 100 : 10 * tier) * .01;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + Math.round((getValueFromTier(tier)) * 100) + "%";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler(priority = EventPriority.HIGHEST)
                public void onEvent(WarlordsDropWeaponEvent event) {
                    if (!validUUIDs.contains(event.getWarlordsEntity().getUuid())) {
                        return;
                    }
                    if (!event.getEventFlags().contains(EventFlags.GUILD)) {
                        return;
                    }
                    event.addModifier(getValueFromTier(tier));
                }

            });
        }
    },
    RESPAWN_TIME_REDUCTION(
            "Respawn Time Reduction",
            "Reduces the time it takes to respawn",
            Material.CLOCK,
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
                public void onEvent(WarlordsGiveRespawnEvent event) {
                    if (!validUUIDs.contains(event.getWarlordsEntity().getUuid())) {
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
        return switch (tier) {
            case 1 -> 1000;
            case 2 -> 2000;
            case 3 -> 4000;
            case 4 -> 8000;
            case 5 -> 12000;
            case 6 -> 18000;
            case 7 -> 27000;
            case 8 -> 40500;
            case 9 -> 60750;
            default -> Long.MAX_VALUE;
        };
    }

}
