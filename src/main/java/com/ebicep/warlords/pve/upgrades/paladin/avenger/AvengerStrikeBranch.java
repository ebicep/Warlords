package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.LegendaryHuntsman;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class AvengerStrikeBranch extends AbstractUpgradeBranch<AvengersStrike> {

    float energySteal = ability.getEnergySteal();

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .3f);
        damage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public AvengerStrikeBranch(AbilityTree abilityTree, AvengersStrike ability) {
        super(abilityTree, ability);
        WarlordsPlayer warlordsPlayer = abilityTree.getWarlordsPlayer();

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 15f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Energy Steal";
                    }

                    @Override
                    public void run(float value) {
                        ability.setEnergySteal(energySteal + value);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeHitBox(ability, 0.25f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Avenger's Slash",
                "Avenger's Strike - Master Upgrade",
                """
                        -5 Additional energy cost.
                        +1 Block Radius.
                        
                        Avenger's Strike hits 2 additional enemies.
                        
                        Deal 40% more damage against ADVANCED or lower enemies and deal 0.5% max health damage against ELITE or higher enemies.""",
                50000,
                () -> {
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 1);
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -5);

                    warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "MAX HP DAMAGE (Avenger's Slash)",
                            null,
                            AvengerStrikeBranch.class,
                            null,
                            abilityTree.getWarlordsPlayer(),
                            CooldownTypes.MASTERY,
                            cm -> {},
                            false
                    ) {
                        @Override
                        public float modifyDamageAfterInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            boolean isAboveElite = event.getWarlordsEntity() instanceof WarlordsNPC npc && npc.getMob().getInternalLevel() >= 4;
                            boolean isNotDuplicateStrike =
                                    !event.getFlags().contains(InstanceFlags.AVENGER_WRATH_STRIKE) ||
                                    !event.getFlags().contains(InstanceFlags.DUPLICATE_AVENGER_STRIKE);

                            if (isAboveElite && event.getCause().equals("Avenger's Strike") && isNotDuplicateStrike) {
                                return currentDamageValue + DamageCheck.clamp(event.getWarlordsEntity().getMaxHealth() * 0.005f);
                            }
                            return currentDamageValue;
                        }
                    });
                }
        );
        masterUpgrade2 = new Upgrade(
                "Avenging Strike",
                "Avenger's Strike - Master Upgrade",
                """
                        +1 Block Radius.
                        
                        Strike crit chance is increased by 15%.
                        
                        If there are at least 7 enemies within 10 blocks, strike damage is increased by 25% and movement speed is increased by 20%.
                        
                        If there are fewer, strike damage is further increased by 50%.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 1);
                    ability.getDamageValues().getStrikeDamage().critChance().addAdditiveModifier("Master Upgrade Branch", 15);
                    MotionSystem calculateSpeed = warlordsPlayer.getSpeed();
                    MotionModifier modifier = new MotionModifierBuilder().setFrom(warlordsPlayer)
                                                                         .setName("Avenging Strike")
                                                                         .setModifier(0)
                                                                         .setDuration(Integer.MAX_VALUE)
                                                                         .build();
                    warlordsPlayer.addSpeedModifier(modifier);
                    warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Avenging Strike",
                            null,
                            AvengerStrikeBranch.class,
                            null,
                            warlordsPlayer,
                            CooldownTypes.MASTERY,
                            cooldownManager -> {
                            },
                            false,
                            (cooldown, ticksElapsed) -> {
                                if (ticksElapsed % 20 == 0) {
                                    long enemiesNearBy = PlayerFilter.entitiesAround(warlordsPlayer, 10, 10, 10)
                                                                     .aliveEnemiesOf(warlordsPlayer)
                                                                     .stream()
                                                                     .count();
                                    float oldModifier = modifier.getModifier();
                                    if (enemiesNearBy >= 7 && oldModifier != 20) {
                                        modifier.setModifier(20);
                                    } else if (oldModifier != 0) {
                                        modifier.setModifier(0);
                                    }
                                }
                            }
                    ));
                }
        );
    }
}
