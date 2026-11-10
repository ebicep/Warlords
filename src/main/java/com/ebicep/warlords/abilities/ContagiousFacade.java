package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.ContagiousFacadeBranch;
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
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContagiousFacade extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<ContagiousFacade, ContagiousFacade.ContagiousFacadeStats> {

    private final ContagiousFacadeStats stats = new ContagiousFacadeStats();
    private FloatModifiable damageAbsorption = new FloatModifiable(30);
    private int tickDuration = 100;
    private int shieldTickDuration = 100;
    private float poisonRadius = 8;
    private int speedIncrease = 40;
    private int speedIncreaseDuration = 100;
    private int stacksGranted = 2;
    private int infectedPlayers = 2;
    private boolean reactivateAbility = true;

    public ContagiousFacade() {
        super(AbstractAbilityBuilder.create("contagiousFacade").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.damageAbsorption = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageAbsorption"), float.class));
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.shieldTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldTickDuration"), int.class);
        this.poisonRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("poisonRadius"), float.class);
        this.speedIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedIncrease"), int.class);
        this.speedIncreaseDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedIncreaseDuration"), int.class);
        this.stacksGranted = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("stacksGranted"), int.class);
        this.infectedPlayers = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("infectedPlayers"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "arcanist.contagiousfacade.activation", 2, 1.4f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 2, 0.7f);
        EffectUtils.playHelixAnimation(wp.getLocation().add(0, 0.25, 0), 3, Particle.CHERRY_LEAVES, 3, 20);
        AtomicDouble totalAbsorbed = new AtomicDouble(0);
        RegularCooldown<ContagiousFacade> protectiveLayerCooldown = new RegularCooldown<>(name,
                "FACADE",
                ContagiousFacade.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 2, 2);
                    float shieldHealth = (float) totalAbsorbed.get();
                    shieldHealth *= pveMasterUpgrade2 ? 2.5f : 1;
                    stats.totalShieldGained += shieldHealth;
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
                                    EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                                    EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                                }
                            })
                    ) {

                        @Override
                        public PlayerNameData addPrefixFromOther() {
                            return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(wp), NamedTextColor.YELLOW);
                        }
                    });
                    wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                                  .append(Component.text(name, NamedTextColor.YELLOW))
                                                                  .append(Component.text(" is now shielding you!", NamedTextColor.GRAY)));
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(Particle.CRIMSON_SPORE, wp.getLocation(), 1, 0.05, 0.1, 0.05, 0.25);
                        EffectUtils.displayParticle(Particle.CHERRY_LEAVES, wp.getLocation(), 2, 0.15, 0.3, 0.15, 0);
                    }
                })
        );
        protectiveLayerCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name,
                    convertToDivisionDecimal(damageAbsorption.getCalculatedValue()),
                    contribution -> totalAbsorbed.addAndGet(Math.abs(contribution))
            );
                }
        );
        if (pveMasterUpgrade2) {
            wp.addKnockbackModifier(wp, name, -100, protectiveLayerCooldown);
        }
        wp.getCooldownManager().addCooldown(protectiveLayerCooldown);
        if (reactivateAbility) {
            addSecondaryAbility(
                    5,
                    () -> {
                        wp.getCooldownManager().removeCooldownNoForce(protectiveLayerCooldown);
                        reactivate(wp);
                    },
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(protectiveLayerCooldown)
            );
        } else {
            reactivate(wp);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Cover yourself in a protective layer that absorbs ")
                .percent(damageAbsorption, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" of all incoming damage for ")
                .durationTicks(tickDuration)
                .text(".")
                .emptyLine()
                .text("Reactivate the ability to increase your speed by")
                .percent(speedIncrease, NamedTextColor.WHITE)
                .text(" for ")
                .durationTicks(speedIncreaseDuration)
                .text(" and inflict ")
                .text(stacksGranted, NamedTextColor.BLUE)
                .text(" stacks of ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(" on ")
                .text(infectedPlayers, NamedTextColor.BLUE)
                .text(" enemies within ")
                .blocks(poisonRadius)
                .text(".")
                .emptyLine()
                .text("Not reactivating the ability will grant yourself a shield equal to all the damage you have absorbed during " + name + ". Lasts ")
                .durationTicks(shieldTickDuration)
                .text(".")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ContagiousFacadeBranch(abilityTree, this);
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        damageAbsorption.tick();
        super.runEveryTick(warlordsEntity);
    }

    private void reactivate(@Nonnull WarlordsEntity wp) {
        wp.addSpeedModifier(wp, name, speedIncrease, speedIncreaseDuration);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 2, 2);
        new CircleEffect(wp.getGame(),
                wp.getTeam(),
                wp.getLocation(),
                poisonRadius,
                new CircumferenceEffect(Particle.DUST, Particle.DUST).particlesPerCircumference(1)
        ).playEffects();
        for (WarlordsEntity hexTarget : PlayerFilter.entitiesAround(wp, poisonRadius, poisonRadius, poisonRadius)
                                                    .aliveEnemiesOf(wp)
                                                    .closestFirst(wp)
                                                    .limit(pveMasterUpgrade ? Integer.MAX_VALUE : infectedPlayers)) {
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), hexTarget.getLocation(), 180, 0, 0, 2);
            for (int i = 0; i < stacksGranted; i++) {
                PoisonousHex.givePoisonousHex(wp, hexTarget);
                EffectUtils.displayParticle(Particle.CRIMSON_SPORE, wp.getLocation(), 20, 0.05, 0.1, 0.05, 0.25);
            }
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                          .append(Component.text(name, NamedTextColor.YELLOW))
                                                          .append(Component.text(" has infected " + hexTarget.getName() + "!", NamedTextColor.GRAY)));
            hexTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                                  .append(Component.text(name, NamedTextColor.YELLOW))
                                                                  .append(Component.text(" has infected you!", NamedTextColor.GRAY)));
            stats.totalHexesInflicted++;
        }
        if (pveMasterUpgrade) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "FAC",
                    ContagiousFacade.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    20 * 8
            ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addModifier(FloatModifiable.ModifierType.ADDITIVE, name, 0.5f)));
        }
        stats.timesReactivated++;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public ContagiousFacadeStats getAbilityStats() {
        return stats;
    }

    public void setReactivateAbility(boolean reactivateAbility) {
        this.reactivateAbility = reactivateAbility;
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

    public int getStacksGranted() {
        return stacksGranted;
    }

    public void setStacksGranted(int stacksGranted) {
        this.stacksGranted = stacksGranted;
    }

    public int getInfectedPlayers() {
        return infectedPlayers;
    }

    public void setInfectedPlayers(int infectedPlayers) {
        this.infectedPlayers = infectedPlayers;
    }

    public static class ContagiousFacadeStats extends AbstractAbilityStats<ContagiousFacade, ContagiousFacadeStats> {

        @Field("times_reactivated")
        private int timesReactivated = 0;

        @Field("total_hexes_inflicted")
        private int totalHexesInflicted = 0;

        @Field("total_shield_gained")
        private float totalShieldGained = 0;

        @Override
        public Class<ContagiousFacadeStats> getClazz() {
            return ContagiousFacadeStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Reactivated", timesReactivated));
            statsDisplay.add(new AbilityStatDisplay("Total Hexes Inflicted", totalHexesInflicted));
            statsDisplay.add(new AbilityStatDisplay("Total Shield Gained", Math.round(totalShieldGained)));
            return statsDisplay;
        }

        @Override
        public ContagiousFacadeStats merge(ContagiousFacadeStats other, int multiplier) {
            ContagiousFacadeStats stats = super.merge(other, multiplier);
            stats.timesReactivated = this.timesReactivated + other.timesReactivated * multiplier;
            stats.totalHexesInflicted = this.totalHexesInflicted + other.totalHexesInflicted * multiplier;
            stats.totalShieldGained = this.totalShieldGained + other.totalShieldGained * multiplier;
            return stats;
        }

        @Override
        public ContagiousFacadeStats create() {
            return new ContagiousFacadeStats();
        }

    }

}
