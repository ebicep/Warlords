package com.ebicep.warlords.guilds.upgrades.permanent;

import com.ebicep.warlords.events.player.ingame.WarlordsGiveExperienceEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveGuildCoinEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public enum GuildUpgradesPermanent implements GuildUpgrade {

    PLAYER_EXP_BONUS(
            "Player EXP Bonus",
            "Increases the experience gained at the end of the game",
            Material.EXP_BOTTLE
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
                public void onEvent(WarlordsGiveExperienceEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }
                    event.getExperienceSummary().replaceAll((key, value) -> (long) (value * getValueFromTier(tier)));
                }

            });
        }
    },
    GUILD_COIN_CONVERSION_RATE(
            "Guild Coin Conversion Rate",
            "Increases the Player to Guild Coin conversion rate",
            Material.GOLD_NUGGET
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? .004 : .0035 * tier;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + NumberFormat.formatOptionalHundredths(getValueFromTier(tier) * 100) + "%";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsGiveGuildCoinEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }

                    event.getCoinConversionRate().set(event.getCoinConversionRate().get() + getValueFromTier(tier));
                }

            });
        }
    },
    DAILY_PLAYER_COIN_BONUS(
            "Daily Player Coin Bonus",
            "Guild players gain coins from logging in once a day",
            Material.GOLD_INGOT
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return tier == 9 ? 10000 : tier * 1000;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + NumberFormat.formatOptionalHundredths(getValueFromTier(tier)) + " Coins";
        }

    },
    GUILD_MEMBER_CAPACITY(
            "Guild Member Capacity",
            "Increases the member capacity of the guild",
            Material.CHEST
    ) {
        final int[] values = new int[]{12, 14, 17, 20, 25, 30, 40, 50, 60, 60};

        @Override
        public double getValueFromTier(int tier) {
            return values[tier - 1];
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return NumberFormat.formatOptionalHundredths(getValueFromTier(tier)) + " Player Limit";
        }

        @Override
        public void onPurchase(Guild guild, int tier) {
            guild.setPlayerLimit((int) getValueFromTier(tier));
        }
    },
    LEGEND_FRAGMENT_BONUS(
            "Legend Fragment Bonus",
            "Increases the legend fragments gained at the end of the game, added every 25 waves cleared",
            Material.BLAZE_POWDER
    ) {
        final int[] values = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 20};

        @Override
        public double getValueFromTier(int tier) {
            return values[tier - 1];
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + NumberFormat.formatOptionalHundredths(getValueFromTier(tier)) + " Legend Fragments";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {
            game.registerEvents(new Listener() {

                @EventHandler
                public void onEvent(WarlordsLegendFragmentGainEvent event) {
                    if (!validUUIDs.contains(event.getPlayer().getUuid())) {
                        return;
                    }

                    event.getLegendFragments().addAndGet((int) getValueFromTier(tier) *
                            (event.getWaveDefenseOption().getDifficulty() == DifficultyIndex.HARD ? 2L : 1) *
                            (event.getWaveDefenseOption().getWavesCleared() / 25)
                    );
                }

            });
        }
    },

    ;

    public static final GuildUpgradesPermanent[] VALUES = values();
    public final String name;
    public final String description;
    public final Material material;

    GuildUpgradesPermanent(String name, String description, Material material) {
        this.name = name;
        this.description = description;
        this.material = material;
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
    public GuildUpgradePermanent createUpgrade(int tier) {
        return new GuildUpgradePermanent(this, tier);
    }

    public void onPurchase(Guild guild, int tier) {

    }

    public long getCost(int tier) {
        switch (tier) {
            case 1:
                return 20000;
            case 2:
                return 40000;
            case 3:
                return 80000;
            case 4:
                return 160000;
            case 5:
                return 240000;
            case 6:
                return 360000;
            case 7:
                return 540000;
            case 8:
                return 810000;
            case 9:
                return 1215000;
            default:
                return Long.MAX_VALUE;
        }
    }

}
