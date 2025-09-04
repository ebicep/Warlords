package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

public class SoulReaver extends AbstractMob implements BossMinionMob {

    public SoulReaver(Location spawnLocation) {
        super(
                spawnLocation,
                "Soul Reaver",
                6000,
                0.3f,
                10,
                600,
                800
        );
    }

    public SoulReaver(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());

    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SOUL_REAVER;
    }
}
