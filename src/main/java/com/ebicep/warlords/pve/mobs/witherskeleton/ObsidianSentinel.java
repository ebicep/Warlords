package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

public class ObsidianSentinel extends AbstractMob implements ChampionMob {

    public ObsidianSentinel(Location spawnLocation) {
        super(
                spawnLocation,
                "Obsidian Sentinel",
                8000,
                0.25f,
                20,
                500,
                700
        );
    }

    public ObsidianSentinel(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.fromRGB(75, 0, 110))
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.OBSIDIAN_SENTINEL;
    }
}
