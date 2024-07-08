package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class CelestialBowWielder extends AbstractMob implements AdvancedMob {

    public CelestialBowWielder(Location spawnLocation) {
        super(
                spawnLocation,
                "Celestial Bow Wielder",
                8000,
                0.25f,
                10,
                600,
                900,
                new Fireball(5.5f)
        );
    }

    public CelestialBowWielder(
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
                maxMeleeDamage,
                new Fireball(5.5f)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CELESTIAL_BOW_WIELDER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!Utils.isProjectile(event.getCause())) {
                    return currentDamageValue * 0.1f;
                }

                return currentDamageValue;
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 160 == 0) {
            warlordsNPC.getSpec().getWeapon().onActivate(warlordsNPC);
        }
    }

}
