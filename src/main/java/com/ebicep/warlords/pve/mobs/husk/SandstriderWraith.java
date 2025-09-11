package com.ebicep.warlords.pve.mobs.husk;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.effects.EffectUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

public class SandstriderWraith extends AbstractMob implements BossMinionMob {

    public SandstriderWraith(Location spawnLocation) {
        super(
                spawnLocation,
                "Sandstrider Wraith",
                3000,   // max health (skirmisher tier)
                0.24f,  // walk speed (faster than brute)
                10,     // damage resistance
                260,    // min melee damage
                380     // max melee damage
        );
    }

    public SandstriderWraith(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        // Dusty spawn effect — light sand-colored flash
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.fromRGB(210, 195, 140))
                .with(FireworkEffect.Type.BALL)
                .build());
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SANDSTRIDER_WRAITH;
    }
}
