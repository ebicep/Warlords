package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SuperBrew extends AbstractAbility implements OrangeAbilityIcon, HitBox, Duration, AbilityStats<SuperBrew, SuperBrew.SuperBrewStats> {

    private final SuperBrewStats stats = new SuperBrewStats();
    private FloatModifiable radius = new FloatModifiable(7.0f);
    private int tickDuration;
    private float energyPerSecondIncrease;
    private int maxEnergyIncrease;
    private int meleeDamageIncreasePercent;
    private int ultCooldownReductionPercent;

    public SuperBrew() {
        super(AbstractAbilityBuilder.create("superBrew").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.energyPerSecondIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyPerSecondIncrease"), float.class);
        this.maxEnergyIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxEnergyIncrease"), int.class);
        this.meleeDamageIncreasePercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("meleeDamageIncreasePercent"), int.class);
        this.ultCooldownReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("ultCooldownReductionPercent"), int.class);
    }


    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 2, 0.75f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 2);

        float radius = this.radius.getCalculatedValue();
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp, true)
                .lookingAtFirst(wp)
                .limit(1)
                .toList();
        WarlordsEntity target = targets.isEmpty() ? wp : targets.get(0);
        EffectUtils.playParticleLinkAnimation(wp.getLocation(), target.getLocation(), Particle.DRAGON_BREATH);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        target.setRegenTickTimer(tickDuration);
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        modifiers.add(target.getEnergyPerSec().addAdditiveModifier(name, energyPerSecondIncrease));
        modifiers.add(target.getEnergy().addAdditiveModifier(name, maxEnergyIncrease));
        for (AbstractAbility ability : target.getAbilitiesImplementing(OrangeAbilityIcon.class)) {
            modifiers.add(ability.getCooldownReductionPerTick().addMultiplicativeModifierAdd(name, ultCooldownReductionPercent / 100f));
        }
        target.getCooldownManager().removeCooldown(SuperBrew.class, false);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SUPER",
                SuperBrew.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                cooldownManager -> {
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(
                                Particle.END_ROD,
                                target.getLocation(),
                                3,
                                random.nextDouble(.5),
                                0,
                                random.nextDouble(.5),
                                .1
                        );
                    }
                    if (ticksElapsed % 20 == 0) {
                        int regenHealth = ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "regenHealth", int.class);
                        target.setCurrentHealth(Math.max(target.getCurrentHealth(), Math.min(target.getCurrentHealth() + regenHealth, target.getMaxHealth())));
                    }
                })
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getCause().isEmpty()) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(meleeDamageIncreasePercent);
                }
                return currentDamageValue;
            }
        });
        if (wp != target) {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You gave a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" to " + target.getName() + "!", NamedTextColor.GRAY)));
            target.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" You have been given a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY))
            );
        } else {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You gave a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" to yourself!", NamedTextColor.GRAY)));
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Pass a powerful brew to yourself or an ally that grants them ")
                .energy(energyPerSecondIncrease)
                .text(" per second, ")
                .energy(maxEnergyIncrease, "")
                .text(" max energy, ")
                .percent(meleeDamageIncreasePercent, NamedTextColor.RED)
                .text(" melee damage, ")
                .percent(ultCooldownReductionPercent, NamedTextColor.GOLD)
                .text(" ultimate cooldown reduction, and permanent passive health regen.")
                .emptyLine()
                .text("If no ally is targeted, receive the brew yourself.")
                .build();
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public SuperBrewStats getAbilityStats() {
        return stats;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public static class SuperBrewStats extends AbstractAbilityStats<SuperBrew, SuperBrewStats> {

        @Field("allies_given")
        private int alliesGiven = 0;
        @Field("self_given")
        private int selfGiven = 0;

        @Override
        public Class<SuperBrewStats> getClazz() {
            return SuperBrewStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Allies Given", alliesGiven));
            statsDisplay.add(new AbilityStatDisplay("Self Given", selfGiven));
            return statsDisplay;
        }

        @Override
        public SuperBrewStats merge(SuperBrewStats other, int multiplier) {
            SuperBrewStats stats = super.merge(other, multiplier);
            stats.alliesGiven = this.alliesGiven + other.alliesGiven * multiplier;
            stats.selfGiven = this.selfGiven + other.selfGiven * multiplier;
            return stats;
        }

        @Override
        public SuperBrewStats create() {
            return new SuperBrewStats();
        }

    }

}
