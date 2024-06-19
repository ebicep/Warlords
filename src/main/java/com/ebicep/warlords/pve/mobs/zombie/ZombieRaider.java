package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ZombieRaider extends AbstractMob implements AdvancedMob {

    private int knockbackResistance = 20;

    public ZombieRaider(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Raider",
                9500,
                0.42f,
                0,
                650,
                850
        );
    }

    public ZombieRaider(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZOMBIE_RAIDER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Debuff Immunity",
                null,
                null,
                null,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                }
        ) {
            final float calculatedKBRes = 1 - knockbackResistance / 100f;

            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(calculatedKBRes);
            }
        });
    }

}
