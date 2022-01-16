package com.ebicep.warlords.player;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;

import java.util.function.Consumer;

public enum ClassesSkillBoosts {
    FIREBALL("Fireball",
            "§7Increases the damage you\n§7deal with Fireball by 20%",
            "§aIncreases the damage you\n§adeal with Fireball by §c20%",
            Fireball.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    FLAME_BURST("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720% and reduce the cooldown\n§7by 20%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20% §aand reduce the cooldown\n§aby §c20%",
            FlameBurst.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            "§7Increase the amount of health you\n§7restore with Time Warp by §715% §7and\n§7reduce the cooldown by 30%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c15% §aand\n§areduce the cooldown by §c30%.",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    ((TimeWarp) abstractAbility).setWarpHealPercentage(45);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT TIME WARP PYRO");
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 10% and reduce\n§7the cooldown by 50%",
            "§aIncrease the amount of health\n§aconverted to shield by §c10% §aand reduce\n§athe cooldown by §c50%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(60);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT ARCANE");
                }
            }
    ),
    INFERNO("Inferno",
            "§7Reduce the cooldown of Inferno\n§7by 25%",
            "§aReduce the cooldown of Inferno\n§aby §c25%",
            Inferno.class,
            abstractAbility -> {

                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    FROST_BOLT("Frostbolt",
            "§7Increases the damage you\n§7deal with Frostbolt by\n§720%",
            "§aIncreases the damage you\n§adeal with Frostbolt by\n§c20%",
            FrostBolt.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            "§7Increase the damage you\n§7deal with Freezing Breath\n§7by 25% and reduce the cooldown\n§7by 15%",
            "§aIncrease the damage you\n§adeal with Freezing Breath\n§aby §c25% §aand reduce the cooldown\n§aby §c15%",
            FreezingBreath.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            "§7Increase the amount of health you\n§7restore with Time Warp by §75% §7and\n§7reduce the cooldown by 40%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c5% §aand\n§areduce the cooldown by §c40%.",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    ((TimeWarp) abstractAbility).setWarpHealPercentage(35);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT TIME WARP PYRO");
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 25%",
            "§aIncrease the amount of health\n§aconverted to shield by §c25%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(75);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT ARCANE");
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            "§7Increase the amount damage you\n§7reduce with Ice Barrier by\n§710% §7and reduce the cooldown by 20%",
            "§aIncrease the amount damage you\n§areduce with Ice Barrier by\n§c10% §aand reduce the cooldown by §c20%",
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier) {
                    ((IceBarrier) abstractAbility).setDamageReductionPercent(60);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT BARRIER");
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            "§7Increases the amount of\n§7health you restore with\n§7Water Bolt by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Bolt by §c20%",
            WaterBolt.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    WATER_BREATH("Water Breath",
            "§7Increases the amount of\n§7health you restore with\n§7Water Breath by 25%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Breath by §c25%",
            WaterBreath.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            "§7Increase the amount of health you\n§7restore with Time Warp by §75% and\n§7reduce the cooldown by 40%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c5% §aand\n§areduce the cooldown by §c40%.",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    ((TimeWarp) abstractAbility).setWarpHealPercentage(35);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT TIME WARP PYRO");
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 15% and reduce\n§7the cooldown by 40%",
            "§aIncrease the amount of health\n§aconverted to shield by §c15% §aand reduce\n§athe cooldown by §c40%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(65);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT ARCANE");
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            "§7Increases the duration of\n§7Healing Rain by 4 seconds and\n§7reduce the cooldown by 20%",
            "§aIncreases the duration of\n§aHealing Rain by §c4 §aseconds and\n§areduce the cooldown by §c20%",
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain) {
                    ((HealingRain) abstractAbility).setDuration(16);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT HEALING RAIN");
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§720% and reduce the cooldown\n§7by 25%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c20% §aand reduce the cooldown\n§aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§720% §7and reduce the cooldown\nby §740%",
            "§aIncrease the damage you\n§adeal with Ground Slam by\n§c20% §aand reduce the cooldown\nby §c40%",
            GroundSlam.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
            }
    ),
    BLOOD_LUST("Blood Lust",
            "§7Increase the amount of damage\n§7you convert into healing with\n§7Blood Lust by 5% and reduce the\n§7cooldown by 20%",
            "§aIncrease the amount of damage\n§ayou convert into healing with\n§aBlood Lust by §c5% §aand reduce the\n§acooldown by §c20%",
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust) {
                    ((BloodLust) abstractAbility).setDamageConvertPercent(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT BLOODLUST");
                }
            }
    ),
    BERSERK("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 25%",
            "§aReduce the cooldown of Berserk\n§aby §c25%",
            Berserk.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeDefender.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            "§7Increase the amount knockback you\n§7deal with Seismic Wave by\n§735% and reduce the cooldown\n§7by 25%",
            "§aIncrease the amount knockback you\n§adeal with Seismic Wave by\n§c35% §aand reduce the cooldown\n§aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    ((SeismicWave) abstractAbility).setVelocity(1.8f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT LAST STAND");
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            "§7Reduce the cooldown of Ground Slam\n§7by 25%",
            "§aReduce the cooldown of Ground Slam\n§aby §c25%",
            GroundSlam.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    INTERVENE("Intervene",
            "§7Reduce the cooldown of Intervene\n§7by 10%",
            "§aReduce the cooldown of Intervene\n§aby §c10%",
            Intervene.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
            }
    ),
    LAST_STAND("Last Stand",
            "§7Increase the amount damage you\n§7reduce with Last Stand by\n§710% §7and reduce the cooldown by 20%",
            "§aIncrease the amount damage you\n§areduce with Last Stand by\n§c10% §aand reduce the cooldown by §c20%",
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand) {
                    ((LastStand) abstractAbility).setSelfDamageReductionPercent(60);
                    ((LastStand) abstractAbility).setTeammateDamageReductionPercent(50);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT LAST STAND");
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            "§7Increase the damage you\n§7deal with Crippling Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crippling Strike\n§aby §c20%",
            CripplingStrike.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            "§7Increase the immobilize duration\n§7of your Reckless Charge by\n§70.25 seconds and reduce the\n§7cooldown by 25%",
            "§aIncrease the immobilize duration\n§aof your Reckless Charge by\n§c0.25 §aseconds and reduce the\n§acooldown by §c25%",
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge) {
                    ((RecklessCharge) abstractAbility).setStunTimeInTicks(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT LAST STAND");
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            "§7Reduce the cooldown of Ground Slam\n§7by 40%",
            "§aReduce the cooldown of Ground Slam\n§aby §c40%",
            GroundSlam.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            "§7Increases the amount of\n§7health you restore with\n§7Orbs of Life by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aOrbs of Life by §c20%",
            OrbsOfLife.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    UNDYING_ARMY("Undying Army",
            "§7Increase the amount of allies\naffected by 2 §7and reduce the\n§7cooldown by 25%",
            "§aIncrease the amount of allies\n§aaffected by §c2 §aand reduce the\n§acooldown by §c25%",
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy) {
                    ((UndyingArmy) abstractAbility).setMaxArmyAllies(8);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT UNDYING ARMY");
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            "§7Increase the damage you\n§7deal with Avenger's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Avenger's Strike\n§aby §c20%",
            AvengersStrike.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            "§7Removes the energy cost of\n§7Consecrate and\n§7reduce the cooldown\n§7by 40%",
            "§aRemoves the energy cost of\n§aConsecrate and\n§areduce the cooldown\n§aby §c40%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT CONSECRATE AVE");
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 35% and increase the speed\n§7duration by 2 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c35% §aand increase the speed\n§aduration by §c2 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusion) abstractAbility).setDuration(5);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT INFUSION AVE");
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 40%",
            "§aReduce the cooldown of Holy Radiance\n§aby §c40%",
            HolyRadiance.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            "§7Reduce the cooldown of Avenger's Wrath\n§7by 25% and increase the duration by\n§73 seconds",
            "§aReduce the cooldown of Avenger's Wrath\n§aby §c25% §aand increase the duration §aby\n§c3 §aseconds",
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath) {
                    ((AvengersWrath) abstractAbility).setDuration(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT INFUSION AVE");
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            "§7Increase the damage you\n§7deal with Crusader's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crusader's Strike\n§aby §c20%",
            CrusadersStrike.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            "§7Removes the energy cost of\n§7Consecrate and\n§7reduce the cooldown\n§7by 40%",
            "§aRemoves the energy cost of\n§aConsecrate and\n§areduce the cooldown\n§aby §c40%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT CONSECRATE CRUS");
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 30% and increase the speed\n§7duration by 3 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c30% §aand increase the speed\n§aduration by §c3 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                    ((LightInfusion) abstractAbility).setDuration(6);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT INFUSION CRUS");
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 20% and increase the duration\n§7of Crusader's Mark by 4 seconds",
            "§aReduce the cooldown of Holy Radiance\n§aby §c20% §aand increase the duration\n§aof Crusader's Mark by §c4 §aseconds",
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                    ((HolyRadianceCrusader) abstractAbility).setMarkDuration(12);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT HOLY CRUS");
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            "§7Reduce the cooldown of Inspiring\n§7Presence by 25%",
            "§aReduce the cooldown of Inspiring\n§aPresence by §c25%",
            InspiringPresence.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            "§7Increase the amount of damage\n§7you convert into healing with\n§7Protector's Strike by 20%",
            "§aIncrease the amount of damage\n§ayou convert into healing with\n§aProtector's Strike by §c20%",
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike) {
                    ((ProtectorsStrike) abstractAbility).setConvertPercent(120);
                    ((ProtectorsStrike) abstractAbility).setSelfConvertPercent(60);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            "§7Increases the range of\n§7Consecrate by 4 blocks and\n§7reduce the cooldown\n§7by 40%",
            "§aIncreases the range of\n§aConsecrate by §c4 §ablocks and\n§areduce the cooldown\n§aby §c40%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    ((Consecrate) abstractAbility).setRadius(8);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT CONSECRATE PROT");
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 35% and increase the speed\n§7duration by 2 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c35% §aand increase the speed\n§aduration by §c2 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusion) abstractAbility).setDuration(5);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT INFUSION PROT");
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            "§7Increases the amount of\n§7health you restore with\n§7Holy Radiance by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHoly Radiance by §c20%",
            HolyRadianceProtector.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            "§7Increases the amount of\n§7health you restore with\n§7Hammer of Light by 20% and\nreduce the cooldown by 25%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHammer of Light by §c20% §aand\nreduce the cooldown by §c25%",
            HammerOfLight.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            "§7Increase the damage you\n§7deal with Lightning Bolt by\n§720%",
            "§aIncrease the damage you\n§adeal with Lightning Bolt by\n§c20%",
            LightningBolt.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            "§7Increase the damage you\n§7deal with Chain Lightning\n§7by 20% and reduce the cooldown\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Chain Lightning\n§aby §c20% §aand reduce the cooldown\n§aby §c20%",
            ChainLightning.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            "§7Increase the damage you\n§7deal with Windfury Weapon\n§7by 20% and increase the proc\n§7chance by 20%",
            "§aIncrease the damage you\n§adeal with Windfury Weapon\n§aby §c20% §aand increase the proc\n§achance by §c20%",
            Windfury.class,
            abstractAbility -> {
                if (abstractAbility instanceof Windfury) {
                    ((Windfury) abstractAbility).setProcChance(55);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT WINDFURY");
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            "§7Reduce the cooldown of Lightning Rod\n§7by 40%",
            "§aReduce the cooldown of Lightning Rod\n§aby §c40%",
            LightningRod.class,
            abstractAbility -> {
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            "§7Increase the damage you\n§7deal with Capacitor Totem\n§7by 20% and increase the duration\n§7by 4 seconds",
            "§aIncrease the damage you\n§adeal with Capacitor Totem\n§aby §c20% §aand increase the duration\n§aby §c4 §aseconds",
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem) {
                    ((CapacitorTotem) abstractAbility).setDuration(12);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT WINDFURY");
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            "§7Increase the damage you\n§7deal with Fallen Souls by\n§720%",
            "§aIncrease the damage you\n§adeal with Fallen Souls by\n§c20%",
            FallenSouls.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    SPIRIT_LINK("Spirit Link",
            "§7Increase the damage you\n§7deal with Spirit Link by\n§725%",
            "§aIncrease the damage you\n§adeal with Spirit Link by\n§c25%",
            SpiritLink.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            "§7Increase the duration of\n§7binds by 1 second.",
            "§aIncrease the duration of\n§abinds by §c1 §asecond.",
            Soulbinding.class,
            abstractAbility -> {
                if (abstractAbility instanceof Soulbinding) {
                    ((Soulbinding) abstractAbility).setBindDuration(3);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT SOULBINDING");
                }
            }
    ),
    REPENTANCE("Repentance",
            "§7Increase the damage you\n§7convert by 5% and reduce the\n§7cooldown by 10%",
            "§aIncrease the damage you\n§aconvert by §c5% §aand reduce the\n§acooldown by §c10%",
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance) {
                    ((Repentance) abstractAbility).setDamageConvertPercent(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT REPENTENCE");
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            "§7Increase the range of Death's Debt\n§7by 4 blocks and reduce the\n§7amount of delayed damage you take\n§7by 25%",
            "§aIncrease the range of Death's Debt\n§aby §c4 §ablocks and reduce the\n§aamount of delayed damage you take\n§aby §c25%",
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt) {
                    ((DeathsDebt) abstractAbility).setRespiteRadius(14);
                    ((DeathsDebt) abstractAbility).setDebtRadius(12);
                    ((DeathsDebt) abstractAbility).setSelfDamageInPercentPerSecond(.125f);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT DEBT");
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            "§7Increase the damage you\n§7deal with Earthen Spike by\n§720%",
            "§aIncrease the damage you\n§adeal with Earthen Spike by\n§c20%",
            EarthenSpike.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    BOULDER("Boulder",
            "§7Increase the damage you\n§7deal with Boulder by 20%",
            "§aIncrease the damage you\n§adeal with Boulder by §c20%",
            Boulder.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            "§7Increase the amount of health you\n§7restore with Earthliving Weapon\n§7by 10% and increase the proc\n§7chance by 20%",
            "§aIncrease the amount of health you\n§arestore with Earthliving Weapon\n§aby §c10% §aand increase the proc\n§achance by §c20%",
            Earthliving.class,
            abstractAbility -> {
                if (abstractAbility instanceof Earthliving) {
                    ((Earthliving) abstractAbility).setProcChance(60);
                } else {
                    System.out.println("ERROR APPLY SKILL BOOST NOT EARTHLIVING");
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            "§7Increases the amount of\n§7health you restore with\n§7Chain Heal by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aChain Heal by §c20%",
            ChainHeal.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
            }
    ),
    HEALING_TOTEM("Healing Totem",
            "§7Increase the amount of health you\n§7restore with Healing Totem\n§7by 20% and reduce the\n§7cooldown by 25%",
            "§aIncrease the amount of health you\n§arestore with Healing Totem\n§aby §c20% §aand reduce the\n§acooldown by §c25%",
            HealingTotem.class,
            abstractAbility -> {
                abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
            }
    );

    public final String name;
    public final String description;
    public final String selectedDescription;
    public final Class ability;
    public final Consumer<AbstractAbility> applyBoost;

    ClassesSkillBoosts(String name, String description, String selectedDescription, Class ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.selectedDescription = selectedDescription;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }
}