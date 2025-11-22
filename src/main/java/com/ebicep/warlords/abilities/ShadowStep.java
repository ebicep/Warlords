package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.ShadowStepBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ShadowStep extends AbstractAbility implements
        PurpleAbilityIcon,
        Damages<ShadowStep.DamageValues>,
        Heals<ShadowStep.HealingValues>,
        AbilityStats<ShadowStep,
                ShadowStep.ShadowStepStats> {

    private final ShadowStepStats stats = new ShadowStepStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int fallDamageNegation = 10;
    private int leapHealThreshold;
    private int guaranteedCrit;
    private float magnitude;
    private float y;
    private float magnitudeFlag;
    private float yFlag;

    public ShadowStep() {
        super(AbstractAbilityBuilder.create("shadowStep").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.fallDamageNegation = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("fallDamageNegation"), int.class);
        this.leapHealThreshold = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("leapHealThreshold"), int.class);
        this.guaranteedCrit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("guaranteedCrit"), int.class);
        this.magnitude = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("magnitude"), float.class);
        this.y = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("y"), float.class);
        this.magnitudeFlag = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("magnitudeFlag"), float.class);
        this.yFlag = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("yFlag"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Location playerLoc = wp.getLocation();
        Utils.playGlobalSound(playerLoc, "rogue.drainingmiasma.activation", 1, 2);
        Utils.playGlobalSound(playerLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 2);
        wp.setFlagPickCooldown(2);
        EffectUtils.playFirework(wp.getLocation().add(0, pveMasterUpgrade2 ? 1 : 0, 0), FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        if (wp.getCurrentHealth() < leapHealThreshold) {
            wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.leapHeal));
        }
        if (pveMasterUpgrade2) {
            doShadowDash(wp);
        } else {
            if (wp.getCarriedFlag() != null) {
                wp.setVelocity(name, playerLoc.getDirection().multiply(magnitudeFlag).setY(yFlag), true);
                wp.setFallDistance(-fallDamageNegation);
            } else {
                wp.setVelocity(name, playerLoc.getDirection().multiply(magnitude).setY(y), true);
                wp.setFallDistance(-fallDamageNegation);
            }
            doShadowStep(wp, playerLoc);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Leap forward, dealing ")
                                               .damage(damageValues.shadowStepDamage)
                                               .text(" damage to all enemies close on cast or when landing on the ground. You take reduced fall damage while leaping.")
                                               .emptyLine()
                                               .text("If you are below ")
                                               .text(leapHealThreshold, NamedTextColor.GREEN)
                                               .text(" health, you will heal for ")
                                               .heal(healingValues.leapHeal)
                                               .text(" health on cast. Guarantees at least ")
                                               .text(guaranteedCrit, NamedTextColor.RED)
                                               .text(" critical hit.")
                                               .emptyLine()
                                               .text("Shadow Step has reduced range when holding a flag.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ShadowStepBranch(abilityTree, this);
    }

    private void doShadowDash(@Nonnull WarlordsEntity wp) {
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Shadow Dash Damage Res",
                null,
                ShadowStep.class,
                new ShadowStep(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                2
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    currentDamageValue.addMultiplicativeModifierMult(name, .25f);
                }
        ));
        Set<WarlordsEntity> hit = new HashSet<>();
        AtomicInteger guaranteedCrit = new AtomicInteger(this.guaranteedCrit);
        LocationBuilder locationBuilder = new LocationBuilder(wp.getEyeLocation());
        for (Block ignored : Utils.getTargetBlockInBetween(wp.getEyeLocation(), 12)) {
            if (!Utils.getTargetBlock(locationBuilder, 1).getType().isAir() ||
                    !locationBuilder.getBlock().getType().isAir() ||
                    !locationBuilder.clone()
                                    .addY(1)
                                    .getBlock()
                                    .getType()
                                    .isAir()
            ) {
                locationBuilder.centerXZBlock();
                boolean isSlab = locationBuilder.clone().addY(-1).getBlock().getBlockData() instanceof Slab;
                locationBuilder.addY(isSlab ? -0.5 : 0);
                break;
            }
            PlayerFilter.entitiesAround(locationBuilder.clone().addY(-1), 3.5, 3.5, 3.5)
                        .aliveEnemiesOf(wp)
                        .excluding(hit)
                        .forEach(warlordsEntity -> {
                            hit.add(warlordsEntity);
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Shadow Dash")
                                    .source(wp)
                                    .value(damageValues.shadowStepDamage)
                                    .critChance(guaranteedCrit.getAndDecrement() > 0 ? 100 : damageValues.shadowStepDamage.getCritChanceValue()));
                        });
            locationBuilder = locationBuilder.forward(1);
            EffectUtils.displayParticle(Particle.SMOKE, locationBuilder.clone().addY(-.5), 10, .1, .1, .1, 0);
        }
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1.5f);
        wp.teleportLocationOnly(locationBuilder);
        if (!hit.isEmpty()) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Shadow Dash",
                    "SHDW",
                    ShadowStep.class,
                    new ShadowStep(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    5 * 20
            ).addModifier(Modifier.MODIFY_OUTGOING_CRIT_CHANCE, (event, currentCritChance) -> {
                        currentCritChance.addMultiplicativeModifierMult("Shadow Dash CC", convertToMultiplicationDecimal(Math.min(2f * hit.size(), 20)));
                    }
            ).addModifier(Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER, (event, currentCritMultiplier) -> {
                currentCritMultiplier.addMultiplicativeModifierMult("Shadow Dash CC", convertToMultiplicationDecimal(Math.min(2f * hit.size(), 20)));
                    }
            ));
        }
    }

    private void doShadowStep(@Nonnull WarlordsEntity wp, Location playerLoc) {
        List<WarlordsEntity> playersHit = new ArrayList<>();
        AtomicInteger guaranteedCrit = new AtomicInteger(this.guaranteedCrit);
        for (WarlordsEntity assaultTarget : PlayerFilter
                .entitiesAround(wp, 5, 5, 5)
                .aliveEnemiesOf(wp)
        ) {
            stats.totalTargetsHit++;
            assaultTarget.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.shadowStepDamage)
                    .critChance(guaranteedCrit.getAndDecrement() > 0 ? 100 : damageValues.shadowStepDamage.getCritChanceValue())
            );
            Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
            if (!pveMasterUpgrade) {
                playersHit.add(assaultTarget);
            }
        }
        new GameRunnable(wp.getGame()) {

            int counter = 0;

            @Override
            public void run() {
                counter++;
                // if player never lands in the span of 10 seconds, remove damage.
                if (counter == 200 || wp.isDead()) {
                    this.cancel();
                }
                wp.getLocation(playerLoc);
                boolean hitGround = wp.getEntity().isOnGround() || wp.onHorse();
                if (hitGround) {
                    for (WarlordsEntity landingTarget : PlayerFilter
                            .entitiesAround(wp, 5, 5, 5)
                            .aliveEnemiesOf(wp)
                            .excluding(playersHit)
                    ) {
                        stats.totalTargetsHit++;
                        landingTarget.addInstance(InstanceBuilder
                                .damage()
                                .ability(ShadowStep.this)
                                .source(wp)
                                .value(damageValues.shadowStepDamage)
                                .critChance(guaranteedCrit.getAndDecrement() > 0 ? 100 : damageValues.shadowStepDamage.getCritChanceValue())
                        );
                        Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
                    }
                    if (pveMasterUpgrade) {
                        pveMasterOnLand(wp);
                    }
                    EffectUtils.playFirework(wp.getLocation(), FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
                    this.cancel();
                }
            }
        }.runTaskTimer(10, 0);
    }

    private void pveMasterOnLand(WarlordsEntity we) {
        we.addSpeedModifier(we, name, 80, 5 * 20);
        we.addKnockbackModifier(we, name, -80, 5 * 20);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public ShadowStepStats getAbilityStats() {
        return stats;
    }

    public void setFallDamageNegation(int fallDamageNegation) {
        this.fallDamageNegation = fallDamageNegation;
    }

    public void setLeapHealThreshold(int leapHealThreshold) {
        this.leapHealThreshold = leapHealThreshold;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable shadowStepDamage = new Value.RangedValueCritable(466, 598, 15, 175);

        private List<Value> values = List.of(shadowStepDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.shadowStepDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("shadowStepDamage"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(shadowStepDamage);
        }

        public Value.RangedValueCritable getShadowStepDamage() {
            return shadowStepDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue leapHeal = new Value.SetValue(600);

        private List<Value> values = List.of(leapHeal);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.leapHeal = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("leapHeal"),
                    Value.SetValue.class
            );
            this.values = List.of(leapHeal);
        }

        public Value.SetValue getLeapHeal() {
            return leapHeal;
        }

    }

    public static class ShadowStepStats extends AbstractAbilityStats<ShadowStep, ShadowStepStats> {

        @Field("total_targets_hit")
        private int totalTargetsHit = 0;

        @Override
        public Class<ShadowStepStats> getClazz() {
            return ShadowStepStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", totalTargetsHit));
            return statsDisplay;
        }

        @Override
        public ShadowStepStats merge(ShadowStepStats other, int multiplier) {
            ShadowStepStats stats = super.merge(other, multiplier);
            stats.totalTargetsHit = this.totalTargetsHit + other.totalTargetsHit * multiplier;
            return stats;
        }

        @Override
        public ShadowStepStats create() {
            return new ShadowStepStats();
        }

    }

}
