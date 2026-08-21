package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class CompensationReward extends AbstractReward {

    public CompensationReward() {
    }

    public CompensationReward(LinkedHashMap<Spendable, Long> rewards, String from) {
        super(rewards, from);
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.DARK_AQUA;
    }

    public static class AscendantShardPrestigePatch extends CompensationReward {
        public AscendantShardPrestigePatch() {
        }

        public AscendantShardPrestigePatch(long totalPrestige) {
            super(new LinkedHashMap<>() {{
                put(Currencies.ASCENDANT_SHARD, totalPrestige);
            }}, "Ascendant Shard");
        }
    }

    public static class PrestigeOrbLoginPatch extends CompensationReward {
        public PrestigeOrbLoginPatch() {
            super(new LinkedHashMap<>(), "Prestige Orb Login Patch");
        }
    }

    public static class CelestialBronzePatch extends CompensationReward {
        public CelestialBronzePatch() {
        }

        public CelestialBronzePatch(long totalCelestialBronze) {
            super(new LinkedHashMap<>() {{
                put(Currencies.LEGEND_FRAGMENTS, 5000 * totalCelestialBronze);
                put(Currencies.SCRAP_METAL, 100 * totalCelestialBronze);
                put(MobDrop.ZENITH_STAR, 3 * totalCelestialBronze);
            }}, "Celestial Bronze Compensation");
        }
    }

    public static class BlessingPatch extends CompensationReward {
        public BlessingPatch() {
        }

        public BlessingPatch(LinkedHashMap<Spendable, Long> rewards) {
            super(rewards, "Blessings Compensation");
        }
    }

    public static class LevelUpPatch extends CompensationReward {
        public LevelUpPatch() {
        }

        public LevelUpPatch(LinkedHashMap<Spendable, Long> rewards) {
            super(rewards, "Level Up");
        }

        public static void giveLevelUpPatchFutureMessage(DatabasePlayer databasePlayer) {
            databasePlayer.addFutureMessage(FutureMessage.create(
                    Arrays.asList(
                            Component.text("------------------------------------------------", NamedTextColor.DARK_AQUA),
                            Component.text("Your Level Up rewards were auto claimed!", NamedTextColor.GREEN),
                            Component.text("Claim them in your Rewards Inventory", NamedTextColor.GREEN),
                            Component.text("------------------------------------------------", NamedTextColor.DARK_AQUA)
                    ),
                    true
            ));
        }
    }


}
