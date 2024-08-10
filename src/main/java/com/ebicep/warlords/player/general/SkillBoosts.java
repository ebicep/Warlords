package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.function.Consumer;

public enum SkillBoosts {

    FIREBALL("Fireball",
            List.of(
                    Component.text("Increase the damage of Fireball by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the direct hit damage bonus by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Fireball.class,
            abstractAbility -> {
                if (abstractAbility instanceof Fireball fireball) {
                    fireball.getDamageValues()
                            .getFireballDamage()
                            .forEachValue(floatModifiable -> {
                                floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                            });
                    fireball.setDirectHitMultiplier(fireball.getDirectHitMultiplier() + 25);
                }
            }
    ),
    FLAME_BURST("Flame Burst",
            List.of(
                    Component.text("Increase the damage of Flame Burst by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and reduce the energy cost by "),
                    Component.text("40", NamedTextColor.RED),
                    Component.text(".")
            ),
            FlameBurst.class,
            abstractAbility -> {
                if (abstractAbility instanceof FlameBurst flameBurst) {
                    flameBurst.getDamageValues()
                              .getFlameBurstDamage()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                              });
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -40);
                }
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            List.of(
                    Component.text("Increase the healing of Time Warp by "),
                    Component.text("10% ", NamedTextColor.RED),
                    Component.text("and reduce the cooldown by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(".")
            ),
            TimeWarpPyromancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpPyromancer timeWarp) {
                    timeWarp.setWarpHealPercentage(timeWarp.getWarpHealPercentage() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            List.of(
                    Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    INFERNO("Inferno",
            List.of(
                    Component.text("Increase the Crit Chance bonus of Inferno by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(" but reduce the Crit Multiplier bonus by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Inferno.class,
            abstractAbility -> {
                if (abstractAbility instanceof Inferno inferno) {
                    inferno.setCritChanceIncrease(inferno.getCritChanceIncrease() + 50);
                    inferno.setCritMultiplierIncrease(inferno.getCritMultiplierIncrease() - 15);
                }
            }
    ),
    FROST_BOLT("Frostbolt",
            List.of(
                    Component.text("Increase the direct hit damage bonus of Frostbolt by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(".")
            ),
            FrostBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof FrostBolt frostBolt) {
                    frostBolt.setDirectHitMultiplier(frostBolt.getDirectHitMultiplier() + 35);
                }
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            List.of(
                    Component.text("Reduce the cooldown of Freezing Breath by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            FreezingBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof FreezingBreath) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            List.of(
                    Component.text("Reduce the cooldown of Time Warp by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            TimeWarpCryomancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpCryomancer) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            List.of(
                    Component.text("Reduce the cooldown of Arcane Shield by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            List.of(
                    Component.text("Increase the damage reduction of Ice Barrier by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the duration by "),
                    Component.text("2 ", NamedTextColor.RED),
                    Component.text("seconds.")
            ),
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier iceBarrier) {
                    iceBarrier.setDamageReductionPercent(iceBarrier.getDamageReductionPercent() + 10);
                    iceBarrier.setTickDuration(iceBarrier.getTickDuration() + 40);
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            List.of(
                    Component.text("Increase the healing of Water Bolt by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the direct hit damage and heal bonus by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            WaterBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBolt waterBolt) {
                    waterBolt.getHealValues()
                             .getBoltHealing()
                             .forEachValue(floatModifiable -> {
                                 floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                             });
                    waterBolt.setDirectHitMultiplier(waterBolt.getDirectHitMultiplier() + 25);
                }
            }
    ),
    WATER_BREATH("Water Breath",
            List.of(
                    Component.text("Reduce the cooldown of Water Breath by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and reduce the energy cost by "),
                    Component.text("30", NamedTextColor.RED),
                    Component.text(".")
            ),
            WaterBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBreath) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -30);
                }
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            List.of(
                    Component.text("Increase the duration of Time Warp by "),
                    Component.text("3", NamedTextColor.RED),
                    Component.text(" seconds and reduce the cooldown by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            TimeWarpAquamancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpAquamancer timeWarpAquamancer) {
                    timeWarpAquamancer.setTickDuration(timeWarpAquamancer.getTickDuration() + 60);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            List.of(
                    Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            List.of(
                    Component.text("Increase the duration of Healing Rain by "),
                    Component.text("2", NamedTextColor.RED),
                    Component.text(" seconds and reduce the cooldown by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain healingRain) {
                    healingRain.setTickDuration(healingRain.getTickDuration() + 40);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            List.of(
                    Component.text("Increase the damage of Wounding Strike by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and reduce the energy cost by "),
                    Component.text("10", NamedTextColor.RED),
                    Component.text(".")
            ),
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeBerserker woundingStrikeBerserker) {
                    woundingStrikeBerserker.getDamageValues()
                                           .getStrikeDamage()
                                           .forEachValue(floatModifiable -> {
                                               floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                           });
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -10);
                }
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            List.of(
                    Component.text("Increase the damage of Seismic Wave by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SeismicWaveBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWaveBerserker seismicWaveBerserker) {
                    seismicWaveBerserker.getDamageValues()
                                        .getWaveDamage()
                                        .forEachValue(floatModifiable -> {
                                            floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                        });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            List.of(
                    Component.text("Remove the energy cost of Ground Slam and increase the damage by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            GroundSlamBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlamBerserker groundSlamBerserker) {
                    groundSlamBerserker.getDamageValues()
                                       .getSlamDamage()
                                       .forEachValue(floatModifiable -> {
                                           floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .4f);
                                       });
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                }
            }
    ),
    BLOOD_LUST("Blood Lust",
            List.of(
                    Component.text("Reduce the cooldown of Blood Lust by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and increase damage converted to healing by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust bloodLust) {
                    bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() + 20);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                }
            }
    ),
    BERSERK("Berserk",
            List.of(
                    Component.text("Increase the damage bonus of Berserk by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and increase the speed by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Berserk.class,
            abstractAbility -> {
                if (abstractAbility instanceof Berserk berserk) {
                    berserk.setDamageIncrease(berserk.getDamageIncrease() + 15);
                    berserk.setSpeedBuff(berserk.getSpeedBuff() + 10);
                }
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            List.of(
                    Component.text("Increase the damage of Wounding Strike by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and increase wounding by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            WoundingStrikeDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeDefender woundingStrikeDefender) {
                    woundingStrikeDefender.getDamageValues()
                                          .getStrikeDamage()
                                          .forEachValue(floatModifiable -> {
                                              floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                          });
                    woundingStrikeDefender.setWounding(woundingStrikeDefender.getWounding() + 10);
                }
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            List.of(
                    Component.text("Increase the knockback of Seismic Wave by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SeismicWaveDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractSeismicWave seismicWave) {
                    seismicWave.setVelocity(1.5f);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            List.of(
                    Component.text("Increase the knockback of Ground Slam by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            GroundSlamDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractGroundSlam groundSlam) {
                    groundSlam.setVelocity(1.35f);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    INTERVENE("Intervene",
            List.of(
                    Component.text("Increase the cast and break range of Intervene by "),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(" blocks and reduce the damage taken by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Intervene.class,
            abstractAbility -> {
                if (abstractAbility instanceof Intervene intervene) {
                    intervene.setDamageReduction(intervene.getDamageReduction() - 25);
                    intervene.setRadius(intervene.getRadius() + 5);
                    intervene.setBreakRadius(intervene.getBreakRadius() + 5);
                }
            }
    ),
    LAST_STAND("Last Stand",
            List.of(
                    Component.text("Increase the damage reduction of Last Stand by "),
                    Component.text("5%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand lastStand) {
                    lastStand.setSelfDamageReductionPercent((int) (lastStand.getSelfDamageReduction() + 5));
                    lastStand.setTeammateDamageReductionPercent((int) (lastStand.getTeammateDamageReduction() + 5));
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .9f);
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            List.of(
                    Component.text("Increase crippling by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(", increase the additional reduction per strike by "),
                    Component.text("5%", NamedTextColor.RED),
                    Component.text(", and reduce the energy cost by "),
                    Component.text("10", NamedTextColor.RED),
                    Component.text(".")
            ),
            CripplingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CripplingStrike cripplingStrike) {
                    cripplingStrike.setCripple(cripplingStrike.getCripple() + 10);
                    cripplingStrike.setCripplePerStrike(cripplingStrike.getCripplePerStrike() + 5);
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -10);
                }
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            List.of(
                    Component.text("Increase the immobilize duration of Reckless Charge by "),
                    Component.text("0.5", NamedTextColor.RED),
                    Component.text(" seconds and reduce the cooldown by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge recklessCharge) {
                    recklessCharge.setStunTimeInTicks(recklessCharge.getStunTimeInTicks() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            List.of(
                    Component.text("Reduce the cooldown of Ground Slam by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            GroundSlamRevenant.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlamRevenant) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            List.of(
                    Component.text("Increase the healing of Orbs of Life by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            OrbsOfLife.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrbsOfLife orbsOfLife) {
                    orbsOfLife.getHealValues()
                              .getOrbHealing()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                              });
                }
            }
    ),
    UNDYING_ARMY("Undying Army",
            List.of(
                    Component.text("Reduce the damage of Undying Army after dying by "),
                    Component.text("5%", NamedTextColor.RED),
                    Component.text(" and increase the duration by "),
                    Component.text("7", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy undyingArmy) {
                    undyingArmy.setTickDuration(undyingArmy.getTickDuration() + 140);
                    undyingArmy.setMaxHealthDamage(undyingArmy.getMaxHealthDamage() - 5);
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            List.of(
                    Component.text("Increase the damage of Avenger's Strike by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the energy steal by "),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(".")
            ),
            AvengersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersStrike avengersStrike) {
                    avengersStrike.getDamageValues()
                                  .getStrikeDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                  });
                    avengersStrike.setEnergySteal(avengersStrike.getEnergySteal() + 5);
                }
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            List.of(
                    Component.text("Increase the damage of Consecrate by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(" and remove the energy cost.")
            ),
            ConsecrateAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof ConsecrateAvenger consecrateAvenger) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    consecrateAvenger.getDamageValues()
                                     .getConsecrateDamage()
                                     .forEachValue(floatModifiable -> {
                                         floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .4f);
                                     });
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            List.of(
                    Component.text("Reduce the cooldown of Light Infusion by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and increase the speed duration by "),
                    Component.text("4", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            LightInfusionAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionAvenger lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 80);
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            List.of(
                    Component.text("Reduce the cooldown of Holy Radiance by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and increase the energy drain of Avenger's Mark by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HolyRadianceAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceAvenger holyRadiance) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    holyRadiance.setEnergyDrainPerSecond(holyRadiance.getEnergyDrainPerSecond() * 1.5f);
                }
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            List.of(
                    Component.text("Increase the energy per second of Avenger's Wrath by "),
                    Component.text("10", NamedTextColor.RED),
                    Component.text(" and increase the duration by "),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath avengersWrath) {
                    avengersWrath.setTickDuration(avengersWrath.getTickDuration() + 100);
                    avengersWrath.setEnergyPerSecond(avengersWrath.getEnergyPerSecond() + 10);
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            List.of(
                    Component.text("Increase the damage of Crusader's Strike by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the energy restored by "),
                    Component.text("3", NamedTextColor.RED),
                    Component.text(".")
            ),
            CrusadersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CrusadersStrike crusadersStrike) {
                    crusadersStrike.getDamageValues()
                            .getStrikeDamage()
                            .forEachValue(floatModifiable -> {
                                floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                            });
                    crusadersStrike.setEnergyGiven(crusadersStrike.getEnergyGiven() + 3);
                }
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            List.of(
                    Component.text("Increase the damage of Consecrate by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and remove the energy cost.")
            ),
            ConsecrateCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof ConsecrateCrusader consecrateCrusader) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    consecrateCrusader.getDamageValues()
                                      .getConsecrateDamage()
                                      .forEachValue(floatModifiable -> {
                                          floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .35f);
                                      });
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            List.of(
                    Component.text("Reduce the cooldown of Light Infusion by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and increase the speed duration by "),
                    Component.text("4", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            LightInfusionCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionCrusader lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 80);
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            List.of(
                    Component.text("Reduce the cooldown of Holy Radiance by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(", increase the duration of Crusader's Mark by "),
                    Component.text("4", NamedTextColor.RED),
                    Component.text(" seconds and speed bonus by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader holyRadiance) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    holyRadiance.setMarkDuration(holyRadiance.getMarkDuration() + 4);
                    holyRadiance.setMarkSpeed(holyRadiance.getMarkSpeed() + 15);
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            List.of(
                    Component.text("Reduce the cooldown of Inspiring Presence by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and increase the speed by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            InspiringPresence.class,
            abstractAbility -> {
                if (abstractAbility instanceof InspiringPresence inspiringPresence) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + 10);
                }
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            List.of(
                    Component.text("Increase the damage converted to healing for allies of Protector's Strike by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike protectorsStrike) {
                    protectorsStrike.setAllyHealing(protectorsStrike.getAllyHealing() + 15);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            List.of(
                    Component.text("Increase the range of Consecrate by "),
                    Component.text("2", NamedTextColor.RED),
                    Component.text(" blocks and reduce the cooldown by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ConsecrateProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractConsecrate consecrate) {
                    consecrate.getHitBoxRadius().addAdditiveModifier("Skill Boost", 2);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            List.of(
                    Component.text("Reduce the cooldown of Light Infusion by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(" and increase the speed duration by "),
                    Component.text("4", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            LightInfusionProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionProtector lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 80);
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            List.of(
                    Component.text("Increase the healing of Holy Radiance by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HolyRadianceProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceProtector holyRadianceProtector) {
                    holyRadianceProtector.getHealValues()
                                         .getRadianceHealing()
                                         .forEachValue(floatModifiable -> {
                                             floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                         });
                }
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            List.of(
                    Component.text("Increase the healing of Hammer of Light by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HammerOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof HammerOfLight hammerOfLight) {
                    hammerOfLight.getHealValues()
                                 .getHammerHealing()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                 });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            List.of(
                    Component.text("Increase the damage of Lightning Bolt by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            LightningBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningBolt lightningBolt) {
                    lightningBolt.getDamageValues()
                                 .getBoltDamage()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                 });
                }
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            List.of(
                    Component.text("Increase the damage of Chain Lightning by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ChainLightning.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainLightning chainLightning) {
                    chainLightning.getDamageValues()
                                  .getChainDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                  });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                }
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            List.of(
                    Component.text("Increase the damage of Windfury Weapon by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(" and increase the proc chance by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            WindfuryWeapon.class,
            abstractAbility -> {
                if (abstractAbility instanceof WindfuryWeapon windfuryWeapon) {
                    windfuryWeapon.setProcChance(windfuryWeapon.getProcChance() + 10);
                    windfuryWeapon.setWeaponDamage(windfuryWeapon.getWeaponDamage() + 30);
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            List.of(
                    Component.text("Reduce the cooldown of Lightning Rod by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            LightningRod.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningRod) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            List.of(
                    Component.text("Reduce the cooldown of Capacitor Totem by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            List.of(
                    Component.text("Increase the damage of Fallen Souls by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            FallenSouls.class,
            abstractAbility -> {
                if (abstractAbility instanceof FallenSouls fallenSouls) {
                    fallenSouls.getDamageValues()
                               .getFallenSoulDamage()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                               });
                }
            }
    ),
    SPIRIT_LINK("Spirit Link",
            List.of(
                    Component.text("Increase the damage of Spirit Link by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and increase the speed duration by "),
                    Component.text("0.5", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            SpiritLink.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritLink spiritLink) {
                    spiritLink.getDamageValues()
                              .getLinkDamage()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                              });
                    spiritLink.setSpeedDuration(spiritLink.getSpeedDuration() + 0.5f);
                }
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            List.of(
                    Component.text("Increase the healing of Soulbinding Weapon by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Soulbinding.class,
            abstractAbility -> {
                if (abstractAbility instanceof Soulbinding soulbinding) {
                    soulbinding.getHealValues()
                               .getAllyHealing()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                               });
                    soulbinding.getHealValues()
                               .getSelfHealing()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                               });
                }
            }
    ),
    REPENTANCE("Repentance",
            List.of(
                    Component.text("Increase the damage converted by "),
                    Component.text("5%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance repentance) {
                    repentance.setDamageConvertPercent(repentance.getDamageConvertPercent() + 5);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .9f);
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            List.of(
                    Component.text("Increase the range of Death's Debt by"),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(" blocks and reduce the delayed damage inflicted by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(".")
            ),
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt deathsDebt) {
                    deathsDebt.setRespiteRadius(deathsDebt.getRespiteRadius() + 5);
                    deathsDebt.setDebtRadius(deathsDebt.getDebtRadius() + 5);
                    deathsDebt.setDelayedDamageTaken(deathsDebt.getDelayedDamageTaken() - 40);
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            List.of(
                    Component.text("Increase the damage of Earthen Spike by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and increase the speed by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            EarthenSpike.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthenSpike earthenSpike) {
                    earthenSpike.getDamageValues()
                                .getSpikeDamage()
                                .forEachValue(floatModifiable -> {
                                    floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                });
                    earthenSpike.setSpeed(earthenSpike.getSpeed() * 1.2f);
                }
            }
    ),
    BOULDER("Boulder",
            List.of(
                    Component.text("Increase the damage of Boulder by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Boulder.class,
            abstractAbility -> {
                if (abstractAbility instanceof Boulder boulder) {
                    boulder.getDamageValues()
                           .getBoulderDamage()
                           .forEachValue(floatModifiable -> {
                               floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                           });
                }
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            List.of(
                    Component.text("Increase the proc chance of Earthliving Weapon by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            EarthlivingWeapon.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthlivingWeapon earthlivingWeapon) {
                    earthlivingWeapon.setProcChance(earthlivingWeapon.getProcChance() + 20);
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            List.of(
                    Component.text("Increase the healing of Chain Heal by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(".")
            ),
            ChainHeal.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainHeal chainHeal) {
                    chainHeal.getHealValues()
                             .getChainHealing()
                             .forEachValue(floatModifiable -> {
                                 floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .3f);
                             });
                }
            }
    ),
    HEALING_TOTEM("Healing Totem",
            List.of(
                    Component.text("Increase the healing of Healing Totem by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HealingTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingTotem healingTotem) {
                    healingTotem.getHealValues()
                                .getTotemHealing()
                                .forEachValue(floatModifiable -> {
                                    floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    JUDGEMENT_STRIKE("Judgement Strike",
            List.of(
                    Component.text("Increase the damage of Judgement Strike by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            JudgementStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof JudgementStrike judgementStrike) {
                    judgementStrike.getDamageValues()
                                   .getStrikeDamage()
                                   .forEachValue(floatModifiable -> {
                                       floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                   });
                }
            }
    ),
    INCENDIARY_CURSE("Incendiary Curse",
            List.of(
                    Component.text("Reduce the cooldown of Incendiary Curse by "),
                    Component.text("40%", NamedTextColor.RED),
                    Component.text(" and increase the blind duration by "),
                    Component.text("0.5", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            IncendiaryCurse.class,
            abstractAbility -> {
                if (abstractAbility instanceof IncendiaryCurse incendiaryCurse) {
                    incendiaryCurse.setBlindDurationInTicks(incendiaryCurse.getBlindDurationInTicks() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    BLINDING_ASSAULT("Shadow Step",
            List.of(
                    Component.text("Reduce the cooldown of Shadow Step by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(" and grant temporary fall damage immunity.")
            ),
            ShadowStep.class,
            abstractAbility -> {
                if (abstractAbility instanceof ShadowStep shadowStep) {
                    shadowStep.setFallDamageNegation(1000);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    SOUL_SWITCH("Soul Switch",
            List.of(
                    Component.text("Reduce the cooldown by Soul Switch by "),
                    Component.text("60%", NamedTextColor.RED),
                    Component.text(" and increase the range by "),
                    Component.text("6", NamedTextColor.RED),
                    Component.text(" blocks.")
            ),
            SoulSwitch.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulSwitch soulSwitch) {
                    soulSwitch.getHitBoxRadius().addAdditiveModifier("Skill Boost", 6);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .4f);
                }
            }
    ),
    ORDER_OF_EVISCERATE("Order of Eviscerate",
            List.of(
                    Component.text("Increase the duration of Order of Eviscerate by "),
                    Component.text("6", NamedTextColor.RED),
                    Component.text(" seconds and reduce the cooldown by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(".")
            ),
            OrderOfEviscerate.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrderOfEviscerate orderOfEviscerate) {
                    orderOfEviscerate.setTickDuration(orderOfEviscerate.getTickDuration() + 120);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                }
            }
    ),
    RIGHTEOUS_STRIKE("Righteous Strike",
            List.of(
                    Component.text("Increase the damage of Righteous Strike by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            RighteousStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof RighteousStrike righteousStrike) {
                    righteousStrike.getDamageValues()
                                   .getStrikeDamage()
                                   .forEachValue(floatModifiable -> {
                                       floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                   });
                }
            }
    ),
    SOUL_SHACKLE("Soul Shackle",
            List.of(
                    Component.text("Reduce the cooldown of Soul Shackle by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SoulShackle.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulShackle) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    HEART_TO_HEART("Heart to Heart",
            List.of(
                    Component.text("Reduce the cooldown of Heart to Heart by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            HeartToHeart.class,
            abstractAbility -> {
                if (abstractAbility instanceof HeartToHeart) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    PRISM_GUARD("Prism Guard",
            List.of(
                    Component.text("Increase the projectile damage reduction of Prism Guard by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            PrismGuard.class,
            abstractAbility -> {
                if (abstractAbility instanceof PrismGuard prismGuard) {
                    prismGuard.setProjectileDamageReduction(prismGuard.getProjectileDamageReduction() + 15);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                }
            }
    ),
    VINDICATE("Vindicate",
            List.of(
                    Component.text("Increase the damage reduction of Vindicate by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and reduce the cooldown by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Vindicate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Vindicate vindicate) {
                    vindicate.setVindicateDamageReduction(vindicate.getVindicateDamageReduction() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    IMPALING_STRIKE("Impaling Strike",
            List.of(
                    Component.text("Increase the damage of Impaling Strike by "),
                    Component.text("10%", NamedTextColor.RED),
                    Component.text(" and increase the leech duration by "),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            ImpalingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ImpalingStrike impalingStrike) {
                    impalingStrike.setLeechDuration(impalingStrike.getLeechDuration() + 5);
                    impalingStrike.getDamageValues()
                                  .getStrikeDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                  });
                }
            }
    ),
    SOOTHING_PUDDLE("Soothing Elixir",
            List.of(
                    Component.text("Increase the impact healing of Soothing Elixir by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SoothingElixir.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoothingElixir soothingElixir) {
                    soothingElixir.getHealValues()
                                  .getElixirHealing()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                  });
                }
            }
    ),
    VITALITY_CONCOCTION("Vitality Concoction",
            List.of(
                    Component.text("Remove the energy cost of Vitality Concoction and increase the duration by "),
                    Component.text("0.8s", NamedTextColor.RED),
                    Component.text(".")
            ),
            VitalityConcoction.class,
            abstractAbility -> {
                if (abstractAbility instanceof VitalityConcoction vitalityConcoction) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    vitalityConcoction.setTickDuration(vitalityConcoction.getTickDuration() + 16);
                }
            }
    ),
    REMEDIC_CHAINS("Remedic Chains",
            List.of(
                    Component.text("Increase the damage bonus of Remedic Chains by "),
                    Component.text("6%", NamedTextColor.RED),
                    Component.text(" and increase the link break range by "),
                    Component.text("10", NamedTextColor.RED),
                    Component.text(" blocks.")
            ),
            RemedicChains.class,
            abstractAbility -> {
                if (abstractAbility instanceof RemedicChains remedicChains) {
                    remedicChains.setAllyDamageIncrease(remedicChains.getAllyDamageIncrease() + 6);
                    remedicChains.setLinkBreakRadius(remedicChains.getLinkBreakRadius() + 10);
                }
            }
    ),
    DRAINING_MIASMA("Draining Miasma",
            List.of(
                    Component.text("Increase the leech duration of Draining Miasma by "),
                    Component.text("5", NamedTextColor.RED),
                    Component.text(" seconds and reduce the cooldown by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            DrainingMiasma.class,
            abstractAbility -> {
                if (abstractAbility instanceof DrainingMiasma drainingMiasma) {
                    drainingMiasma.setLeechDuration(drainingMiasma.getLeechDuration() + 5);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    POISONOUS_HEX("Poisonous Hex",
            List.of(
                    Component.text("Increase the pierce of Poisonous Hex by "),
                    Component.text("1", NamedTextColor.RED),
                    Component.text(" and the duration by "),
                    Component.text("2", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            PoisonousHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof PoisonousHex poisonousHex) {
                    poisonousHex.setMaxEnemiesHit(3);
                    poisonousHex.setTickDuration(poisonousHex.getTickDuration() + 20);
                }
            }
    ),
    SOULFIRE_BEAM("Soulfire Beam",
            List.of(
                    Component.text("Reduce the cooldown of Soulfire Beam by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SoulfireBeam.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulfireBeam) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_CONJURER("Energy Seer",
            List.of(
                    Component.text("Reduce the cooldown of Energy Seer by "),
                    Component.text("45%", NamedTextColor.RED),
                    Component.text(".")
            ),
            EnergySeerConjurer.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerConjurer) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .55f);
                }
            }
    ),
    CONTAGIOUS_FACADE("Contagious Facade",
            List.of(
                    Component.text("Increased the damage reduction of Contagious Facade by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(", reduce the cooldown by "),
                    Component.text("30%", NamedTextColor.RED),
                    Component.text(", and grant "),
                    Component.text("1", NamedTextColor.RED),
                    Component.text(" extra Hex stack.")
            ),
            ContagiousFacade.class,
            abstractAbility -> {
                if (abstractAbility instanceof ContagiousFacade contagiousFacade) {
                    contagiousFacade.getDamageAbsorption().addAdditiveModifier("Skill Boost", 20);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                    contagiousFacade.setStacksGranted(contagiousFacade.getStacksGranted() + 1);
                }
            }
    ),
    ASTRAL_PLAGUE("Astral Plague",
            List.of(
                    Component.text("Reduce the cooldown of Astral Plague by "),
                    Component.text("35%", NamedTextColor.RED),
                    Component.text(".")
            ),
            AstralPlague.class,
            abstractAbility -> {
                if (abstractAbility instanceof AstralPlague) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                }
            }
    ),
    FORTIFYING_HEX("Fortifying Hex",
            List.of(
                    Component.text("Increase the damage reduction of Fortifying Hex by "),
                    Component.text("2%", NamedTextColor.RED),
                    Component.text(" and the duration by "),
                    Component.text("2", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            FortifyingHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof FortifyingHex fortifyingHex) {
                    fortifyingHex.getDamageReduction().addAdditiveModifier("Skill Boost", 2);
                    fortifyingHex.setTickDuration(fortifyingHex.getTickDuration() + 40);
                }
            }
    ),
    GUARDIAN_BEAM("Guardian Beam",
            List.of(
                    Component.text("Reduce the cooldown of Guardian Beam by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            GuardianBeam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GuardianBeam) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_SENTINEL("Energy Seer",
            List.of(
                    Component.text("Increase the energy restored by Energy Seer by "),
                    Component.text("140", NamedTextColor.RED),
                    Component.text(".")
            ),
            EnergySeerSentinel.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerSentinel energySeerSentinel) {
                    energySeerSentinel.setEnergyRestore(energySeerSentinel.getEnergyRestore() + 140);
                }
            }
    ),
    MYSTICAL_BARRIER("Mystical Barrier",
            List.of(
                    Component.text("Increase the rune timer increase inflicted by Mystical Barrier by "),
                    Component.text("0.5", NamedTextColor.RED),
                    Component.text(" seconds and increase the base and maximum shield health by "),
                    Component.text("400", NamedTextColor.RED),
                    Component.text(" and "),
                    Component.text("800", NamedTextColor.RED),
                    Component.text(".")
            ),
            MysticalBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof MysticalBarrier mysticalBarrier) {
                    mysticalBarrier.setRuneTimerIncrease(mysticalBarrier.getRuneTimerIncrease() + 0.5f);
                    mysticalBarrier.setShieldBase(mysticalBarrier.getShieldBase() + 400);
                    mysticalBarrier.setShieldMaxHealth(mysticalBarrier.getShieldMaxHealth() + 800);
                }
            }
    ),
    SANCTUARY("Sanctuary",
            List.of(
                    Component.text("Reduce the cooldown of Sanctuary by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            Sanctuary.class,
            abstractAbility -> {
                if (abstractAbility instanceof Sanctuary) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    MERCIFUL_HEX("Merciful Hex",
            List.of(
                    Component.text("Increase the healing over time healed by Merciful Hex by "),
                    Component.text("100%", NamedTextColor.RED),
                    Component.text(" and increase the duration by "),
                    Component.text("2", NamedTextColor.RED),
                    Component.text(" seconds.")
            ),
            MercifulHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof MercifulHex mercifulHex) {
                    mercifulHex.getHealValues()
                               .getHexDOTHealing()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", 1.0f);
                               });
                    mercifulHex.setTickDuration(mercifulHex.getTickDuration() + 20);
                }
            }
    ),
    RAY_OF_LIGHT("Ray of Light",
            List.of(
                    Component.text("Reduce the cooldown of Ray of Light by "),
                    Component.text("20%", NamedTextColor.RED),
                    Component.text(".")
            ),
            RayOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof RayOfLight) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_LUMINARY("Energy Seer",
            List.of(
                    Component.text("Increase the energy restored by Energy Seer by "),
                    Component.text("140", NamedTextColor.RED),
                    Component.text(".")
            ),
            EnergySeerLuminary.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerLuminary energySeerLuminary) {
                    energySeerLuminary.setEnergyRestore(energySeerLuminary.getEnergyRestore() + 140);
                }
            }
    ),
    SANCTIFIED_BEACON("Sanctified Beacon",
            List.of(
                    Component.text("Remove the energy cost of Sanctified Beacon, reduce the cooldown by "),
                    Component.text("50%", NamedTextColor.RED),
                    Component.text(", and increase the Crit Multiplier reduction by "),
                    Component.text("15%", NamedTextColor.RED),
                    Component.text(".")
            ),
            SanctifiedBeacon.class,
            abstractAbility -> {
                if (abstractAbility instanceof SanctifiedBeacon sanctifiedBeacon) {
                    abstractAbility.getEnergyCost().addMultiplicativeModifierMult("Skill Boost", 0);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                    sanctifiedBeacon.setCritMultiplierReducedBy(sanctifiedBeacon.getCritMultiplierReducedBy() + 15);
                }
            }
    ),
    DIVINE_BLESSING("Divine Blessing",
            List.of(
                    Component.text("Reduce the cooldown of Divine Blessing by "),
                    Component.text("25%", NamedTextColor.RED),
                    Component.text(".")
            ),
            DivineBlessing.class,
            abstractAbility -> {
                if (abstractAbility instanceof DivineBlessing) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),

    ;

    public static final SkillBoosts[] VALUES = values();
    public final String name;
    public final List<Component> description;
    public final Class<?> ability;
    public final Consumer<AbstractAbility> applyBoost;

    SkillBoosts(String name, List<Component> description, Class<?> ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }

    public TextComponent getSelectedDescription() {
        TextComponent.Builder builder = Component.text();
        description.forEach(component -> builder.append(component.colorIfAbsent(NamedTextColor.GREEN)));
        return builder.build();
    }

    public TextComponent getUnselectedDescription() {
        TextComponent.Builder builder = Component.text();
        description.forEach(component -> builder.append(component.color(NamedTextColor.GRAY)));
        return builder.build();
    }

}