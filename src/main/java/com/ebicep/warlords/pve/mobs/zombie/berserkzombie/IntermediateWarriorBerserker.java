package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;
import org.bukkit.Particle;

public class IntermediateWarriorBerserker extends AbstractBerserkZombie implements IntermediateMob {

    public IntermediateWarriorBerserker(Location spawnLocation) {
        this(
                spawnLocation,
                "Warrior Berserker",
                4000,
                0.494f, //30% more than basic zombie
                10,
                300,
                500
        );
    }

    public IntermediateWarriorBerserker(
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
                new BerserkerZombieWoundingStrike()
        );
        Value.RangedValueCritable strikeDamage = woundingStrike.getDamageValues().getStrikeDamage();
        strikeDamage.min().addMultiplicativeModifierAdd(name, .25f);
        strikeDamage.max().addMultiplicativeModifierAdd(name, .25f);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.INTERMEDIATE_WARRIOR_BERSERKER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Berserk",
                "BERS",
                Berserk.class,
                new Berserk(),
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(Particle.ANGRY_VILLAGER, warlordsNPC.getLocation().add(0, 1.75, 0), 1, 0, 0, 0, 0.1);
                    }
                }
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    currentDamageValue.addMultiplicativeModifierMult("Berserk", 1.2f);
                }
        ));
    }

}
