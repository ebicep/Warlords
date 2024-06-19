package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.ContagiousFacadeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContagiousFacade extends AbstractAbility implements BlueAbilityIcon, Duration {

    private FloatModifiable damageAbsorption = new FloatModifiable(30);
    private int tickDuration = 100;
    private int shieldTickDuration = 100;
    private double poisonRadius = 8;
    private int speedIncrease = 40;
    private int speedIncreaseDuration = 100;

    public ContagiousFacade() {
        super("Contagious Facade", 0, 0, 30, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Cover yourself in a protective layer that absorbs ")
                               .append(Component.text(format(damageAbsorption.getCalculatedValue()) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of all incoming damage for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "))
                               .append(Component.text("\n\nReactivate the ability to increase your speed by"))
                               .append(Component.text(speedIncrease + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(format(speedIncreaseDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text("seconds and inflict "))
                               .append(Component.text("3", NamedTextColor.BLUE))
                               .append(Component.text(" stacks of Poisonous Hex on "))
                               .append(Component.text("2", NamedTextColor.RED))
                               .append(Component.text(" nearby enemies in an "))
                               .append(Component.text(format(poisonRadius), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks radius."))
                               .append(Component.text("\n\nNot reactivating the ability will grant yourself a shield equal to all the damage you have absorbed during " + name + ". Lasts "))
                               .append(Component.text(format(shieldTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "arcanist.contagiousfacade.activation", 2, 1.4f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 2, 0.7f);
        EffectUtils.playHelixAnimation(wp.getLocation().add(0, 0.25, 0), 3, Particle.CHERRY_LEAVES, 3, 20);
        AtomicDouble totalAbsorbed = new AtomicDouble(0);
        RegularCooldown<ContagiousFacade> protectiveLayerCooldown = new RegularCooldown<>(
                name,
                "FACADE",
                ContagiousFacade.class,
                new ContagiousFacade(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (!wp.isAlive()) {
                        return;
                    }

                    Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 2, 2);
                    float shieldHealth = (float) totalAbsorbed.get();
                    Shield shield = new Shield(name, shieldHealth);
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name + " Shield",
                            "SHIELD",
                            Shield.class,
                            shield,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager1 -> {
                            },
                            cooldownManager1 -> {
                            },
                            shieldTickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 3 == 0) {
                                    Location location = wp.getLocation();
                                    location.add(0, 1.5, 0);
                                    EffectUtils.displayParticle(Particle.CHERRY_LEAVES, location, 2, 0.15, 0.3, 0.15, 0.01);
                                    EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                                    EffectUtils.displayParticle(Particle.SPELL_WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                                }
                            })
                    ) {
                        @Override
                        public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                            event.getWarlordsEntity().getCooldownManager().queueUpdatePlayerNames();
                        }

                        @Override
                        public PlayerNameData addPrefixFromOther() {
                            return new PlayerNameData(
                                    Component.text((int) (shield.getShieldHealth()), NamedTextColor.YELLOW),
                                    we -> we.isTeammate(wp)
                            );
                        }
                    });
                    wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                            .append(Component.text(" Your ", NamedTextColor.GRAY))
                            .append(Component.text(name, NamedTextColor.YELLOW))
                            .append(Component.text(" is now shielding you!", NamedTextColor.GRAY))
                    );
                    if (pveMasterUpgrade) {
                        PlayerFilter.entitiesAround(wp, 4, 4, 4)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .source(wp)
                                                .value(shieldHealth)
                                        );
                                        enemy.addSpeedModifier(wp, name, -50, 60, "BASE");
                                    });
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    EffectUtils.displayParticle(
                            Particle.CRIMSON_SPORE,
                            wp.getLocation(),
                            1,
                            0.05,
                            0.1,
                            0.05,
                            0.25
                    );
                    EffectUtils.displayParticle(
                            Particle.CHERRY_LEAVES,
                            wp.getLocation(),
                            2,
                            0.15,
                            0.3,
                            0.15,
                            0
                    );
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * convertToDivisionDecimal(damageAbsorption.getCalculatedValue());
                float absorbedAmount = currentDamageValue - afterValue;
                if (pveMasterUpgrade2 && totalAbsorbed.get() + absorbedAmount >= wp.getMaxHealth()) {
                    return currentDamageValue;
                }
                totalAbsorbed.addAndGet(absorbedAmount);
                return afterValue;
            }
        };
        wp.getCooldownManager().addCooldown(protectiveLayerCooldown);
        addSecondaryAbility(
                5,
                () -> {
                    wp.getCooldownManager().removeCooldownNoForce(protectiveLayerCooldown);
                    wp.addSpeedModifier(wp, name, speedIncrease, speedIncreaseDuration, "BASE");
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 2, 2);
                    new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            poisonRadius,
                            new CircumferenceEffect(Particle.REDSTONE, Particle.REDSTONE).particlesPerCircumference(1)
                    ).playEffects();
                    for (WarlordsEntity hexTarget : PlayerFilter
                            .entitiesAround(wp, poisonRadius, poisonRadius, poisonRadius)
                            .aliveEnemiesOf(wp)
                            .closestFirst(wp)
                            .limit(2)
                    ) {
                        EffectUtils.playParticleLinkAnimation(
                                wp.getLocation(),
                                hexTarget.getLocation(),
                                180,
                                0,
                                0,
                                2
                        );
                        for (int i = 0; i < 3; i++) {
                            PoisonousHex.givePoisonousHex(wp, hexTarget);
                            EffectUtils.displayParticle(
                                    Particle.CRIMSON_SPORE,
                                    wp.getLocation(),
                                    20,
                                    0.05,
                                    0.1,
                                    0.05,
                                    0.25
                            );
                        }
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your ", NamedTextColor.GRAY))
                                .append(Component.text(name, NamedTextColor.YELLOW))
                                .append(Component.text(" has infected " + hexTarget.getName() + "!", NamedTextColor.GRAY))
                        );
                        hexTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                                .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                .append(Component.text(name, NamedTextColor.YELLOW))
                                .append(Component.text(" has infected you!", NamedTextColor.GRAY))
                        );
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(protectiveLayerCooldown)
        );
        return true;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        damageAbsorption.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ContagiousFacadeBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public FloatModifiable getDamageAbsorption() {
        return damageAbsorption;
    }

    public int getShieldTickDuration() {
        return shieldTickDuration;
    }

    public void setShieldTickDuration(int shieldTickDuration) {
        this.shieldTickDuration = shieldTickDuration;
    }
}
