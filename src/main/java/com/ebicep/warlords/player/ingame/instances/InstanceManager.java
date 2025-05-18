package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.abilities.Intervene;
import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.abilities.Repentance;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.general.settings.ChatSettings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import com.ebicep.warlords.player.ingame.instances.type.HealingInstance;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class InstanceManager {

    public static Optional<WarlordsDamageHealingFinalEvent> addDamageHealingInstance(WarlordsEntity warlordsEntity, WarlordsDamageHealingEvent event) {
        if (warlordsEntity.isDead()) {
            return Optional.empty();
        }
        InstanceDebugHoverable debugHoverable = new InstanceDebugHoverable();
        debugHoverable.appendTitle("Pre Event", NamedTextColor.AQUA);
        debugHoverable.appendEvent(event);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        Optional<WarlordsDamageHealingFinalEvent> eventOptional;
        switch (event.getInstanceType()) {
            case HEALING -> eventOptional = addHealingInstance(warlordsEntity, debugHoverable, event);
            case DAMAGE -> eventOptional = addDamageInstance(warlordsEntity, debugHoverable, event);
            default -> eventOptional = Optional.empty();
        }
        eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager().callEvent(warlordsDamageHealingFinalEvent));
        return eventOptional;
    }

    private static Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity warlordsEntity,
            InstanceDebugHoverable debugMessage,
            WarlordsDamageHealingEvent event
    ) {
//        for (AbstractCooldown<?> abstractCooldown : warlordsEntity.getCooldownManager().getCooldownsDistinct()) {
//            abstractCooldown.damageDoBeforeVariableSetFromSelf(event);
//        }
        for (AbstractCooldown<?> abstractCooldown : event.getSource().getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.damageDoBeforeVariableSetFromAttacker(event);
            List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
            if (extraDamageInstances != null) {
                extraDamageInstances.forEach(e -> e.damageDoBeforeVariableSetFromAttacker(event));
            }
        }

        WarlordsEntity source = event.getSource();
        AbstractAbility ability = event.getAbility();
        String cause = event.getCause();
        float min = event.getMin();
        float max = event.getMax();
        float critChance = event.getCritChance();
        float critMultiplier = event.getCritMultiplier();
        boolean isMeleeHit = cause.isEmpty();
        boolean isFallDamage = cause.equals("Fall");
        EnumSet<InstanceFlags> flags = event.getFlags();
        List<CustomInstanceFlags> customFlags = event.getCustomFlags();
        boolean trueDamage = flags.contains(InstanceFlags.TRUE_DAMAGE);
        boolean pierceDamage = flags.contains(InstanceFlags.PIERCE);
        boolean ignoreDamageReduction = pierceDamage || flags.contains(InstanceFlags.IGNORE_DAMAGE_REDUCTION_ONLY);
        boolean noDamageBoost = flags.contains(InstanceFlags.IGNORE_DAMAGE_BOOST);

        AtomicReference<WarlordsDamageHealingFinalEvent> finalEvent = new AtomicReference<>(null);
        // Spawn Protection / Undying Army / Game State
        if ((warlordsEntity.isDead() && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) || !warlordsEntity.isActive()) {
            return Optional.empty();
        }

        debugMessage.appendTitle("Post Event", NamedTextColor.AQUA);
        debugMessage.appendEvent(event);

        float initialHealth = warlordsEntity.getCurrentHealth();

        List<AbstractCooldown<?>> selfCooldownsDistinct = warlordsEntity.getCooldownManager().getCooldownsDistinct();
        List<AbstractCooldown<?>> attackersCooldownsDistinct = source.getCooldownManager().getCooldownsDistinct();

        debugMessage.appendTitle("Before Reduction", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN)));
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
//            abstractCooldown.doBeforeReductionFromSelf(event);
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown));
        }
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Source Cooldowns", NamedTextColor.DARK_GREEN)));
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.doBeforeReductionFromAttacker(event);
            List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
            if (extraDamageInstances != null) {
                extraDamageInstances.forEach(e -> e.doBeforeReductionFromAttacker(event));
            }
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown));
        }

        debugMessage.appendTitle("Crit Modifiers", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Source Cooldowns", NamedTextColor.DARK_GREEN)));
        if (critChance > 0 && !flags.contains(InstanceFlags.IGNORE_CRIT_MODIFIERS)) {
            float previousCC = critChance;
            float previousCM = critMultiplier;
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                // TODO CLEAN THIS GARBGE
                List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                critChance = abstractCooldown.addCritChanceFromAttacker(event, critChance);
                critMultiplier = abstractCooldown.addCritMultiplierFromAttacker(event, critMultiplier);
                if (previousCC != critChance) {
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.GREEN))
                            .value(previousCC, critChance, abstractCooldown)
                    );
                }
                if (previousCM != critMultiplier) {
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.GREEN))
                            .value(previousCM, critMultiplier, abstractCooldown)
                    );
                }
                previousCC = critChance;
                previousCM = critMultiplier;
                if (extraDamageInstances != null) {
                    for (DamageInstance damageInstance : extraDamageInstances) {
                        critChance = damageInstance.addCritChanceFromAttacker(event, critChance);
                        critMultiplier = damageInstance.addCritMultiplierFromAttacker(event, critMultiplier);
                        if (previousCC != critChance) {
                            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                    .create(2)
                                    .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.GREEN))
                                    .value(previousCC, critChance, abstractCooldown)
                            );
                        }
                        if (previousCM != critMultiplier) {
                            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                    .create(2)
                                    .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.GREEN))
                                    .value(previousCM, critMultiplier, abstractCooldown)
                            );
                        }
                        previousCC = critChance;
                        previousCM = critMultiplier;
                    }
                }
            }
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                critChance = abstractCooldown.setCritChanceFromAttacker(event, critChance);
                critMultiplier = abstractCooldown.setCritMultiplierFromAttacker(event, critMultiplier);
                if (previousCC != critChance) {
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.GREEN))
                            .value(previousCC, critChance, abstractCooldown)
                    );
                }
                if (previousCM != critMultiplier) {
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.GREEN))
                            .value(previousCM, critMultiplier, abstractCooldown)
                    );
                }
                previousCC = critChance;
                previousCM = critMultiplier;
                if (extraDamageInstances != null) {
                    for (DamageInstance e : extraDamageInstances) {
                        critChance = e.setCritChanceFromAttacker(event, critChance);
                        critMultiplier = e.setCritMultiplierFromAttacker(event, critMultiplier);
                        if (previousCC != critChance) {
                            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                    .create(2)
                                    .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.GREEN))
                                    .value(previousCC, critChance, abstractCooldown)
                            );
                        }
                        if (previousCM != critMultiplier) {
                            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                    .create(2)
                                    .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.GREEN))
                                    .value(previousCM, critMultiplier, abstractCooldown)
                            );
                        }
                        previousCC = critChance;
                        previousCM = critMultiplier;
                    }
                }
            }
        }
        //crit
        float damageValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;
        if (critChance > 0 && crit <= critChance && source.isCanCrit()) {
            isCrit = true;
            damageValue *= critMultiplier / 100f;
        }
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.onPostCritCalculationFromAttacker(event, damageValue, isCrit, critChance, critMultiplier);
            List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
            if (extraDamageInstances != null) {
                for (DamageInstance damageInstance : extraDamageInstances) {
                    damageInstance.onPostCritCalculationFromAttacker(event, damageValue, isCrit, critChance, critMultiplier);
                }
            }
        }
        debugMessage.appendTitle("Calculated Damage", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageValue), NamedTextColor.GOLD))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create("" + isCrit, NamedTextColor.GOLD))
        );

        final float damageHealValueBeforeAllReduction = damageValue;
        if (!flags.contains(InstanceFlags.IGNORE_SELF_RES) && !trueDamage) {
            warlordsEntity.addAbsorbed(Math.max(0, damageValue - (damageValue *= 1 - warlordsEntity.getSpec().getDamageResistance() / 100f)));
            debugMessage.appendTitle(ComponentBuilder
                    .create("Spec Damage Reduction: ", NamedTextColor.AQUA)
                    .text(NumberFormat.formatOptionalHundredths(warlordsEntity.getSpec().getDamageResistance()), NamedTextColor.BLUE)
                    .build()
            );
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                    .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageValue), NamedTextColor.GOLD))
            );
        }

        if (source == warlordsEntity && (isFallDamage || isMeleeHit)) {
            if (isMeleeHit) {
                // True damage
                sendTookDamageMessage(warlordsEntity, debugMessage, min, "melee damage");
                warlordsEntity.resetRegenTimer();
                if (warlordsEntity.getCurrentHealth() - min <= 0 && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) {
                    warlordsEntity.getEntity().showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text("You took ", NamedTextColor.GRAY)
                                     .append(Component.text(Math.round(min), NamedTextColor.RED))
                                     .append(Component.text(" melee damage and died.")),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    warlordsEntity.die(source);
                } else {
                    warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() - min);
                    warlordsEntity.playHurtAnimation(source);
                }
            } else {
                // Fall Damage
                sendTookDamageMessage(warlordsEntity, debugMessage, damageValue, "fall damage");
                warlordsEntity.resetRegenTimer();
                if (warlordsEntity.getCurrentHealth() - damageValue <= 0 && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) {
                    warlordsEntity.getEntity().showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text("You took ", NamedTextColor.GRAY)
                                     .append(Component.text(Math.round(min), NamedTextColor.RED))
                                     .append(Component.text(" fall damage and died.")),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    warlordsEntity.die(source);
                } else {
                    warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() - damageValue);
                    warlordsEntity.playHurtAnimation(source);
                }

                // TODO refactor
                for (OrderOfEviscerate.OrderOfEviscerateData orderOfEviscerate : new CooldownFilter<>(source, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(OrderOfEviscerate.OrderOfEviscerateData.class)
                        .toList()
                ) {
                    orderOfEviscerate.addAndCheckDamageThreshold(damageValue, source);
                }
            }

            warlordsEntity.cancelHealingPowerUp();
            return Optional.empty();
        }
        float previousDamageValue = damageValue;
        // Flag carrier multiplier.
        double flagMultiplier = warlordsEntity.getFlagDamageMultiplier();
        if (flagMultiplier != 1 && !trueDamage) {
            damageValue *= (float) flagMultiplier;
            debugMessage.appendTitle(ComponentBuilder
                    .create("Flag Damage Multiplier: ", NamedTextColor.AQUA)
                    .text(NumberFormat.formatOptionalHundredths(flagMultiplier), NamedTextColor.BLUE)
                    .build()
            );
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                    .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageValue), NamedTextColor.GOLD))
            );
            previousDamageValue = damageValue;
        }
        // Reduction before Intervene.
        if (!trueDamage) {
            debugMessage.appendTitle("Before Intervene", NamedTextColor.AQUA);
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
            );
            for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                float newDamageValue = abstractCooldown.modifyDamageBeforeInterveneFromSelf(event, damageValue);
                if (newDamageValue < damageValue && ignoreDamageReduction) { // pierce ignores victim dmg reduction
                    continue;
                }
                damageValue = newDamageValue;
                if (previousDamageValue != damageValue) {
                    if (previousDamageValue > damageValue) {
                        abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                    }
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                            .value(previousDamageValue, damageValue, abstractCooldown)
                    );
                }
                previousDamageValue = damageValue;
                List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                if (extraDamageInstances != null) {
                    for (DamageInstance damageInstance : extraDamageInstances) {
                        newDamageValue = damageInstance.modifyDamageBeforeInterveneFromSelf(event, newDamageValue);
                    }
                }
                if (newDamageValue < damageValue && ignoreDamageReduction) { // pierce ignores victim dmg reduction
                    continue;
                }
                damageValue = newDamageValue;
                if (previousDamageValue != damageValue) {
                    if (previousDamageValue > damageValue) {
                        abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                    }
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                            .value(previousDamageValue, damageValue, abstractCooldown)
                    );
                }
                previousDamageValue = damageValue;
            }

            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Source Cooldowns", NamedTextColor.DARK_GREEN))
            );
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                float newDamageValue = abstractCooldown.modifyDamageBeforeInterveneFromAttacker(event, damageValue);
                if (newDamageValue > damageValue && noDamageBoost) { // no damage boost ignores attacker dmg increase
                    continue;
                }
                damageValue = newDamageValue;
                if (previousDamageValue != damageValue) {
                    if (previousDamageValue > damageValue) {
                        abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                    }
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                            .value(previousDamageValue, damageValue, abstractCooldown)
                    );
                }
                previousDamageValue = damageValue;
                List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                if (extraDamageInstances != null) {
                    for (DamageInstance damageInstance : extraDamageInstances) {
                        newDamageValue = damageInstance.modifyDamageBeforeInterveneFromAttacker(event, newDamageValue);
                    }
                }
                if (newDamageValue > damageValue && noDamageBoost) { // no damage boost ignores attacker dmg increase
                    continue;
                }
                damageValue = newDamageValue;
                if (previousDamageValue != damageValue) {
                    if (previousDamageValue > damageValue) {
                        abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                    }
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(2)
                            .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                            .value(previousDamageValue, damageValue, abstractCooldown)
                    );
                }
            }
            //debugMessage.append(Component.newline()).append(Component.text("In Hammer", NamedTextColor.RED));
        }

        final float damageHealValueBeforeInterveneReduction = damageValue;
        // Intervene
        Optional<LinkedCooldown> optionalInterveneCooldown = new CooldownFilter<>(warlordsEntity, LinkedCooldown.class)
                .filterCooldownClass(Intervene.InterveneData.class)
                .filter(regularCooldown -> !Objects.equals(regularCooldown.getFrom(), warlordsEntity))
                .findFirst();
        boolean intervened = !trueDamage && !pierceDamage &&
                optionalInterveneCooldown.isPresent() && optionalInterveneCooldown.get().getTicksLeft() > 0 &&
                warlordsEntity.isEnemy(source);
        if (intervened && optionalInterveneCooldown.get().getFrom() == source) {
            ChatUtils.MessageType.GAME.sendErrorMessage("Intervene Overflow? " + warlordsEntity.getName() + " intervened from " + source.getName() + " - " + event);
            intervened = false;
        }
        if (intervened) {
            debugMessage.appendTitle("Intervene", NamedTextColor.AQUA);

            Intervene.InterveneData data = (Intervene.InterveneData) optionalInterveneCooldown.get().getCooldownObject();
            float maxDamagePrevented = data.getMaxDamagePrevented();
            float preDamagePrevented = data.getDamagePrevented();
            WarlordsEntity intervenedBy = optionalInterveneCooldown.get().getFrom();
            intervenedBy.resetRegenTimer();
            // Break Intervene if above damage threshold
            if (preDamagePrevented + damageValue > maxDamagePrevented) {
                optionalInterveneCooldown.get().setTicksLeft(0);
                //extra overVeneDamage to target
                float overVeneDamage = preDamagePrevented + damageValue - maxDamagePrevented;
                //remaining vene prevent damage
                float leftOverPrevented = maxDamagePrevented - preDamagePrevented;
                data.addDamagePrevented(leftOverPrevented);
                intervenedBy.addAbsorbed(leftOverPrevented * (1 - data.getIntervene().getDamageReduction() / 100f));
                damageValue = leftOverPrevented * (data.getIntervene().getDamageReduction() / 100f);
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                        .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageValue), NamedTextColor.GOLD))
                );
                intervenedBy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Intervene")
                        .source(source)
                        .value(damageValue)
                        .showAsCrit(isCrit)
                        .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS)
                );
                warlordsEntity.addInstance(InstanceBuilder
                        .damage()
                        .cause(cause)
                        .source(source)
                        .value(overVeneDamage)
                        .showAsCrit(isCrit)
                        .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS)
                ).ifPresent(finalEvent::set);
            } else {
                damageValue *= data.getIntervene().getDamageReduction() / 100f;
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                        .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageValue), NamedTextColor.GOLD))
                );
                data.addDamagePrevented(damageHealValueBeforeInterveneReduction);
                intervenedBy.addAbsorbed(damageHealValueBeforeInterveneReduction - damageValue);
                intervenedBy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Intervene")
                        .source(source)
                        .value(damageValue)
                        .showAsCrit(isCrit)
                        .flags(InstanceFlags.IGNORE_CRIT_MODIFIERS)
                );
                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        flags,
                        warlordsEntity,
                        source,
                        ability,
                        cause,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        0,
                        0,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true,
                        WarlordsDamageHealingFinalEvent.FinalEventFlag.INTERVENED
                ));
            }

            Location loc = warlordsEntity.getLocation();
            //EFFECTS + SOUNDS
            Utils.playGlobalSound(loc, "warrior.intervene.block", 2, 1);
            source.playHitSound();
            warlordsEntity.playHurtAnimation(source);
            intervenedBy.playHurtAnimation(source);
            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), intervenedBy.getLocation(), 255, 0, 0, 2);
            // Remove horses.
            if (!flags.contains(InstanceFlags.NO_DISMOUNT)) {
                warlordsEntity.removeHorse();
            }

            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Intervene From Attacker", NamedTextColor.GREEN))
            );
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                abstractCooldown.onInterveneFromAttacker(event, damageValue);
                List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                if (extraDamageInstances != null) {
                    for (DamageInstance damageInstance : extraDamageInstances) {
                        damageInstance.onInterveneFromAttacker(event, damageValue);
                    }
                }
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(abstractCooldown)
                );
            }
        } else {
            // Damage reduction after Intervene
            if (!trueDamage) {
                // Damage Reduction
                // Example: .8 = 20% reduction.
                debugMessage.appendTitle("After Intervene", NamedTextColor.AQUA);
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
                );
                for (AbstractCooldown<?> abstractCooldown : CooldownManager.getPrioritizedCooldowns(selfCooldownsDistinct,
                        "modifyDamageAfterInterveneFromSelf",
                        WarlordsDamageHealingEvent.class,
                        float.class
                )) {
                    float newDamageValue = abstractCooldown.modifyDamageAfterInterveneFromSelf(event, damageValue);
                    if (newDamageValue < damageValue && ignoreDamageReduction) { // pierce ignores victim dmg reduction
                        continue;
                    }
                    damageValue = newDamageValue;
                    if (previousDamageValue != damageValue) {
                        if (previousDamageValue > damageValue) {
                            abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                        }
                        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                .create(2)
                                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                                .value(previousDamageValue, damageValue, abstractCooldown)
                        );
                    }
                    previousDamageValue = damageValue;
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            newDamageValue = damageInstance.modifyDamageAfterInterveneFromSelf(event, newDamageValue);
                        }
                    }
                    if (newDamageValue < damageValue && ignoreDamageReduction) { // pierce ignores victim dmg reduction
                        continue;
                    }
                    damageValue = newDamageValue;
                    if (previousDamageValue != damageValue) {
                        if (previousDamageValue > damageValue) {
                            abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                        }
                        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                .create(2)
                                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                                .value(previousDamageValue, damageValue, abstractCooldown)
                        );
                    }
                    previousDamageValue = damageValue;
                }

                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Source Cooldowns", NamedTextColor.DARK_GREEN))
                );
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                    float newDamageValue = abstractCooldown.modifyDamageAfterInterveneFromAttacker(event, damageValue);
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            newDamageValue = damageInstance.modifyDamageAfterInterveneFromAttacker(event, newDamageValue);
                        }
                    }
                    if (newDamageValue > damageValue && noDamageBoost) { // no damage boost ignores attacker dmg increase
                        continue;
                    }
                    damageValue = newDamageValue;
                    if (previousDamageValue != damageValue) {
                        if (previousDamageValue > damageValue) {
                            abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                        }
                        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                .create(2)
                                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                                .value(previousDamageValue, damageValue, abstractCooldown)
                        );
                    }
                    previousDamageValue = damageValue;
                }
            }

            final float damageHealValueBeforeShieldReduction = damageValue;
            // Arcane Shield
            Optional<RegularCooldown> shieldCooldown = new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                    .filterCooldownClass(Shield.class)
                    .filter(RegularCooldown::hasTicksLeft)
                    .findFirst();
            if (!trueDamage &&
                    !pierceDamage &&
                    shieldCooldown.isPresent() &&
                    warlordsEntity.isEnemy(source)
            ) {
                RegularCooldown cooldown = shieldCooldown.get();
                Shield shield = (Shield) cooldown.getCooldownObject();
                debugMessage.appendTitle("Shield (" + shield.getName() + ")", NamedTextColor.AQUA);
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Pre Health: ", NamedTextColor.GREEN))
                        .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(shield.getShieldHealth()), NamedTextColor.GOLD))
                );
                //adding dmg to shield
                shield.addShieldHealth(-damageValue);
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Post Health: ", NamedTextColor.GREEN))
                        .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(shield.getShieldHealth()), NamedTextColor.GOLD))
                );
                //check if broken
                TextComponent.Builder ownMessage = Component.text();
                TextComponent.Builder attackerMessage = Component.text();
                if (shield.getShieldHealth() <= 0) {
                    cooldown.setTicksLeft(0);
                }
                if (shield.isBroken()) {
                    float newDamage = -shield.getShieldHealth();
                    addDamageInstance(warlordsEntity, new InstanceDebugHoverable(), new WarlordsDamageHealingEvent(
                                    warlordsEntity,
                            source,
                            cause,
                                    newDamage,
                                    newDamage,
                                    isCrit ? 100 : 0,
                                    100,
                                    true,
                                    EnumSet.of(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS),
                                    customFlags
                            )
                    );

                    cooldown.getFrom().addAbsorbed(-(shield.getShieldHealth()));

                    return Optional.empty();
                } else {
                    double totalShieldHealth = new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                            .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                            .mapToDouble(Shield::getShieldHealth)
                            .sum();
                    warlordsEntity.giveAbsorption((float) (totalShieldHealth / warlordsEntity.getMaxHealth() * 40));

                    if (isMeleeHit) {
                        ownMessage.append(WarlordsEntity.RECEIVE_ARROW_RED
                                .append(Component.text(" You absorbed " + source.getName() + "'s melee hit.", NamedTextColor.GRAY
                                )));
                        attackerMessage.append(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your melee hit was absorbed by " + warlordsEntity.getName() + ".", NamedTextColor.GRAY
                                )));
                    } else {
                        ownMessage.append(WarlordsEntity.RECEIVE_ARROW_RED
                                .append(Component.text(" You absorbed " + source.getName() + "'s " + cause + " hit.", NamedTextColor.GRAY
                                )));
                        attackerMessage.append(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your " + cause + " was absorbed by " + warlordsEntity.getName() + ".", NamedTextColor.GRAY
                                )));
                    }

                    cooldown.getFrom().addAbsorbed(Math.abs(damageHealValueBeforeAllReduction));
                }

                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("On Shield", NamedTextColor.DARK_GREEN))
                );
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
                );

                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    abstractCooldown.onShieldFromSelf(event, damageValue, isCrit);
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            damageInstance.onShieldFromSelf(event, damageValue, isCrit);
                        }
                    }
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(3)
                            .prefix(abstractCooldown)
                    );
                }

                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Attackers Cooldowns", NamedTextColor.DARK_GREEN))
                );
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
//                    abstractCooldown.onShieldFromAttacker(event, damageValue, isCrit);
                    debugMessage.append(InstanceDebugHoverable.LevelBuilder
                            .create(3)
                            .prefix(abstractCooldown)
                    );
                }

                if (shield.getShieldHealth() >= 0) {
                    DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsEntity.getUuid(),
                            warlordsEntity instanceof WarlordsPlayer && warlordsEntity.getEntity() instanceof Player
                    );
                    switch (databasePlayer.getChatHealingMode()) {
                        case ALL -> {
                            if (warlordsEntity.isShowDebugMessage()) {
                                warlordsEntity.sendMessage(ownMessage.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                            } else {
                                warlordsEntity.sendMessage(ownMessage.build());
                            }
                        }
                        case CRITS_ONLY -> {
                            if (isCrit) {
                                if (warlordsEntity.isShowDebugMessage()) {
                                    warlordsEntity.sendMessage(ownMessage.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                                } else {
                                    warlordsEntity.sendMessage(ownMessage.build());
                                }
                            }
                        }
                    }
                    databasePlayer = DatabaseManager.getPlayer(source.getUuid(), source instanceof WarlordsPlayer && source.getEntity() instanceof Player);
                    switch (databasePlayer.getChatHealingMode()) {
                        case ALL -> {
                            if (source.isShowDebugMessage()) {
                                source.sendMessage(attackerMessage.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                            } else {
                                source.sendMessage(attackerMessage.build());
                            }
                        }
                        case CRITS_ONLY -> {
                            if (isCrit) {
                                if (source.isShowDebugMessage()) {
                                    source.sendMessage(attackerMessage.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                                } else {
                                    source.sendMessage(attackerMessage.build());
                                }
                            }
                        }
                    }
                }

                warlordsEntity.playHurtAnimation(source);

                if (!flags.contains(InstanceFlags.NO_HIT_SOUND)) {
                    warlordsEntity.playHitSound(source);
                }
                if (!flags.contains(InstanceFlags.NO_DISMOUNT)) {
                    warlordsEntity.removeHorse();
                }

                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        flags,
                        warlordsEntity,
                        source,
                        ability, cause,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        damageHealValueBeforeShieldReduction,
                        damageValue,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true,
                        WarlordsDamageHealingFinalEvent.FinalEventFlag.SHIELDED
                ));
                warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent.get());
                source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent.get());
            } else {
                debugMessage.appendTitle("Modify Damage After All", NamedTextColor.AQUA);
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(1)
                        .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
                );
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    damageValue = abstractCooldown.modifyDamageAfterAllFromSelf(event, damageValue, isCrit);
                    if (previousDamageValue != damageValue) {
                        if (previousDamageValue > damageValue) {
                            abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                        }
                        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                .create(2)
                                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                                .value(previousDamageValue, damageValue, abstractCooldown)
                        );
                    }
                    previousDamageValue = damageValue;
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            damageValue = damageInstance.modifyDamageAfterAllFromSelf(event, damageValue, isCrit);
                        }
                    }
                    if (previousDamageValue != damageValue) {
                        if (previousDamageValue > damageValue) {
                            abstractCooldown.getFrom().addAbsorbed(previousDamageValue - damageValue);
                        }
                        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                                .create(2)
                                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                                .value(previousDamageValue, damageValue, abstractCooldown)
                        );
                    }
                    previousDamageValue = damageValue;
                }

                boolean debt = warlordsEntity.getCooldownManager().hasCooldownFromName("Spirits' Respite");
                //if (isEnemy(attacker)) {
                warlordsEntity.getHitBy().put(source, 10);
                warlordsEntity.cancelHealingPowerUp();
                if (!flags.contains(InstanceFlags.NO_DISMOUNT)) {
                    warlordsEntity.removeHorse();
                }

                float finalDamageValue = damageValue;
                warlordsEntity.doOnStaticAbility(SoulShackle.class, soulShackle -> soulShackle.addToShacklePool(finalDamageValue));
                warlordsEntity.doOnStaticAbility(Repentance.class, repentance -> repentance.addToPool(finalDamageValue));

                if (!flags.contains(InstanceFlags.NO_MESSAGE)) {
                    sendDamageMessage(warlordsEntity, debugMessage, source, warlordsEntity, cause, damageValue, isCrit, isMeleeHit, flags);
                }
                //debugMessage.append("\n").append(ChatColor.AQUA).append("On Damage");
                //appendDebugMessage(debugMessage, 1, ChatColor.DARK_GREEN, "Target Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    abstractCooldown.onDamageFromSelf(event, damageValue, isCrit);
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            damageInstance.onDamageFromSelf(event, damageValue, isCrit);
                        }
                    }
                    //appendDebugMessage(debugMessage, 2, abstractCooldown);
                }

                //appendDebugMessage(debugMessage, 1, ChatColor.DARK_GREEN, "Attackers Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                    abstractCooldown.onDamageFromAttacker(event, damageValue, isCrit);
                    List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                    if (extraDamageInstances != null) {
                        for (DamageInstance damageInstance : extraDamageInstances) {
                            damageInstance.onDamageFromAttacker(event, damageValue, isCrit);
                        }
                    }
                    //appendDebugMessage(debugMessage, 2, abstractCooldown);
                }
                //}

                warlordsEntity.resetRegenTimer();
                warlordsEntity.updateHealth();

                float cappedDamage = Math.min(damageValue, warlordsEntity.getCurrentHealth() - (flags.contains(InstanceFlags.CANT_KILL) ? 1 : 0));
                source.addDamage(cappedDamage, FlagHolder.isPlayerHolderFlag(warlordsEntity));
                warlordsEntity.addDamageTaken(cappedDamage);
                warlordsEntity.playHurtAnimation(source);
                if (source.isNoEnergyConsumption()) {
                    source.getRecordDamage().add(cappedDamage);
                }

                // debt and healing
                if (!debt && warlordsEntity.isTakeDamage()) {
                    warlordsEntity.setCurrentHealth(Math.min(warlordsEntity.getCurrentHealth() - damageValue, warlordsEntity.getMaxHealth()));
                }

                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        flags,
                        warlordsEntity,
                        source,
                        ability, cause,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        damageHealValueBeforeShieldReduction,
                        damageValue,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true,
                        WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR
                ));
                warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent.get());
                source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent.get());
                // The player died.
                if (warlordsEntity.getCurrentHealth() <= 0 && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) {
                    if (source.getEntity() instanceof Player player) {
                        player.playSound(source.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 1);
                        player.playSound(source.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 0.5f);
                    }

                    source.addKill();

                    warlordsEntity.getGame().forEachOnlinePlayer((p, t) -> {
                        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p.getUniqueId(), true);
                        ChatSettings.ChatKills killsMode = databasePlayer.getChatKillsMode();
                        if (killsMode != ChatSettings.ChatKills.ALL && killsMode != ChatSettings.ChatKills.NO_ASSISTS) {
                            return;
                        }
                        if (p == warlordsEntity.getEntity()) {
                            warlordsEntity.sendMessage(Component.text("You were killed by ", NamedTextColor.GRAY)
                                                                .append(source.getColoredName()));
                        } else if (p == source.getEntity()) {
                            source.sendMessage(Component.text("You killed ", NamedTextColor.GRAY)
                                                          .append(warlordsEntity.getColoredName()));
                        } else {
                            p.sendMessage(warlordsEntity.getColoredName()
                                                        .append(Component.text(" was killed by ", NamedTextColor.GRAY))
                                                        .append(source.getColoredName()));
                        }
                    });

                    for (WarlordsEntity enemy : PlayerFilter.playingGame(warlordsEntity.getGame())
                                                            .enemiesOf(warlordsEntity)
                                                            .stream()
                                                            .toList()
                    ) {
                        for (AbstractCooldown<?> abstractCooldown : enemy.getCooldownManager().getCooldownsDistinct()) {
                            abstractCooldown.onDeathFromEnemies(event, damageValue, isCrit, enemy == source);
                            List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
                            if (extraDamageInstances != null) {
                                for (DamageInstance damageInstance : extraDamageInstances) {
                                    damageInstance.onDeathFromEnemies(event, damageValue, isCrit, enemy == source);
                                }
                            }
                        }
                    }
                    warlordsEntity.getEntity().showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text(source.getName() + " killed you.", NamedTextColor.GRAY),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    warlordsEntity.die(source);
                } else {
                    if (!flags.contains(InstanceFlags.NO_HIT_SOUND) && warlordsEntity != source && damageValue != 0) {
                        warlordsEntity.playHitSound(source);
                    }
                }
            }
        }

//        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
//            abstractCooldown.onEndFromSelf(event, damageValue, isCrit);
//        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.onEndFromAttacker(event, damageValue, isCrit);
            List<DamageInstance> extraDamageInstances = abstractCooldown.getExtraDamageInstances();
            if (extraDamageInstances != null) {
                for (DamageInstance damageInstance : extraDamageInstances) {
                    damageInstance.onEndFromAttacker(event, damageValue, isCrit);
                }
            }
        }

        return Optional.ofNullable(finalEvent.get());
    }

    public static void sendTookDamageMessage(WarlordsEntity warlordsEntity, InstanceDebugHoverable debugMessage, float damage, String from) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsEntity.getUuid(),
                warlordsEntity instanceof WarlordsPlayer && warlordsEntity.getEntity() instanceof Player
        );
        if (databasePlayer.getChatDamageMode() == ChatSettings.ChatDamage.ALL) {
            Component component = WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You took ", NamedTextColor.GRAY))
                    .append(Component.text(Math.round(damage), NamedTextColor.RED))
                    .append(Component.text(" " + from + ".", NamedTextColor.GRAY));
            if (warlordsEntity.isShowDebugMessage()) {
                warlordsEntity.sendMessage(component.hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
            } else {
                warlordsEntity.sendMessage(component);
            }
        }
    }

    private static Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(
            WarlordsEntity warlordsEntity,
            InstanceDebugHoverable debugMessage,
            WarlordsDamageHealingEvent event
    ) {
        WarlordsEntity source = event.getSource();

        List<AbstractCooldown<?>> selfCooldownsDistinct = warlordsEntity.getCooldownManager().getCooldownsDistinct();
        List<AbstractCooldown<?>> attackersCooldownsDistinct = source.getCooldownManager().getCooldownsDistinct();

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.healingDoBeforeVariableSetFromSelf(event);
            List<HealingInstance> extraHealingInstances = abstractCooldown.getExtraHealingInstances();
            if (extraHealingInstances != null) {
                for (HealingInstance healingInstance : extraHealingInstances) {
                    healingInstance.healingDoBeforeVariableSetFromSelf(event);
                }
            }
        }
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.healingDoBeforeVariableSetFromAttacker(event);
            List<HealingInstance> extraHealingInstances = abstractCooldown.getExtraHealingInstances();
            if (extraHealingInstances != null) {
                for (HealingInstance healingInstance : extraHealingInstances) {
                    healingInstance.healingDoBeforeVariableSetFromAttacker(event);
                }
            }
        }

        AbstractAbility ability = event.getAbility();
        String cause = event.getCause();
        float min = event.getMin();
        float max = event.getMax();
        float critChance = event.getCritChance();
        float critMultiplier = event.getCritMultiplier();
        EnumSet<InstanceFlags> flags = event.getFlags();
        boolean isLastStandFromShield = flags.contains(InstanceFlags.LAST_STAND_FROM_SHIELD);
        boolean pierce = flags.contains(InstanceFlags.PIERCE);

        WarlordsDamageHealingFinalEvent finalEvent;
        // Spawn Protection / Undying Army / Game State
        if ((warlordsEntity.isDead() && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) || !warlordsEntity.isActive()) {
            return Optional.empty();
        }

        debugMessage.appendTitle("Post Event", NamedTextColor.AQUA);
        debugMessage.appendEvent(event);

        float initialHealth = warlordsEntity.getCurrentHealth();
        // Critical Hits
        float healValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;

        if (crit <= critChance && source.isCanCrit()) {
            isCrit = true;
            healValue *= critMultiplier / 100f;
        }

        debugMessage.appendTitle("Calculated Heal", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(healValue), NamedTextColor.GOLD))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create("" + isCrit, NamedTextColor.GOLD))
        );

        final float healValueBeforeReduction = healValue;
        float previousHealValue = healValue;

        debugMessage.appendTitle("Before Heal", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
        );
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            float newHealValue = abstractCooldown.modifyHealingFromSelf(event, healValue);
            if (newHealValue < healValue && pierce) { // pierce ignores victim healing reduction
                continue;
            }
            healValue = newHealValue;
            if (previousHealValue != healValue) {
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                        .value(previousHealValue, healValue, abstractCooldown)
                );
            }
            previousHealValue = healValue;
            List<HealingInstance> extraHealingInstances = abstractCooldown.getExtraHealingInstances();
            if (extraHealingInstances != null) {
                for (HealingInstance healingInstance : extraHealingInstances) {
                    newHealValue = healingInstance.modifyHealingFromSelf(event, newHealValue);
                }
            }
            if (newHealValue < healValue && pierce) { // pierce ignores victim healing reduction
                continue;
            }
            healValue = newHealValue;
            if (previousHealValue != healValue) {
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                        .value(previousHealValue, healValue, abstractCooldown)
                );
            }
            previousHealValue = healValue;
        }

        debugMessage.appendTitle("Attackers Cooldowns", NamedTextColor.AQUA);
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            healValue = abstractCooldown.modifyHealingFromAttacker(event, healValue);
            if (previousHealValue != healValue) {
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                        .value(previousHealValue, healValue, abstractCooldown)
                );
            }
            previousHealValue = healValue;
            List<HealingInstance> extraHealingInstances = abstractCooldown.getExtraHealingInstances();
            if (extraHealingInstances != null) {
                for (HealingInstance healingInstance : extraHealingInstances) {
                    healValue = healingInstance.modifyHealingFromAttacker(event, healValue);
                }
            }
            if (previousHealValue != healValue) {
                debugMessage.append(InstanceDebugHoverable.LevelBuilder
                        .create(2)
                        .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                        .value(previousHealValue, healValue, abstractCooldown)
                );
            }
            previousHealValue = healValue;
        }
        if (warlordsEntity == source || warlordsEntity.isTeammate(source)) {
            float maxHealth = warlordsEntity.getHealth().getCalculatedValue();
            boolean overhealSelf = warlordsEntity == source && flags.contains(InstanceFlags.CAN_OVERHEAL_SELF);
            boolean overhealOthers = warlordsEntity != source && warlordsEntity.isTeammate(source) && flags.contains(InstanceFlags.CAN_OVERHEAL_OTHERS);
            if (overhealSelf || overhealOthers) {
                maxHealth *= 1.1f;
            }
            if (warlordsEntity.getCurrentHealth() + healValue > maxHealth) {
                healValue = maxHealth - warlordsEntity.getCurrentHealth();
            }

            if (healValue <= 0) {
                return Optional.empty();
            }

            boolean isOverheal = maxHealth > warlordsEntity.getMaxHealth() && healValue + warlordsEntity.getCurrentHealth() > warlordsEntity.getMaxBaseHealth();
            if (warlordsEntity == source) {
                sendHealingMessage(warlordsEntity, debugMessage, healValue, cause, isCrit, isLastStandFromShield, isOverheal);
            } else {
                sendHealingMessage(warlordsEntity, debugMessage, source, warlordsEntity, healValue, cause, isCrit, isLastStandFromShield, isOverheal);
            }

            for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                abstractCooldown.onHealFromSelf(event, healValue, isCrit);
            }
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                abstractCooldown.onHealFromAttacker(event, healValue, isCrit);
            }

            float cappedHealValue = Math.min(healValue, maxHealth - warlordsEntity.getCurrentHealth());
            source.addHealing(cappedHealValue, FlagHolder.isPlayerHolderFlag(warlordsEntity));
            warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() + healValue);

            if (!flags.contains(InstanceFlags.NO_HIT_SOUND)) {
                warlordsEntity.playHitSound(source);
            }
        }

        finalEvent = new WarlordsDamageHealingFinalEvent(
                event,
                flags,
                warlordsEntity,
                source,
                ability,
                cause,
                initialHealth,
                healValueBeforeReduction,
                healValueBeforeReduction,
                healValueBeforeReduction,
                healValue,
                critChance,
                critMultiplier,
                isCrit,
                false,
                WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR
        );
        warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent);
        source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);

        return Optional.of(finalEvent);
    }

    /**
     * @param player                which player should receive the message.
     * @param healValue             heal value of the message.
     * @param ability               which ability should the message display.
     * @param isCrit                whether if it's a critical hit message.
     * @param isLastStandFromShield whether the message is last stand healing.
     */
    private static void sendHealingMessage(
            @Nonnull WarlordsEntity player,
            InstanceDebugHoverable debugMessage,
            float healValue,
            String ability,
            boolean isCrit,
            boolean isLastStandFromShield,
            boolean isOverHeal
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder healBuilder = Component.text().color(NamedTextColor.GREEN);
        if (isCrit) {
            healBuilder.decorate(TextDecoration.BOLD);
        }
        healBuilder.append(Component.text(Math.round(healValue)));
        healBuilder.append(Component.text(isCrit ? "!" : ""));
        if (isLastStandFromShield) {
            healBuilder.append(Component.text(" Absorbed!"));
        }
        secondHalf.append(healBuilder);
        secondHalf.append(Component.text(" health."));

        // Own Message
        TextComponent.Builder hitBuilder = Component.text(" Your " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        hitBuilder.append(Component.text(" healed you for "));

        TextComponent.Builder ownFeed = Component.text()
                                                 .append(WarlordsEntity.GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);

        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player.getUuid(), player instanceof WarlordsPlayer && player.getEntity() instanceof Player);
        switch (databasePlayer.getChatHealingMode()) {
            case ALL -> {
                player.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    player.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                }
            }
        }
    }

    /**
     * @param warlordsEntity
     * @param sender                which player sends the message.
     * @param receiver              which player receives the message.
     * @param healValue             heal value of the message.
     * @param ability               which ability should the message display.
     * @param isCrit                whether if it's a critical hit message.
     * @param isLastStandFromShield whether the message is last stand healing.
     * @param isOverHeal            whether the message is overhealing.
     */
    private static void sendHealingMessage(
            WarlordsEntity warlordsEntity,
            InstanceDebugHoverable debugMessage,
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            float healValue, String ability,
            boolean isCrit,
            boolean isLastStandFromShield,
            boolean isOverHeal
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder healBuilder = Component.text().color(NamedTextColor.GREEN);
        if (isCrit) {
            healBuilder.decorate(TextDecoration.BOLD);
        }
        healBuilder.append(Component.text(Math.round(healValue)));
        healBuilder.append(Component.text(isCrit ? "!" : ""));
        if (isLastStandFromShield) {
            healBuilder.append(Component.text(" Absorbed!"));
        }
        secondHalf.append(healBuilder);
        secondHalf.append(Component.text(" health."));

        // Own Message
        TextComponent.Builder hitBuilder = Component.text(" Your " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed " + warlordsEntity.getName() + " for "));
        } else {
            hitBuilder.append(Component.text(" healed " + warlordsEntity.getName() + " for "));
        }

        TextComponent.Builder ownFeed = Component.text()
                                                 .append(WarlordsEntity.GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);

        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(sender.getUuid(), sender instanceof WarlordsPlayer && sender.getEntity() instanceof Player);
        switch (databasePlayer.getChatHealingMode()) {
            case ALL -> {
                sender.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    sender.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
                }
            }
        }

        // Ally Message
        hitBuilder = Component.text(" " + sender.getName() + "'s " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed you for "));
        } else {
            hitBuilder.append(Component.text(" healed you for "));
        }

        TextComponent.Builder allyFeed = Component.text()
                                                  .append(WarlordsEntity.RECEIVE_ARROW_GREEN)
                                                  .append(hitBuilder)
                                                  .append(secondHalf);

        databasePlayer = DatabaseManager.getPlayer(receiver.getUuid(), receiver instanceof WarlordsPlayer && receiver.getEntity() instanceof Player);
        switch (databasePlayer.getChatHealingMode()) {
            case ALL -> {
                receiver.sendMessage(allyFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    receiver.sendMessage(allyFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
                }
            }
        }

    }

    /**
     * @param warlordsEntity
     * @param sender         which player sends the message.
     * @param receiver       which player should receive the message.
     * @param ability        what is the damage ability.
     * @param damageValue    what is the damage value.
     * @param isCrit         whether if it's a critical hit message.
     * @param isMeleeHit     whether if it's a melee hit.
     * @param flags
     */
    private static void sendDamageMessage(
            WarlordsEntity warlordsEntity,
            InstanceDebugHoverable debugMessage,
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            String ability,
            float damageValue,
            boolean isCrit,
            boolean isMeleeHit,
            EnumSet<InstanceFlags> flags
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder damageBuilder = Component.text().color(NamedTextColor.RED);
        if (isCrit) {
            damageBuilder.decorate(TextDecoration.BOLD);
        }
        damageBuilder.append(Component.text(Math.round(damageValue)));
        if (isCrit) {
            damageBuilder.append(Component.text("! "));
        }
        secondHalf.append(damageBuilder);
        if (isCrit) {
            secondHalf.append(Component.text("critical"));
        }
        if (isMeleeHit) {
            secondHalf.append(Component.text(" melee"));
        }
        if (flags.contains(InstanceFlags.ROOTED)) {
            secondHalf.append(Component.text(" rooted"));
        }
        secondHalf.append(Component.text(" damage."));

        // Receiver feed
        TextComponent.Builder hitBuilder = Component.text(" " + sender.getName(), NamedTextColor.GRAY).toBuilder();
        if (!isMeleeHit) {
            hitBuilder.append(Component.text("'s " + ability));
        }
        hitBuilder.append(Component.text(" hit you for "));
        TextComponent.Builder enemyFeed = Component.text()
                                                   .append(WarlordsEntity.RECEIVE_ARROW_RED)
                                                   .append(hitBuilder)
                                                   .append(secondHalf);

        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsEntity.getUuid(), receiver instanceof WarlordsPlayer && receiver.getEntity() instanceof Player);
        switch (databasePlayer.getChatDamageMode()) {
            case ALL -> {
                receiver.sendMessage(enemyFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    receiver.sendMessage(enemyFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
                }
            }
        }

        // Sender feed
        hitBuilder = Component.text(" ", NamedTextColor.GRAY).toBuilder();
        if (isMeleeHit) {
            hitBuilder.append(Component.text("You hit "));
        } else {
            hitBuilder.append(Component.text("Your " + ability + " hit "));
        }
        hitBuilder.append(Component.text(warlordsEntity.getName() + " for "));
        TextComponent.Builder ownFeed = Component.text()
                                                 .append(WarlordsEntity.GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);
        databasePlayer = DatabaseManager.getPlayer(sender.getUuid(), sender instanceof WarlordsPlayer && sender.getEntity() instanceof Player);
        switch (databasePlayer.getChatDamageMode()) {
            case ALL -> {
                sender.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    sender.sendMessage(ownFeed.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())), true);
                }
            }
        }
    }

}
