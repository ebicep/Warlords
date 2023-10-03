package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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


}
