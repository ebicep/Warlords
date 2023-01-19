package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;

import java.util.function.Consumer;

public enum SkillBoosts {
    FIREBALL("Fireball",
            "§7Increases the damage you deal with Fireball by 20%",
            "§aIncreases the damage you deal with Fireball by §c20%",
            Fireball.class,
            abstractAbility -> {
                if (abstractAbility instanceof Fireball) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    FLAME_BURST("Flame Burst",
            "§7Increases the damage you deal with Flame Burst by 25% and reduce the energy cost by 40",
            "§aIncreases the damage you deal with Flame Burst by §c25% §aand reduce the energy cost by §c40",
            FlameBurst.class,
            abstractAbility -> {
                if (abstractAbility instanceof FlameBurst) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                }
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            "§7Increase the amount of health you restore with Time Warp by 10% and reduce the cooldown by 50%.",
            "§aIncrease the amount of health you restore with Time Warp by §c10% §aand reduce the cooldown by §c50%.",
            TimeWarpPyromancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    ((TimeWarp) abstractAbility).setWarpHealPercentage(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            "§7Remove the energy cost of Arcane Shield and reduce the cooldown by 30%",
            "§aRemove the energy cost of Arcane Shield §aand reduce the cooldown by §c30%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    INFERNO("Inferno",
            "§7Increase the Crit Multiplier bonus of Inferno by 60%",
            "§aIncrease the Crit Multiplier bonus of Inferno by §c60%",
            Inferno.class,
            abstractAbility -> {
                if (abstractAbility instanceof Inferno) {
                    ((Inferno) abstractAbility).setCritMultiplierIncrease(((Inferno) abstractAbility).getCritMultiplierIncrease() + 60);
                }
            }
    ),
    FROST_BOLT("Frostbolt",
            "§7Increases the damage you deal with Frostbolt by 20% and increase the slowness by 5%",
            "§aIncreases the damage you deal with Frostbolt by §c20% §aand increase the slowness by §c5%",
            FrostBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof FrostBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    ((FrostBolt) abstractAbility).setSlowness(((FrostBolt) abstractAbility).getSlowness() + 5);
                }
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            "§7Increase the damage you deal with Freezing Breath by 20% and reduce the cooldown by 20%",
            "§aIncrease the damage you deal with Freezing Breath §aby §c20% §aand reduce the cooldown §aby §c20%",
            FreezingBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof FreezingBreath) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            "§7Reduce the cooldown of Time Warp §7by 40%",
            "§aReduce the cooldown of Time Warp §aby §c40%",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            "§7Reduce the cooldown of Arcane Shield §7by 30%",
            "§aReduce the cooldown of Arcane Shield §aby §c30%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            "§7Increase the amount damage you reduce with Ice Barrier by §75% §7and increase the duration by 2 seconds",
            "§aIncrease the amount damage you reduce with Ice Barrier by §c5% §aand increase the duration by §c2 §aseconds",
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier) {
                    ((IceBarrier) abstractAbility).setDamageReductionPercent(55);
                    ((IceBarrier) abstractAbility).setDuration(((IceBarrier) abstractAbility).getDuration() + 2);
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            "§7Increases the amount of health you restore with Water Bolt by 20%",
            "§aIncreases the amount of health you restore with Water Bolt by §c20%",
            WaterBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    WATER_BREATH("Water Breath",
            "§7Increases the amount of health you restore with Water Breath by 15% and reduce the energy cost by 30",
            "§aIncreases the amount of health you restore with Water Breath by §c15% §aand reduce the energy cost by §c30",
            WaterBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBreath) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 30);
                }
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            "§7Reduce the cooldown of Time Warp §7by 50%",
            "§aReduce the cooldown of Time Warp §aby §c50%",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            "§7Remove the energy cost of Arcane Shield and reduce the cooldown by 30%",
            "§aRemove the energy cost of Arcane Shield §aand reduce the cooldown by §c30%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            "§7Increases the duration of §7Healing Rain by 4 seconds and §7reduce the cooldown by 20%",
            "§aIncreases the duration of §aHealing Rain by §c4 §aseconds and §areduce the cooldown by §c20%",
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain) {
                    ((HealingRain) abstractAbility).setDuration(16);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            "§7Increase the damage you §7deal with Wounding Strike §7by 10% and reduce the energy cost by 10",
            "§aIncrease the damage you §adeal with Wounding Strike §aby §c10% §aand reduce the energy cost by §c10",
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeBerserker) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 10);
                }
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            "§7Increase the damage you §7deal with Seismic Wave by §715% and reduce the cooldown §7by 25%",
            "§aIncrease the damage you §adeal with Seismic Wave by §c15% §aand reduce the cooldown §aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            "§7Increase the damage you §7deal with Ground Slam by §735% §7and reduce the cooldown by §710%",
            "§aIncrease the damage you §adeal with Ground Slam by §c35% §aand reduce the cooldown by §c10%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.35f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.35f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    BLOOD_LUST("Blood Lust",
            "§7Reduce the cooldown of Blood Lust by 30% and increase damage converted to healing by 5%",
            "§aReduce the cooldown of Blood Lust §aby §c30% §aand increase damage converted to healing by §c5%",
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust) {
                    ((BloodLust) abstractAbility).setDamageConvertPercent(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    BERSERK("Berserk",
            "§7Increase the damage bonus of Berserk §7by 15% and increase the speed §7by 10%",
            "§aIncrease the damage bonus of Berserk §aby §c15% §aand increase the speed §aby §c10%",
            Berserk.class,
            abstractAbility -> {
                if (abstractAbility instanceof Berserk) {
                    ((Berserk) abstractAbility).setDamageIncrease(((Berserk) abstractAbility).getDamageIncrease() + 15);
                    ((Berserk) abstractAbility).setSpeedBuff(((Berserk) abstractAbility).getSpeedBuff() + 10);
                }
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            "§7Increase the damage you §7deal with Wounding Strike §7by 10% and increase wounding by 25%",
            "§aIncrease the damage you §adeal with Wounding Strike §aby §c10% §aand increase wounding by §c25%",
            WoundingStrikeDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeDefender) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                    ((WoundingStrikeDefender) abstractAbility).setWounding(((WoundingStrikeDefender) abstractAbility).getWounding() + 25);
                }
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            "§7Increase the amount knockback you §7deal with Seismic Wave by §735% and reduce the cooldown §7by 25%",
            "§aIncrease the amount knockback you §adeal with Seismic Wave by §c35% §aand reduce the cooldown §aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    ((SeismicWave) abstractAbility).setVelocity(1.8f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            "§7Increase the amount of knockback you deal with Ground Slam by 10% and reduce the cooldown by 20%",
            "§aIncrease the amount of knockback you deal with Ground Slam by §c10% §aand reduce the cooldown by §c20%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    ((GroundSlam) abstractAbility).setVelocity(1.35f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    INTERVENE("Intervene",
            "§7Increase the cast and break radius §7of Intervene by 5 blocks and increase §7the max amount of damage you can §7absorb by 400",
            "§aIncrease the cast and break radius §aof Intervene by §c5 §ablocks and increase §athe max amount of damage you can §aabsorb by §c400",
            Intervene.class,
            abstractAbility -> {
                if (abstractAbility instanceof Intervene) {
                    ((Intervene) abstractAbility).setMaxDamagePrevented(4000);
                    ((Intervene) abstractAbility).setRadius(15);
                    ((Intervene) abstractAbility).setBreakRadius(20);
                }
            }
    ),
    LAST_STAND("Last Stand",
            "§7Increase the amount damage you §7reduce with Last Stand by §75% §7and reduce the cooldown by 10%",
            "§aIncrease the amount damage you §areduce with Last Stand by §c5% §aand reduce the cooldown by §c10%",
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand) {
                    ((LastStand) abstractAbility).setSelfDamageReductionPercent(55);
                    ((LastStand) abstractAbility).setTeammateDamageReductionPercent(45);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            "§7Increase the damage you §7reduce with Crippling Strike §7by 10% and increase the additional reduction per strike by 5%",
            "§aIncrease the damage you §areduce with Crippling Strike §aby §c10% §aand increase the additional reduction per strike by §c5%",
            CripplingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CripplingStrike) {
                    ((CripplingStrike) abstractAbility).setCripple(((CripplingStrike) abstractAbility).getCripple() + 10);
                    ((CripplingStrike) abstractAbility).setCripplePerStrike(((CripplingStrike) abstractAbility).getCripplePerStrike() + 5);
                }
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            "§7Increase the immobilize duration §7of your Reckless Charge by §70.3 seconds and reduce the §7cooldown by 30%",
            "§aIncrease the immobilize duration §aof your Reckless Charge by §c0.3 §aseconds and reduce the §acooldown by §c30%",
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge) {
                    ((RecklessCharge) abstractAbility).setStunTimeInTicks(16);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            "§7Reduce the cooldown of Ground Slam §7by 40%",
            "§aReduce the cooldown of Ground Slam §aby §c40%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            "§7Increases the amount of §7health you restore with §7Orbs of Life by 20%",
            "§aIncreases the amount of §ahealth you restore with §aOrbs of Life by §c20%",
            OrbsOfLife.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrbsOfLife) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    UNDYING_ARMY("Undying Army",
            "§7Reduce the damage of Undying Army after dying by 5% and increase the duration by 5 seconds",
            "§aReduce the damage of Undying Army after dying by §c5% §aand increase the duration by §c5 §aseconds",
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy) {
                    ((UndyingArmy) abstractAbility).setDuration(15);
                    ((UndyingArmy) abstractAbility).setMaxHealthDamage(((UndyingArmy) abstractAbility).getMaxHealthDamage() - 5);
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            "§7Increase the damage you §7deal with Avenger's Strike §7by 15% and increase the energy you steal by 5",
            "§aIncrease the damage you §adeal with Avenger's Strike §aby §c15% §aand increase the energy you steal by §c5",
            AvengersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    ((AvengersStrike) abstractAbility).setEnergySteal(((AvengersStrike) abstractAbility).getEnergySteal() + 5);
                }
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            "§7Remove the energy cost of Consecrate and increase the damage by 35%",
            "§aRemove the energy cost of Consecrate and increase the damage by §c35%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.35f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.35f);
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion §7by 25% and increase the energy you restore by 40",
            "§aReduce the cooldown of Light Infusion §aby §c25% §aand increase the energy §ayou restore by §c40",
            LightInfusionAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionAvenger) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    ((LightInfusionAvenger) abstractAbility).setEnergyGiven(((LightInfusionAvenger) abstractAbility).getEnergyGiven() + 40);
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance §7by 20% and double the energy drain of Avenger's Mark",
            "§aReduce the cooldown of Holy Radiance §aby §c20% §aand double the energy drain of Avenger's Mark",
            HolyRadianceAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceAvenger) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                    ((HolyRadianceAvenger) abstractAbility).setEnergyDrainPerSecond(((HolyRadianceAvenger) abstractAbility).getEnergyDrainPerSecond() * 2);
                }
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            "§7Increase the energy per second of Avenger's Wrath by 10 and increase the duration by 5 seconds",
            "§aIncrease the energy per second of Avenger's Wrath by §c10 §aand increase the duration §aby §c5 §aseconds",
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath) {
                    ((AvengersWrath) abstractAbility).setDuration(((AvengersWrath) abstractAbility).getDuration() + 5);
                    ((AvengersWrath) abstractAbility).setEnergyPerSecond(((AvengersWrath) abstractAbility).getEnergyPerSecond() + 10);
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            "§7Increase the damage you §7deal with Crusader's Strike §7by 20%",
            "§aIncrease the damage you §adeal with Crusader's Strike §aby §c20%",
            CrusadersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CrusadersStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            "§7Remove the energy cost of Consecrate and increase the damage §7by 35%",
            "§aRemove the energy cost of Consecrate and increase the damage §aby §c35%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.35f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.35f);
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion §7by 35% and increase the speed §7duration by 3 seconds",
            "§aReduce the cooldown of Light Infusion §aby §c35% §aand increase the speed §aduration by §c3 §aseconds",
            LightInfusionCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionCrusader) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusionCrusader) abstractAbility).setDuration(((LightInfusionCrusader) abstractAbility).getDuration() + 3);
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance §7by 25%, increase the duration §7of Crusader's Mark by 4 seconds and speed bonus by 15%",
            "§aReduce the cooldown of Holy Radiance §aby §c25%§a, increase the duration §aof Crusader's Mark by §c4 §aseconds and speed bonus by §c15%",
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    ((HolyRadianceCrusader) abstractAbility).setMarkDuration(12);
                    ((HolyRadianceCrusader) abstractAbility).setMarkSpeed(((HolyRadianceCrusader) abstractAbility).getMarkSpeed() + 15);
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            "§7Reduce the cooldown of Inspiring §7Presence by 25% and increase the speed by 10%",
            "§aReduce the cooldown of Inspiring §aPresence by §c25% §aand increase the speed by §c10%",
            InspiringPresence.class,
            abstractAbility -> {
                if (abstractAbility instanceof InspiringPresence) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    ((InspiringPresence) abstractAbility).setSpeedBuff(((InspiringPresence) abstractAbility).getSpeedBuff() + 10);
                }
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            "§7Increase the amount of damage §7you convert into healing for allies with §7Protector's Strike by 10% and heal 1 more ally.",
            "§aIncrease the amount of damage §ayou convert into healing for allies with §aProtector's Strike by §c10% §aand heal §c1 §amore ally.",
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike) {
                    ((ProtectorsStrike) abstractAbility).setMinConvert(((ProtectorsStrike) abstractAbility).getMinConvert() + 10);
                    ((ProtectorsStrike) abstractAbility).setMaxConvert(((ProtectorsStrike) abstractAbility).getMaxConvert() + 10);
                    ((ProtectorsStrike) abstractAbility).setMaxAllies(((ProtectorsStrike) abstractAbility).getMaxAllies() + 1);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            "§7Increases the range of §7Consecrate by 2 blocks and §7reduce the cooldown §7by 30%",
            "§aIncreases the range of §aConsecrate by §c2 §ablocks and §areduce the cooldown §aby §c30%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    ((Consecrate) abstractAbility).setRadius(((Consecrate) abstractAbility).getRadius() + 2);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            "§7Reduce the cooldown of Light Infusion §7by 35% and increase the speed §7duration by 3 seconds",
            "§aReduce the cooldown of Light Infusion §aby §c35% §aand increase the speed §aduration by §c3 §aseconds",
            LightInfusionProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionProtector) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusionProtector) abstractAbility).setDuration(6);
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            "§7Increases the amount of §7health you restore with §7Holy Radiance by 20%",
            "§aIncreases the amount of §ahealth you restore with §aHoly Radiance by §c20%",
            HolyRadianceProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceProtector) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            "§7Increases the amount of §7health you restore with §7Hammer of Light by 25% and reduce the cooldown by 25%",
            "§aIncreases the amount of §ahealth you restore with §aHammer of Light by §c25% §aand reduce the cooldown by §c25%",
            HammerOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof HammerOfLight) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            "§7Increase the damage you §7deal with Lightning Bolt by §720%",
            "§aIncrease the damage you §adeal with Lightning Bolt by §c20%",
            LightningBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            "§7Increase the damage you §7deal with Chain Lightning §7by 20% and reduce the cooldown by 15%",
            "§aIncrease the damage you §adeal with Chain Lightning §aby §c20% §aand reduce the cooldown by §c15%",
            ChainLightning.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainLightning) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            "§7Increase the damage you §7deal with Windfury Weapon §7by 40% and increase the proc §7chance by 20%",
            "§aIncrease the damage you §adeal with Windfury Weapon §aby §c40% §aand increase the proc §achance by §c20%",
            Windfury.class,
            abstractAbility -> {
                if (abstractAbility instanceof Windfury) {
                    ((Windfury) abstractAbility).setProcChance(55);
                    ((Windfury) abstractAbility).setWeaponDamage(((Windfury) abstractAbility).getWeaponDamage() + 40);
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            "§7Reduce the cooldown of Lightning Rod §7by 40%",
            "§aReduce the cooldown of Lightning Rod §aby §c40%",
            LightningRod.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningRod) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            "§7Increase the damage you §7deal with Capacitor Totem §7by 30% and reduce the cooldown §7by 15%",
            "§aIncrease the damage you §adeal with Capacitor Totem §aby §c30% §aand reduce the cooldown §aby §c15%",
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.3f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.3f);
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            "§7Increase the damage you §7deal with Fallen Souls by §720%",
            "§aIncrease the damage you §adeal with Fallen Souls by §c20%",
            FallenSouls.class,
            abstractAbility -> {
                if (abstractAbility instanceof FallenSouls) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SPIRIT_LINK("Spirit Link",
            "§7Increase the damage you deal with Spirit Link by 25% and increase the speed duration by 0.5 seconds",
            "§aIncrease the damage you §adeal with Spirit Link by §c25% §aand increase the speed duration by §c0.5 §aseconds",
            SpiritLink.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritLink) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    ((SpiritLink) abstractAbility).setSpeedDuration(((SpiritLink) abstractAbility).getSpeedDuration() + 0.5);
                }
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            "§7Increase the duration of §7binds by 2 seconds.",
            "§aIncrease the duration of §abinds by §c2 §aseconds.",
            Soulbinding.class,
            abstractAbility -> {
                if (abstractAbility instanceof Soulbinding) {
                    ((Soulbinding) abstractAbility).setBindDuration(4);
                }
            }
    ),
    REPENTANCE("Repentance",
            "§7Increase the damage you convert by 5% and reduce the cooldown by 10%",
            "§aIncrease the damage you §aconvert by §c5% §aand reduce the §acooldown by §c10%",
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance) {
                    ((Repentance) abstractAbility).setDamageConvertPercent(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            "§7Increase the range of Death's Debt §7by 5 blocks and reduce the §7amount of delayed damage you take §7by 40%",
            "§aIncrease the range of Death's Debt §aby §c5 §ablocks and reduce the §aamount of delayed damage you take §aby §c40%",
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt) {
                    ((DeathsDebt) abstractAbility).setRespiteRadius(15);
                    ((DeathsDebt) abstractAbility).setDebtRadius(13);
                    ((DeathsDebt) abstractAbility).setSelfDamageInPercentPerSecond(.1f);
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            "§7Increase the damage you §7deal with Earthen Spike by §715% and increase the speed by 30%",
            "§aIncrease the damage you §adeal with Earthen Spike by §c15% §aand increase the speed by §c30%",
            EarthenSpike.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthenSpike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    ((EarthenSpike) abstractAbility).setSpeed(((EarthenSpike) abstractAbility).getSpeed() * 1.3f);
                }
            }
    ),
    BOULDER("Boulder",
            "§7Increase the damage you §7deal with Boulder by 25%",
            "§aIncrease the damage you §adeal with Boulder by §c25%",
            Boulder.class,
            abstractAbility -> {
                if (abstractAbility instanceof Boulder) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            "§7Increase the proc chance by of Earthliving Weapon§7 by 20%",
            "§aIncrease the proc chance by of Earthliving Weapon by §c20%",
            Earthliving.class,
            abstractAbility -> {
                if (abstractAbility instanceof Earthliving) {
                    ((Earthliving) abstractAbility).setProcChance(60);
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            "§7Increases the amount of health you restore with Chain Heal by 30%",
            "§aIncreases the amount of health you restore with Chain Heal by §c30%",
            ChainHeal.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainHeal) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.3f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.3f);
                }
            }
    ),
    HEALING_TOTEM("Healing Totem",
            "§7Increase the amount of health you restore with Healing Totem by 25% and reduce the cooldown by 25%",
            "§aIncrease the amount of health you restore with Healing Totem by §c25% §aand reduce the cooldown by §c25%",
            HealingTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingTotem) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    JUDGEMENT_STRIKE("Judgement Strike",
            "§7Increase the amount of damage you §7deal with Judgement Strike §7by 20%",
            "§aIncrease the amount of damage you §adeal with Judgement Strike §aby §c20%",
            JudgementStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof JudgementStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    INCENDIARY_CURSE("Incendiary Curse",
            "§7Reduce the cooldown of Incendiary Curse by 35% and increase the blind duration by 1.5 seconds.",
            "§aReduce the cooldown of Incendiary Curse by §c35% §aand increase the blind duration by §c1.5 §aseconds.",
            IncendiaryCurse.class,
            abstractAbility -> {
                if (abstractAbility instanceof IncendiaryCurse) {
                    ((IncendiaryCurse) abstractAbility).setBlindDurationInTicks(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                }
            }
    ),
    BLINDING_ASSAULT("Shadow Step",
            "§7Reduce the cooldown by Shadow Step by 40% and become temporarily immune to fall damage after leaping.",
            "§aReduce the cooldown by Shadow Step by §c40% §aand become temporarily immune to fall damage after leaping.",
            ShadowStep.class,
            abstractAbility -> {
                if (abstractAbility instanceof ShadowStep) {
                    ((ShadowStep) abstractAbility).setFallDamageNegation(1000);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    SOUL_SWITCH("Soul Switch",
            "§7Reduce the cooldown by Soul Switch by 50% and increase the range by 2 blocks.",
            "§aReduce the cooldown by Soul Switch by §c50% §aand increase the range by §c2 §ablocks",
            SoulSwitch.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulSwitch) {
                    ((SoulSwitch) abstractAbility).setRadius(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ORDER_OF_EVISCERATE("Order Of Eviscerate",
            "§7Increase the duration of Order Of Eviscerate by 4 seconds and reduce the cooldown by 30%.",
            "§aIncrease the duration of Order Of Eviscerate by §c4 §aseconds and reduce the cooldown by §c30%.",
            OrderOfEviscerate.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrderOfEviscerate) {
                    ((OrderOfEviscerate) abstractAbility).setDuration(12);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    RIGHTEOUS_STRIKE("Righteous Strike",
            "§7Increase the amount of damage you §7deal with Righteous Strike §7by 20%",
            "§aIncrease the amount of damage you §adeal with Righteous Strike §aby §c20%",
            RighteousStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof RighteousStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SOUL_SHACKLE("Soul Shackle",
            "§7Reduce the cooldown of Soul Shackle by 15% and increase the silence duration by 0.5 seconds",
            "§aReduce the cooldown of Soul Shackle by §c15% §aand increase the silence duration by §c0.5 §aseconds",
            SoulShackle.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulShackle) {
                    ((SoulShackle) abstractAbility).setMinSilenceDurationInTicks(50);
                    ((SoulShackle) abstractAbility).setMaxSilenceDurationInTicks(80);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    HEART_TO_HEART("Heart To Heart",
            "§7Reduce the cooldown of Heart ot Heart by 30% and increase the amount of health you restore by 300",
            "§aReduce the cooldown of Heart ot Heart by §c30% §aand increase the amount of health you restore by §c300",
            HeartToHeart.class,
            abstractAbility -> {
                if (abstractAbility instanceof HeartToHeart) {
                    ((HeartToHeart) abstractAbility).setHealthRestore(((HeartToHeart) abstractAbility).getHealthRestore() + 300);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    PRISM_GUARD("Prism Guard",
            "§7Increase the damage reduction of Prism Guard by 15% and increase the amount of health you restore by 300",
            "§aIncrease the damage reduction of Prism Guard by §c15% §aand increase the amount of health you restore by §c300",
            PrismGuard.class,
            abstractAbility -> {
                if (abstractAbility instanceof PrismGuard) {
                    ((PrismGuard) abstractAbility).setProjectileDamageReduction(75);
                    ((PrismGuard) abstractAbility).setBubbleHealing(((PrismGuard) abstractAbility).getBubbleHealing() + 300);
                }
            }
    ),
    VINDICATE("Vindicate",
            "§7Increase the damage reduction of Vindicate by 10% and reduce the cooldown by 25%",
            "§aIncrease the damage reduction of Vindicate by §c10% §aand reduce the cooldown by §c25%",
            Vindicate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Vindicate) {
                    ((Vindicate) abstractAbility).setVindicateDamageReduction(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    IMPALING_STRIKE("Impaling Strike",
            "§7Increase the amount of damage you §7deal with Impaling Strike §7by 10% and increase the leech duration by 5 seconds.",
            "§aIncrease the amount of damage you §adeal with Impaling Strike §aby §c10% §aand increase the leech duration by §c5 §aseconds.",
            ImpalingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ImpalingStrike) {
                    ((ImpalingStrike) abstractAbility).setLeechDuration(10);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                }
            }
    ),
    SOOTHING_PUDDLE("Soothing Elixir",
            "§7Increase the amount of health you §7restore with Soothing Elixir §7by 25%",
            "§aIncrease the amount of health you §arestore with Soothing Elixir §aby §c25%",
            SoothingElixir.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoothingElixir) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    VITALITY_LIQUOR("Vitality Liquor",
            "§7Increase the amount of health you §7restore with Vitality Liquor §7by 15% and reduce the cooldown by 30%",
            "§aIncrease the amount of health you §arestore with Vitality Liquor §aby §c15% §aand reduce the cooldown by §c30%",
            VitalityLiquor.class,
            abstractAbility -> {
                if (abstractAbility instanceof VitalityLiquor) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    ((VitalityLiquor) abstractAbility).setMinWaveHealing(((VitalityLiquor) abstractAbility).getMinWaveHealing() * 1.15f);
                    ((VitalityLiquor) abstractAbility).setMaxWaveHealing(((VitalityLiquor) abstractAbility).getMaxWaveHealing() * 1.15f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    REMEDIC_CHAINS("Remedic Chains",
            "§7Increase the amount of health you restore with Remedic Chains by 10% and increase the link break radius by 10 blocks.",
            "§aIncrease the amount of health you restore with Remedic Chains by §c10% §aand increase the link break radius by §c10 §ablocks.",
            RemedicChains.class,
            abstractAbility -> {
                if (abstractAbility instanceof RemedicChains) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                    ((RemedicChains) abstractAbility).setLinkBreakRadius(25);
                }
            }
    ),
    DRAINING_MIASMA("Draining Miasma",
            "§7Increase the leech duration of Draining Miasma by 5 seconds and reduce the cooldown by 30%",
            "§aIncrease the leech duration of Draining Miasma by §c5 §aseconds and reduce the cooldown by §c30%",
            DrainingMiasma.class,
            abstractAbility -> {
                if (abstractAbility instanceof DrainingMiasma) {
                    ((DrainingMiasma) abstractAbility).setLeechDuration(10);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),

    ;

    public static final SkillBoosts[] VALUES = values();
    public final String name;
    public final String description;
    public final String selectedDescription;
    public final Class<?> ability;
    public final Consumer<AbstractAbility> applyBoost;

    SkillBoosts(String name, String description, String selectedDescription, Class<?> ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.selectedDescription = selectedDescription;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }
}