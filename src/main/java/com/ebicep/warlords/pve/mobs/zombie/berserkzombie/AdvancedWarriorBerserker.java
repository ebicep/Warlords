package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class AdvancedWarriorBerserker extends AbstractBerserkZombie implements AdvancedMob {

    public AdvancedWarriorBerserker(Location spawnLocation) {
        this(
                spawnLocation,
                "Warrior Berserker",
                7000,
                0.43f,
                20,
                450,
                600
        );
    }

    public AdvancedWarriorBerserker(
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
        strikeDamage.min().addMultiplicativeModifierAdd(name, .5f);
        strikeDamage.max().addMultiplicativeModifierAdd(name, .5f);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ADVANCED_WARRIOR_BERSERKER;
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
        ).addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_SELF, (event, currentDamageValue) -> {
                    currentDamageValue.addMultiplicativeModifierMult("Berserk", 1.2f);
                }
        ));
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                name,
                "LUST",
                BloodLust.BloodLustData.class,
                null,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(
                                Particle.DUST,
                                warlordsNPC.getLocation(),
                                1,
                                (Math.random() - 0.5) * 1,
                                1.2,
                                (Math.random() - 0.5) * 1,
                                1,
                                new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                        );
                    }
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getSource();
                attacker.addInstance(InstanceBuilder
                        .healing()
                        .cause(name)
                        .source(attacker)
                        .value(currentDamageValue * .65f)
                );
            }
        });
    }

}
