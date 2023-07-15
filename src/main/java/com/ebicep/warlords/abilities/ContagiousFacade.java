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
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.ContagiousFacadeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ContagiousFacade extends AbstractAbility implements BlueAbilityIcon, Duration {

    private float damageAbsorption = 30;
    private int tickDuration = 100;
    private int shieldTickDuration = 100;
    private int speedIncrease = 30;
    private int speedIncreaseDuration = 100;

    public ContagiousFacade() {
        super("Contagious Facade", 0, 0, 30, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Cover yourself in a protective layer that absorbs ")
                               .append(Component.text(format(damageAbsorption) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of all incoming damage for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "))
                               .append(Component.text("\n\nReactivate the ability to grant yourself a shield equal to all the damage you have absorbed during " + name + ". Lasts "))
                               .append(Component.text(format(shieldTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. \n\nNot reactivating the ability will instead increase your speed by "))
                               .append(Component.text(speedIncrease + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(format(speedIncreaseDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.contagiousfacade.activation", 2, 1.5f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 2, 0.7f);
        EffectUtils.playHelixAnimation(wp.getLocation(), 3, Particle.CHERRY_LEAVES, 3, 20);
        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                wp.getLocation().add(0, 1, 0),
                3,
                new CircumferenceEffect(Particle.END_ROD, Particle.REDSTONE).particlesPerCircumference(1)
        ).playEffects();
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
                    wp.getCooldownManager().addRegularCooldown(
                            name,
                            "SHIELD",
                            Shield.class,
                            new Shield(name, shieldHealth),
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
                                    EffectUtils.displayParticle(Particle.CHERRY_LEAVES, location, 2, 0.15F, 0.3F, 0.15F, 0.01);
                                    EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001);
                                    EffectUtils.displayParticle(Particle.SPELL_WITCH, location, 1, 0.3F, 0.3F, 0.3F, 0);
                                }
                            })
                    );
                    if (pveMasterUpgrade) {
                        PlayerFilter.entitiesAround(wp, 4, 4, 4)
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> {
                                    enemy.addDamageInstance(wp, name, shieldHealth, shieldHealth, 0, 100);
                                    enemy.addSpeedModifier(wp, name, -50, 60, "BASE");
                                });
                    }
                },
                tickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * (100 - damageAbsorption) / 100f;
                totalAbsorbed.addAndGet(currentDamageValue - afterValue);
                return afterValue;
            }
        };
        wp.getCooldownManager().addCooldown(protectiveLayerCooldown);
        addSecondaryAbility(() -> {
                    wp.getCooldownManager().removeCooldownNoForce(protectiveLayerCooldown);
                    wp.addSpeedModifier(wp, name, speedIncrease, speedIncreaseDuration, "BASE");
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(protectiveLayerCooldown)
        );
        return true;
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

    public float getDamageAbsorption() {
        return damageAbsorption;
    }

    public void setDamageAbsorption(float damageAbsorption) {
        this.damageAbsorption = damageAbsorption;
    }

    public int getShieldTickDuration() {
        return shieldTickDuration;
    }

    public void setShieldTickDuration(int shieldTickDuration) {
        this.shieldTickDuration = shieldTickDuration;
    }
}
