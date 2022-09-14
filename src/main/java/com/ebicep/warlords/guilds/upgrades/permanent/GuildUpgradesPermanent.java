package com.ebicep.warlords.guilds.upgrades.permanent;

import com.ebicep.warlords.events.player.WarlordsPlayerGiveExperienceEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.UnaryOperator;

public enum GuildUpgradesPermanent implements GuildUpgrade {

    PLAYER_EXP_BONUS(
            "Player EXP Bonus",
            Material.EXP_BOTTLE,
            true,
            null
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
                public void onEvent(WarlordsPlayerGiveExperienceEvent event) {
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
            Material.GOLD_NUGGET,
            true,
            null
    ) {
        @Override
        public double getValueFromTier(int tier) {
            return (tier == 9 ? 5 : 2 + .25 * tier) * .01;
        }

        @Override
        public String getEffectBonusFromTier(int tier) {
            return "+" + NumberFormat.formatOptionalHundredths(getValueFromTier(tier) * 100) + "%";
        }

        @Override
        public void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {

        }
    },

    ;

    public static final GuildUpgradesPermanent[] VALUES = values();
    public final String name;
    public final Material material;
    public final boolean isPermanent;
    public final UnaryOperator<Instant> expirationDate;

    GuildUpgradesPermanent(String name, Material material, boolean isPermanent, UnaryOperator<Instant> expirationDate) {
        this.name = name;
        this.material = material;
        this.isPermanent = isPermanent;
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

    @Override
    public GuildUpgradePermanent createUpgrade(int tier) {
        return new GuildUpgradePermanent(this, tier);
    }

    public long getCost(int tier) {
        switch (tier) {
            case 1:
                return 40000;
            case 2:
                return 80000;
            case 3:
                return 160000;
            case 4:
                return 320000;
            case 5:
                return 480000;
            case 6:
                return 720000;
            case 7:
                return 1080000;
            case 8:
                return 1620000;
            case 9:
                return 2430000;
            default:
                return Long.MAX_VALUE;
        }
    }

}
