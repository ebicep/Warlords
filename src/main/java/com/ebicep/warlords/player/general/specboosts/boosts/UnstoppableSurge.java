//package com.ebicep.warlords.player.general.specboosts.boosts;
//
//import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
//import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
//import com.ebicep.warlords.player.ingame.WarlordsEntity;
//import com.ebicep.warlords.player.ingame.WarlordsPlayer;
//import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
//import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
//import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
//import org.bukkit.event.EventHandler;
//
//import java.util.List;
//
//public class UnstoppableSurge implements SpecBoostManager.SpecBoost<UnstoppableSurge> {
//
//    private int lightInfusionHealing;
//    private float lightInfusionSpeedIncreasePercent;
//    private int lightInfusionDurationIncreaseTicks;
//    private float slowResistancePercent;
//    private float knockbackResistancePercent;
//
//    @Override
//    public void init() {
//        this.lightInfusionHealing = getValue("lightInfusionHealing", int.class);
//        this.lightInfusionSpeedIncreasePercent = getValue("lightInfusionSpeedIncreasePercent", float.class);
//        this.lightInfusionDurationIncreaseTicks = getValue("lightInfusionDurationIncreaseTicks", int.class);
//        this.slowResistancePercent = getValue("slowResistancePercent", float.class);
//        this.knockbackResistancePercent = getValue("knockbackResistancePercent", float.class);
//    }
//
//    @Override
//    public String getConfigFieldName() {
//        return "unstoppableSurge";
//    }
//
//    @Override
//    public List<Object> getVariables() {
//        return List.of(lightInfusionHealing, lightInfusionSpeedIncreasePercent, lightInfusionDurationIncreaseTicks, slowResistancePercent, knockbackResistancePercent);
//    }
//
//    @Override
//    public SpecBoostManager.Boost create() {
//        return new Boost();
//    }
//
//    @Override
//    public UnstoppableSurge get() {
//        return this;
//    }
//
//    public class Boost implements SpecBoostManager.Boost {
//
//        private WarlordsEntity warlordsEntity;
//
//        @Override
//        public void apply(WarlordsPlayer warlordsPlayer) {
//            this.warlordsEntity = warlordsPlayer;
//
//            // Apply permanent slow and knockback resistance
//            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
//                    getStringName(),
//                    "RESIST",
//                    UnstoppableSurge.Boost.class,
//                    null,
//                    warlordsPlayer,
//                    CooldownTypes.SPEC_BOOST,
//                    cooldownManager -> {},
//                    false
//            ) {
//                @Override
//                public float modifyMovement(float multiplier) {
//                    // Reduce incoming slow effects
//                    return multiplier * (1 - slowResistancePercent / 100);
//                }
//
//                @Override
//                public void multiplyKB(org.bukkit.util.Vector currentVector) {
//                    // Reduce incoming knockback
//                    currentVector.multiply(1 - knockbackResistancePercent / 100);
//                }
//            });
//        }
//
//        @Override
//        public void unapply(WarlordsPlayer warlordsPlayer) {
//            // Permanent cooldowns are removed automatically when the spec boost is unapplied.
//        }
//
//        @EventHandler
//        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
//            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
//                return;
//            }
//            if (event.getAbility() instanceof LightInfusion) {
//                // Modify Light Infusion's effects
//                LightInfusion lightInfusion = (LightInfusion) event.getAbility();
//
//                // Increase healing
//                warlordsEntity.addHealingInstance(
//                        warlordsEntity,
//                        getStringName(),
//                        lightInfusionHealing,
//                        lightInfusionHealing,
//                        0,
//                        100,
//                        0
//                );
//
//                // Increase speed and duration
//                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
//                        getStringName() + " Speed",
//                        "SPD",
//                        UnstoppableSurge.Boost.class,
//                        null,
//                        warlordsEntity,
//                        CooldownTypes.ABILITY,
//                        cooldownManager -> {},
//                        lightInfusion.getTickDuration() + lightInfusionDurationIncreaseTicks // Add duration
//                ) {
//                    @Override
//                    public float modifyMovement(float multiplier) {
//                        return multiplier * (1 + lightInfusionSpeedIncreasePercent / 100);
//                    }
//                });
//            }
//        }
//    }
//}
