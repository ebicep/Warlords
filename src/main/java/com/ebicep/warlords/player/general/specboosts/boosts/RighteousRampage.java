package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.Vindicate;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

public class RighteousRampage implements SpecBoostManager.SpecBoost<RighteousRampage> {

    private float damageReductionDecrease;
    private float soulShackleAoEDamagePercent;
    private float soulShackleAoERadius;
    private int vindicateVINDDurationTicks;
    private int vindicateDamageResistanceDurationTicks;
    private Value.RangedValue vindicateLeapDamage;
    private float vindicateLeapRadius;
    private float knockbackMagnitude;
    private float knockbackY;
    private float leapMagnitude;
    private float leapY;
    private float leapMagnitudeFlag;
    private float leapYFlag;

    @Override
    public void init() {
        this.damageReductionDecrease = getValue("damageReductionDecrease", float.class);
        this.soulShackleAoEDamagePercent = getValue("soulShackleAoEDamagePercent", float.class);
        this.soulShackleAoERadius = getValue("soulShackleAoERadius", float.class);
        this.vindicateVINDDurationTicks = getValue("vindicateVINDDurationTicks", int.class);
        this.vindicateDamageResistanceDurationTicks = getValue("vindicateDamageResistanceDurationTicks", int.class);
        this.vindicateLeapDamage = getValue("vindicateLeapDamage", Value.RangedValue.class);
        this.vindicateLeapRadius = getValue("vindicateLeapRadius", float.class);
        this.knockbackMagnitude = getValue("knockbackMagnitude", float.class);
        this.knockbackY = getValue("knockbackY", float.class);
        this.leapMagnitude = getValue("leapMagnitude", float.class);
        this.leapY = getValue("leapY", float.class);
        this.leapMagnitudeFlag = getValue("leapMagnitudeFlag", float.class);
        this.leapYFlag = getValue("leapYFlag", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "righteousRampage";
    }

    @Override
    public TextComponent getDescription() {
        return getDescriptionWithAbility(new com.ebicep.warlords.abilities.BullRush());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                damageReductionDecrease,
                soulShackleAoERadius,
                soulShackleAoEDamagePercent,
                vindicateVINDDurationTicks,
                vindicateDamageResistanceDurationTicks,
                vindicateLeapRadius,
                vindicateLeapDamage
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RighteousRampage get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getSpec().setDamageResistance(warlordsPlayer.getSpec().getDamageResistance() - damageReductionDecrease); // TODO flaot modifable
            warlordsPlayer.getAbilitiesMatching(Vindicate.class).forEach(vindicate -> {
                vindicate.setTickDuration(vindicateVINDDurationTicks);
                vindicate.setDamageReductionTickDuration(vindicateDamageResistanceDurationTicks);
            });
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                if (abilities.get(i) instanceof HeartToHeart) {
                    com.ebicep.warlords.abilities.BullRush bullRush = new com.ebicep.warlords.abilities.BullRush();
                    bullRush.init(bullRush.getBuilder());
                    abilities.set(i, bullRush);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof SoulShackle)) {
                return;
            }
            if (event.getInstanceFlags().contains(InstanceFlags.RECURSIVE)) {
                return;
            }
            PlayerFilter.entitiesAround(event.getWarlordsEntity().getLocation(), soulShackleAoERadius, soulShackleAoERadius, soulShackleAoERadius)
                        .aliveEnemiesOf(warlordsEntity)
                        .excluding(event.getWarlordsEntity())
                        .forEach(aoeTarget -> {
                            float aoeDamage = event.getValue() * (soulShackleAoEDamagePercent / 100);
                            aoeTarget.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Soul Shackle")
                                    .source(warlordsEntity)
                                    .value(aoeDamage)
                                    .flags(InstanceFlags.RECURSIVE)
                            );
                        });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown) ||
                    !cooldown.getName().equals("Vindicate Resistance") ||
                    !cooldown.getFrom().equals(warlordsEntity)
            ) {
                return;
            }
            warlordsEntity.getAbilitiesMatching(Vindicate.class).forEach(vindicate -> {
                vindicate.addSecondaryAbility(
                        5,
                        () -> {
                            warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, regularCooldown);
                            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    getStringName(),
                                    null,
                                    Boost.class,
                                    null,
                                    warlordsEntity,
                                    CooldownTypes.SPEC_BOOST,
                                    cooldownManager -> {
                                    },
                                    cooldownManager -> {
                                    },
                                    regularCooldown.getTicksLeft()
                            ) {
                                @Override
                                protected Listener getListener() {
                                    return CooldownUtils.getDebuffImmunityListener(CooldownUtils.DebuffImmunity
                                            .create(warlordsEntity)
                                            .stunPredicate()
                                    );
                                }
                            });
                            warlordsEntity.setVelocity(
                                    getStringName(),
                                    warlordsEntity
                                            .getLocation()
                                            .getDirection()
                                            .multiply(warlordsEntity.hasFlag() ? leapMagnitudeFlag : leapMagnitude)
                                            .setY(warlordsEntity.hasFlag() ? leapYFlag : leapY),
                                    true
                            );
                            new GameRunnable(warlordsEntity.getGame()) {

                                int counter = 0;

                                @Override
                                public void run() {
                                    counter++;
                                    // if player never lands in the span of 10 seconds, remove damage.
                                    if (counter == 200 || warlordsEntity.isDead()) {
                                        this.cancel();
                                    }
                                    boolean hitGround = warlordsEntity.getEntity().isOnGround() || warlordsEntity.onHorse();
                                    if (!hitGround) {
                                        return;
                                    }
                                    warlordsEntity.getKnockback().removeModifier(getStringName());
                                    warlordsEntity.getCooldownManager().removeCooldownByName(getStringName());
                                    for (WarlordsEntity landingTarget : PlayerFilter
                                            .entitiesAround(warlordsEntity, vindicateLeapRadius, vindicateLeapRadius, vindicateLeapRadius)
                                            .aliveEnemiesOf(warlordsEntity)
                                    ) {
                                        landingTarget.addInstance(InstanceBuilder
                                                .damage()
                                                .cause("Vindictive Leap")
                                                .source(warlordsEntity)
                                                .value(vindicateLeapDamage)
                                        );
                                        final Location loc = landingTarget.getLocation();
                                        final Vector v = warlordsEntity
                                                .getLocation()
                                                .toVector()
                                                .subtract(loc.toVector())
                                                .normalize()
                                                .multiply(-knockbackMagnitude)
                                                .setY(knockbackY);
                                        landingTarget.setVelocity(getStringName(), v, false);
                                        Utils.playGlobalSound(warlordsEntity.getLocation(), "warrior.revenant.orbsoflife", 2, .25f);
                                    }
                                    FireWorkEffectPlayer.playFirework(warlordsEntity.getLocation(), FireworkEffect
                                            .builder()
                                            .withColor(Color.BLACK)
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .build()
                                    );
                                    this.cancel();
                                }
                            }.runTaskTimer(10, 0);
                        },
                        false,
                        secondaryAbility -> warlordsEntity.isDead() || !warlordsEntity.getCooldownManager().hasCooldown(cooldown)
                );
            });
        }

    }

}
