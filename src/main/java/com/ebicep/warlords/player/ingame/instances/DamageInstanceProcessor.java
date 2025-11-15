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
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.general.settings.ChatSettings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiableFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class DamageInstanceProcessor {

    public static Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            InstanceDebugHoverable debugMessage,
            WarlordsDamageHealingEvent event
    ) {
        DamageInstanceProcessor processor = new DamageInstanceProcessor(debugMessage, event);
        return processor.process();
    }

    // Core event data
    private final InstanceDebugHoverable debugMessage;
    private final WarlordsDamageHealingEvent event;
    private final WarlordsEntity warlordsEntity;
    private final WarlordsEntity source;
    private final AbstractAbility ability;
    private final String cause;
    // Damage values
    private final float min;
    private final float max;
    private final FloatModifiable critChance;
    private final FloatModifiable critMultiplier;
    private final FloatModifiable damageValue;
    // Flags
    private final boolean isMeleeHit;
    private final boolean isFallDamage;
    private final EnumSet<InstanceFlags> flags;
    private final List<CustomInstanceFlags> customFlags;
    private final List<TextComponent> debugMessages;
    private final boolean trueDamage;
    private final boolean pierceDamage;
    private final boolean ignoreDamageReduction;
    private final boolean noSourceDamageBoost;
    private final boolean noTargetDamageBoost;
    // State tracking
    private final float initialHealth;
    private final List<AbstractCooldown<?>> selfCooldownsDistinct;
    private final List<AbstractCooldown<?>> attackersCooldownsDistinct;
    private final AtomicReference<WarlordsDamageHealingFinalEvent> finalEvent;
    // Calculated values
    private float damageHealValueBeforeAllReduction;
    private float damageHealValueBeforeInterveneReduction;
    private float damageHealValueBeforeShieldReduction;
    private float calculatedCritChance;
    private float calculatedCritMultiplier;
    private boolean isCrit;

    public DamageInstanceProcessor(InstanceDebugHoverable debugMessage, WarlordsDamageHealingEvent event) {
        this.debugMessage = debugMessage;
        this.event = event;
        this.warlordsEntity = event.getWarlordsEntity();
        this.source = event.getSource();
        this.ability = event.getAbility();
        this.cause = event.getCause();
        this.min = event.getMin();
        this.max = event.getMax();
        this.critChance = new FloatModifiable(event.getCritChance());
        this.critMultiplier = new FloatModifiable(event.getCritMultiplier());
        this.isMeleeHit = cause.isEmpty();
        this.isFallDamage = cause.equals("Fall");
        this.flags = event.getFlags();
        this.customFlags = event.getCustomFlags();
        this.debugMessages = event.getDebugMessages();
        this.trueDamage = flags.contains(InstanceFlags.TRUE_DAMAGE);
        this.pierceDamage = flags.contains(InstanceFlags.PIERCE);
        this.ignoreDamageReduction = pierceDamage || flags.contains(InstanceFlags.IGNORE_DAMAGE_REDUCTION_ONLY);
        this.noSourceDamageBoost = flags.contains(InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST);
        this.noTargetDamageBoost = flags.contains(InstanceFlags.IGNORE_TARGET_DAMAGE_BOOST);
        this.initialHealth = warlordsEntity.getCurrentHealth();
        this.selfCooldownsDistinct = warlordsEntity.getCooldownManager().getCooldownsDistinct();
        this.attackersCooldownsDistinct = source.getCooldownManager().getCooldownsDistinct();
        this.finalEvent = new AtomicReference<>(null);
        this.damageValue = new FloatModifiable((float) ((Math.random() * (max - min)) + min));
    }

    public Optional<WarlordsDamageHealingFinalEvent> process() {
        applyPreEventModifiers();

        if (!validateEntityState()) {
            return Optional.empty();
        }

        setupDebugMessages();
        applyBeforeReductionModifiers();
        calculateCriticals();

        if (handleSelfInflictedDamage()) {
            return Optional.empty();
        }

        applyFlagMultiplier();
        applyBeforeInterveneModifiers();

        if (handleIntervene()) {
            return Optional.ofNullable(finalEvent.get());
        }

        applyAfterInterveneModifiers();

        if (handleShield()) {
            return Optional.ofNullable(finalEvent.get());
        }

        applyFinalDamage();
        applyEndModifiers();

        return Optional.ofNullable(finalEvent.get());
    }

    private void applyPreEventModifiers() {
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_BEFORE_VARIABLE_SET_ATTACKER, m -> m.apply(event));
        }
    }

    private boolean validateEntityState() {
        return !warlordsEntity.isDead() && warlordsEntity.isActive();
    }

    private void setupDebugMessages() {
        debugMessage.appendTitle("Post Event", NamedTextColor.AQUA);
        debugMessage.appendEvent(event);
    }

    private void applyBeforeReductionModifiers() {
        debugMessage.appendTitle("Before Reduction", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN)));

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown));
        }

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Source Cooldowns", NamedTextColor.DARK_GREEN)));

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_BEFORE_ANY_REDUCTION_ATTACKER, m -> m.apply(event));
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown));
        }
    }

    private void calculateCriticals() {
        debugMessage.appendTitle("Crit Modifiers", NamedTextColor.AQUA);

        if (critChance.getBaseValue() > 0 && !flags.contains(InstanceFlags.IGNORE_CRIT_MODIFIERS)) {
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                abstractCooldown.applyModifiers(Modifier.DAMAGE_CRIT_CHANCE_ATTACKER, m -> m.apply(event, critChance));
                abstractCooldown.applyModifiers(Modifier.DAMAGE_CRIT_MULTIPLIER_ATTACKER, m -> m.apply(event, critMultiplier));
            }
        }

        critChance.refresh();
        critMultiplier.refresh();

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.GREEN))
                .value(critChance));
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.GREEN))
                .value(critMultiplier));

        setupDamageValue();
        applyCriticalHit();
    }

    private void setupDamageValue() {
        if (ignoreDamageReduction) {
            FloatModifiableFilter.InstancePierce pierceFilter = new FloatModifiableFilter.InstancePierce();
            damageValue.addFilter(pierceFilter);
        }
    }

    private void applyCriticalHit() {
        double crit = ThreadLocalRandom.current().nextDouble(100);
        calculatedCritChance = critChance.getCalculatedValue();
        calculatedCritMultiplier = critMultiplier.getCalculatedValue();
        isCrit = calculatedCritChance > 0 && crit <= calculatedCritChance && source.isCanCrit();

        if (isCrit) {
            damageValue.addMultiplicativeModifierMult("Crit Multiplier", calculatedCritMultiplier / 100f);
        }

        damageHealValueBeforeAllReduction = damageValue.getCalculatedValue();

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_POST_CRIT_CALCULATION_ATTACKER, m -> m.apply(
                            event,
                            damageHealValueBeforeAllReduction,
                            isCrit,
                            calculatedCritChance,
                            calculatedCritMultiplier
                    )
            );
        }

        debugMessage.appendTitle("Calculated Damage", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageHealValueBeforeAllReduction), NamedTextColor.GOLD))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create("" + isCrit, NamedTextColor.GOLD))
        );

        applySpecDamageResistance();
    }

    private void applySpecDamageResistance() {
        if (!flags.contains(InstanceFlags.IGNORE_SELF_RES) && !trueDamage) {
            damageValue.addMultiplicativeModifierAdd("Spec Damage Resistance", -warlordsEntity.getSpec().getDamageResistance() / 100f);
            debugMessage.appendTitle(ComponentBuilder
                    .create("Spec Damage Reduction: ", NamedTextColor.AQUA)
                    .text(NumberFormat.formatOptionalHundredths(warlordsEntity.getSpec().getDamageResistance()), NamedTextColor.BLUE)
                    .build()
            );
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(1)
                    .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                    .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(damageHealValueBeforeAllReduction), NamedTextColor.GOLD))
            );
        }
    }

    private boolean handleSelfInflictedDamage() {
        if (source != warlordsEntity || (!isFallDamage && !isMeleeHit)) {
            return false;
        }

        if (isMeleeHit) {
            handleMeleeDamage();
        } else {
            handleFallDamage();
        }

        warlordsEntity.cancelHealingPowerUp();
        return true;
    }

    private void handleMeleeDamage() {
        sendTookDamageMessage(min, "melee damage");
        warlordsEntity.resetRegenTimer();

        if (warlordsEntity.getCurrentHealth() - min <= 0) {
            warlordsEntity.die(source, createDeathInfo(min, "melee damage"));
        } else {
            warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() - min);
            warlordsEntity.playHurtAnimation(source);
        }
    }

    private void handleFallDamage() {
        sendTookDamageMessage(damageHealValueBeforeAllReduction, "fall damage");
        warlordsEntity.resetRegenTimer();

        if (warlordsEntity.getCurrentHealth() - damageHealValueBeforeAllReduction <= 0) {
            warlordsEntity.die(source, createDeathInfo(min, "fall damage"));
        } else {
            warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() - damageHealValueBeforeAllReduction);
            warlordsEntity.playHurtAnimation(source);
        }

        // Order of Eviscerate handling
        for (OrderOfEviscerate.OrderOfEviscerateData orderOfEviscerate : new CooldownFilter<>(source, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(OrderOfEviscerate.OrderOfEviscerateData.class)
                .toList()) {
            orderOfEviscerate.addAndCheckDamageThreshold(damageHealValueBeforeAllReduction, source);
        }
    }

    /**
     * Sends a simple damage message for self-inflicted damage (melee/fall)
     */
    private void sendTookDamageMessage(float damage, String damageType) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(
                warlordsEntity.getUuid(),
                warlordsEntity instanceof WarlordsPlayer && warlordsEntity.getEntity() instanceof Player
        );
        if (databasePlayer.getChatDamageMode() != ChatSettings.ChatDamage.ALL) {
            return;
        }
        TextComponent.Builder message = Component
                .text()
                .append(WarlordsEntity.RECEIVE_ARROW_RED)
                .append(Component.text(" You took ", NamedTextColor.GRAY))
                .append(Component.text(Math.round(damage), NamedTextColor.RED))
                .append(Component.text(" " + damageType + ".", NamedTextColor.GRAY));
        Component finalMessage = message.build().hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage()));
        warlordsEntity.sendMessage(finalMessage, true);
    }

    private WarlordsDeathEvent.DeathInfoBuilder createDeathInfo(float damage, String damageType) {
        return WarlordsDeathEvent.DeathInfoBuilder
                .create()
                .setTitle(Title.title(
                        Component.text("YOU DIED!", NamedTextColor.RED),
                        Component.text("You took ", NamedTextColor.GRAY)
                                 .append(Component.text(Math.round(damage), NamedTextColor.RED))
                                 .append(Component.text(" " + damageType + " and died.")),
                        Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                ));
    }

    private void applyFlagMultiplier() {
        double flagMultiplier = warlordsEntity.getFlagDamageMultiplier();
        if (flagMultiplier != 1 && !trueDamage && !flags.contains(InstanceFlags.IGNORE_FLAG_MULTIPLIER)) {
            damageValue.addMultiplicativeModifierMult("Flag Carrier Multiplier", (float) flagMultiplier);
            debugMessage.appendTitle(ComponentBuilder
                    .create("Flag Damage Multiplier: ", NamedTextColor.AQUA)
                    .text(NumberFormat.formatOptionalHundredths(flagMultiplier), NamedTextColor.BLUE)
                    .build()
            );
        }
    }

    private void applyBeforeInterveneModifiers() {
        if (trueDamage) {
            return;
        }

        debugMessage.appendTitle("Before Intervene", NamedTextColor.AQUA);

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_BEFORE_INTERVENE_SELF, m -> m.apply(event, damageValue));
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, m -> m.apply(event, damageValue));
        }

        damageValue.refresh();
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(damageValue));

        damageHealValueBeforeInterveneReduction = damageValue.getCalculatedValue();
    }

    private boolean handleIntervene() {
        Optional<LinkedCooldown<Intervene.InterveneData>> optionalInterveneCooldown = findInterveneCooldown();

        if (trueDamage || pierceDamage ||
                optionalInterveneCooldown.isEmpty() ||
                optionalInterveneCooldown.get().getTicksLeft() <= 0 ||
                !warlordsEntity.isEnemy(source)
        ) {
            return false;
        }

        LinkedCooldown<Intervene.InterveneData> interveneCooldown = optionalInterveneCooldown.get();
        if (validateIntervene(interveneCooldown)) {
            processIntervene(interveneCooldown);
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private Optional<LinkedCooldown<Intervene.InterveneData>> findInterveneCooldown() {
        return (Optional<LinkedCooldown<Intervene.InterveneData>>)
                (Optional<?>) new CooldownFilter<>(warlordsEntity, LinkedCooldown.class)
                        .filterCooldownClass(Intervene.InterveneData.class)
                        .filter(regularCooldown -> !Objects.equals(regularCooldown.getFrom(), warlordsEntity))
                        .findFirst();
    }

    private boolean validateIntervene(LinkedCooldown<Intervene.InterveneData> interveneCooldown) {
        if (interveneCooldown.getFrom() == source) {
            ChatUtils.MessageType.GAME.sendErrorMessage(
                    "Intervene Overflow? " + warlordsEntity.getName() +
                            " intervened from " + source.getName() + " - " + event
            );
            return false;
        }
        return true;
    }

    private void processIntervene(LinkedCooldown<Intervene.InterveneData> interveneCooldown) {
        debugMessage.appendTitle("Intervene", NamedTextColor.AQUA);

        Intervene.InterveneData data = interveneCooldown.getCooldownObject();
        WarlordsEntity intervenedBy = interveneCooldown.getFrom();
        intervenedBy.resetRegenTimer();

        float calculatedDamageValue = damageValue.getCalculatedValue();
        float maxDamagePrevented = data.getMaxDamagePrevented();
        float preDamagePrevented = data.getDamagePrevented();

        if (preDamagePrevented + calculatedDamageValue > maxDamagePrevented) {
            handleInterveneBreak(interveneCooldown, data, intervenedBy, calculatedDamageValue, maxDamagePrevented, preDamagePrevented);
        } else {
            handleInterveneHold(interveneCooldown, data, intervenedBy, calculatedDamageValue);
        }

        playInterveneEffects(intervenedBy, calculatedDamageValue);
    }

    private void handleInterveneBreak(
            LinkedCooldown<Intervene.InterveneData> interveneCooldown, Intervene.InterveneData data,
            WarlordsEntity intervenedBy, float calculatedDamageValue,
            float maxDamagePrevented, float preDamagePrevented
    ) {
        interveneCooldown.setTicksLeft(0);
        float overVeneDamage = preDamagePrevented + calculatedDamageValue - maxDamagePrevented;
        float leftOverPrevented = maxDamagePrevented - preDamagePrevented;

        data.addDamagePrevented(leftOverPrevented);
        intervenedBy.addAbsorbed(leftOverPrevented * (1 - data.getIntervene().getDamageReduction() / 100f));

        float reducedDamage = leftOverPrevented * (data.getIntervene().getDamageReduction() / 100f);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(reducedDamage), NamedTextColor.GOLD))
        );

        intervenedBy.addInstance(InstanceBuilder
                .damage()
                .cause("Intervene")
                .source(source)
                .value(reducedDamage)
                .showAsCrit(isCrit)
                .flags(InstanceFlags.TRUE_DAMAGE)
        );

        warlordsEntity.addInstance(InstanceBuilder
                .damage()
                .cause(cause)
                .source(source)
                .value(overVeneDamage)
                .showAsCrit(isCrit)
                .flags(InstanceFlags.TRUE_DAMAGE)
        ).ifPresent(finalEvent::set);
    }

    private void handleInterveneHold(
            LinkedCooldown<Intervene.InterveneData> interveneCooldown, Intervene.InterveneData data,
            WarlordsEntity intervenedBy, float calculatedDamageValue
    ) {
        float reducedDamage = calculatedDamageValue * (data.getIntervene().getDamageReduction() / 100f);

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(reducedDamage), NamedTextColor.GOLD))
        );

        data.addDamagePrevented(damageHealValueBeforeInterveneReduction);
        intervenedBy.addAbsorbed(damageHealValueBeforeInterveneReduction - reducedDamage);

        intervenedBy.addInstance(InstanceBuilder
                .damage()
                .cause("Intervene")
                .source(source)
                .value(reducedDamage)
                .showAsCrit(isCrit)
        );

        finalEvent.set(new WarlordsDamageHealingFinalEvent(
                event, flags, warlordsEntity, source, ability, cause,
                initialHealth, damageHealValueBeforeAllReduction,
                damageHealValueBeforeInterveneReduction, 0, 0,
                calculatedCritChance, calculatedCritMultiplier, isCrit, true,
                WarlordsDamageHealingFinalEvent.FinalEventFlag.INTERVENED
        ));
    }

    private void playInterveneEffects(WarlordsEntity intervenedBy, float calculatedDamageValue) {
        Location loc = warlordsEntity.getLocation();
        Utils.playGlobalSound(loc, "warrior.intervene.block", 2, 1);
        source.playHitSound();
        warlordsEntity.playHurtAnimation(source);
        intervenedBy.playHurtAnimation(source);
        EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), intervenedBy.getLocation(), 255, 0, 0, 2);

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Intervene From Attacker", NamedTextColor.GREEN))
        );

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_INTERVENE_ATTACKER, m -> m.apply(event, calculatedDamageValue));
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown)
            );
        }
    }

    private void applyAfterInterveneModifiers() {
        if (trueDamage) {
            return;
        }

        debugMessage.appendTitle("After Intervene", NamedTextColor.AQUA);

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_AFTER_INTERVENE_SELF, m -> m.apply(event, damageValue));
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_AFTER_INTERVENE_ATTACKER, m -> m.apply(event, damageValue));
        }

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Damage Value: ", NamedTextColor.GREEN))
                .value(damageValue));

        damageHealValueBeforeShieldReduction = damageValue.getCalculatedValue();
    }

    private boolean handleShield() {
        Optional<RegularCooldown<Shield>> shieldCooldown = findShieldCooldown();

        if (trueDamage || pierceDamage ||
                shieldCooldown.isEmpty() ||
                !warlordsEntity.isEnemy(source)
        ) {
            return false;
        }

        processShield(shieldCooldown.get());
        return true;
    }

    @SuppressWarnings("unchecked")
    private Optional<RegularCooldown<Shield>> findShieldCooldown() {
        return (Optional<RegularCooldown<Shield>>)
                (Optional<?>) new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClass(Shield.class)
                        .filter(RegularCooldown::hasTicksLeft)
                        .findFirst();
    }

    private void processShield(RegularCooldown<Shield> cooldown) {
        Shield shield = cooldown.getCooldownObject();
        debugMessage.appendTitle("Shield (" + shield.getName() + ")", NamedTextColor.AQUA);

        float preShieldHealth = shield.getShieldHealth();
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Pre Health: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(preShieldHealth), NamedTextColor.GOLD))
        );

        shield.addShieldHealth(-damageHealValueBeforeShieldReduction);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Post Health: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(shield.getShieldHealth()), NamedTextColor.GOLD))
        );

        if (shield.getShieldHealth() <= 0) {
            cooldown.setTicksLeft(0);
        }

        if (shield.isBroken()) {
            handleBrokenShield(shield, cooldown, preShieldHealth);
        } else {
            handleActiveShield(shield, cooldown);
        }
    }

    private void handleBrokenShield(Shield shield, RegularCooldown<Shield> cooldown, float preShieldHealth) {
        cooldown.getFrom().addAbsorbed(preShieldHealth);
        float newDamage = -shield.getShieldHealth();

        List<CustomInstanceFlags> newCustomFlags = new ArrayList<>(customFlags);
        customFlags.stream()
                   .filter(CustomInstanceFlags.InstanceShieldsInstanceFlag.class::isInstance)
                   .map(CustomInstanceFlags.InstanceShieldsInstanceFlag.class::cast)
                   .findAny()
                   .ifPresentOrElse(
                           customInstanceFlags -> customInstanceFlags.shields().add(shield),
                           () -> newCustomFlags.add(new CustomInstanceFlags.InstanceShieldsInstanceFlag(new ArrayList<>(List.of(shield))))
                   );

        finalEvent.set(addDamageInstance(new InstanceDebugHoverable(), new WarlordsDamageHealingEvent(
                        warlordsEntity, source, cause, newDamage, newDamage,
                        isCrit ? 100 : 0, 100, true,
                        EnumSet.of(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS),
                        newCustomFlags, debugMessages
                )
        ).orElse(null));
    }

    private void handleActiveShield(Shield shield, RegularCooldown<Shield> cooldown) {
        cooldown.getFrom().addAbsorbed(Math.abs(damageHealValueBeforeShieldReduction));
        Shield.updateAbsorption(warlordsEntity);

        sendShieldMessages(shield);
        applyShieldModifiers();

        warlordsEntity.playHurtAnimation(source);

        if (!flags.contains(InstanceFlags.NO_HIT_SOUND)) {
            warlordsEntity.playHitSound(source);
        }

        finalEvent.set(new WarlordsDamageHealingFinalEvent(
                event, flags, warlordsEntity, source, ability, cause,
                initialHealth, damageHealValueBeforeAllReduction,
                damageHealValueBeforeInterveneReduction, damageHealValueBeforeShieldReduction,
                damageHealValueBeforeShieldReduction, calculatedCritChance, calculatedCritMultiplier,
                isCrit, true, WarlordsDamageHealingFinalEvent.FinalEventFlag.SHIELDED,
                new ArrayList<>(List.of(new CustomInstanceFlags.InstanceShieldsInstanceFlag(List.of(shield))))
        ));
    }

    private void sendShieldMessages(Shield shield) {
        TextComponent.Builder ownMessage = Component.text();
        TextComponent.Builder attackerMessage = Component.text();

        if (isMeleeHit) {
            ownMessage.append(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You absorbed " + source.getName() + "'s melee hit.", NamedTextColor.GRAY)));
            attackerMessage.append(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your melee hit was absorbed by " + warlordsEntity.getName() + ".", NamedTextColor.GRAY)));
        } else {
            ownMessage.append(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You absorbed " + source.getName() + "'s " + cause + " hit.", NamedTextColor.GRAY)));
            attackerMessage.append(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your " + cause + " was absorbed by " + warlordsEntity.getName() + ".", NamedTextColor.GRAY)));
        }

        if (shield.getShieldHealth() >= 0) {
            sendPlayerMessage(warlordsEntity, ownMessage.build());
            sendPlayerMessage(source, attackerMessage.build());
        }
    }

    private void sendPlayerMessage(WarlordsEntity entity, Component message) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(
                entity.getUuid(),
                entity instanceof WarlordsPlayer && entity.getEntity() instanceof Player
        );

        switch (databasePlayer.getChatHealingMode()) {
            case ALL -> {
                if (entity.isShowDebugMessage()) {
                    entity.sendMessage(message.hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                } else {
                    entity.sendMessage(message);
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (entity.isShowDebugMessage()) {
                        entity.sendMessage(message.hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage())));
                    } else {
                        entity.sendMessage(message);
                    }
                }
            }
        }
    }

    private void applyShieldModifiers() {
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("On Shield", NamedTextColor.DARK_GREEN))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(2)
                .prefix(ComponentBuilder.create("Target Cooldowns", NamedTextColor.DARK_GREEN))
        );

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_SHIELD_SELF, m -> m.apply(event, damageHealValueBeforeShieldReduction, isCrit));
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
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_SHIELD_ATTACKER, m -> m.apply(event, damageHealValueBeforeShieldReduction, isCrit));
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(3)
                    .prefix(abstractCooldown)
            );
        }
    }

    private void applyFinalDamage() {
        debugMessage.appendTitle("Modify Damage After All", NamedTextColor.AQUA);

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_AFTER_ALL_SELF, m -> m.apply(event, damageValue, isCrit));
        }

        boolean debt = warlordsEntity.getCooldownManager().hasCooldownFromName("Spirits' Respite");
        warlordsEntity.getHitBy().put(source, 10);
        warlordsEntity.cancelHealingPowerUp();

        float finalDamageValue = damageValue.getCalculatedValue();

        updateAbilityPools(finalDamageValue);

        if (!flags.contains(InstanceFlags.NO_MESSAGE)) {
            sendDamageMessage(finalDamageValue);
        }

        applyOnDamageModifiers(finalDamageValue);

        warlordsEntity.resetRegenTimer();
        warlordsEntity.updateHealth();

        float cappedDamage = Math.min(finalDamageValue,
                warlordsEntity.getCurrentHealth() - (flags.contains(InstanceFlags.CANT_KILL) ? 1 : 0)
        );

        source.addDamage(cappedDamage, FlagHolder.isPlayerHolderFlag(warlordsEntity));
        warlordsEntity.addDamageTaken(cappedDamage);
        warlordsEntity.playHurtAnimation(source);

        if (source.isNoEnergyConsumption()) {
            source.getRecordDamage().add(cappedDamage);
        }

        float newHealth = calculateNewHealth(debt, finalDamageValue);
        warlordsEntity.setCurrentHealth(newHealth);

        if (newHealth <= 0) {
            handleDeath(finalDamageValue);
        } else {
            if (!flags.contains(InstanceFlags.NO_HIT_SOUND) && warlordsEntity != source && finalDamageValue != 0) {
                warlordsEntity.playHitSound(source);
            }
        }

        finalEvent.set(new WarlordsDamageHealingFinalEvent(
                event, flags, warlordsEntity, source, ability, cause,
                initialHealth, damageHealValueBeforeAllReduction,
                damageHealValueBeforeInterveneReduction, damageHealValueBeforeShieldReduction,
                finalDamageValue, calculatedCritChance, calculatedCritMultiplier,
                isCrit, true, WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR
        ));

        warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent.get());
        source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent.get());
    }

    private void updateAbilityPools(float finalDamageValue) {
        warlordsEntity.doOnStaticAbility(SoulShackle.class,
                soulShackle -> soulShackle.addToShacklePool(finalDamageValue)
        );
        warlordsEntity.doOnStaticAbility(Repentance.class,
                repentance -> repentance.addToPool(finalDamageValue)
        );
    }

    private void applyOnDamageModifiers(float finalDamageValue) {
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_DAMAGE_SELF, m -> m.apply(event, finalDamageValue, isCrit));
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_DAMAGE_ATTACKER, m -> m.apply(event, finalDamageValue, isCrit));
        }
    }

    private float calculateNewHealth(boolean debt, float finalDamageValue) {
        float newHealth = warlordsEntity.getCurrentHealth();
        if (!debt && warlordsEntity.isTakeDamage()) {
            newHealth = Math.min(warlordsEntity.getCurrentHealth() - finalDamageValue, warlordsEntity.getMaxHealth());
        }
        return newHealth;
    }

    private void handleDeath(float finalDamageValue) {
        warlordsEntity.die(source, WarlordsDeathEvent.DeathInfoBuilder
                .create()
                .setTitle(Title.title(
                        Component.text("YOU DIED!", NamedTextColor.RED),
                        Component.text(source.getName() + " killed you.", NamedTextColor.GRAY),
                        Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                ))
                .setOnDeathRunnable(() -> handleDeathCallback(finalDamageValue))
        );
    }

    private void handleDeathCallback(float finalDamageValue) {
        if (source.getEntity() instanceof Player player) {
            player.playSound(source.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 1);
            player.playSound(source.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 0.5f);
        }

        source.addKill();

        sendDeathMessages();
        applyDeathModifiers(finalDamageValue);
    }

    private void sendDeathMessages() {
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
    }

    private void applyDeathModifiers(float finalDamageValue) {
        for (WarlordsEntity enemy : PlayerFilter.playingGame(warlordsEntity.getGame())
                                                .enemiesOf(warlordsEntity)
                                                .stream()
                                                .toList()
        ) {
            for (AbstractCooldown<?> abstractCooldown : enemy.getCooldownManager().getCooldownsDistinct()) {
                abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_DEATH_ENEMIES,
                        m -> m.apply(event, finalDamageValue, isCrit, enemy == source)
                );
            }
        }
    }

    private void applyEndModifiers() {
        float finalDamageValue = damageValue.getCalculatedValue();

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.DAMAGE_ON_END_ATTACKER,
                    m -> m.apply(event, finalDamageValue, isCrit)
            );
        }
    }

    /**
     * Sends damage message to both attacker and target
     */
    private void sendDamageMessage(float damageValue) {
        TextComponent.Builder damageInfo = buildDamageMessage(damageValue);

        // Send message to target (receiver)
        TextComponent.Builder receiverFeed = Component.text()
                                                      .append(WarlordsEntity.RECEIVE_ARROW_RED)
                                                      .append(buildReceiverDamageText())
                                                      .append(damageInfo);

        sendDamageMessageBasedOnMode(warlordsEntity, receiverFeed.build());

        // Send message to attacker (sender)
        TextComponent.Builder senderFeed = Component.text()
                                                    .append(WarlordsEntity.GIVE_ARROW_GREEN)
                                                    .append(buildAttackerDamageText())
                                                    .append(damageInfo);

        sendDamageMessageBasedOnMode(source, senderFeed.build());
    }

    /**
     * Builds the damage amount and modifiers portion of the message
     */
    private TextComponent.Builder buildDamageMessage(float damageValue) {
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

        return secondHalf;
    }

    /**
     * Builds the text for the receiver's damage message
     */
    private TextComponent.Builder buildReceiverDamageText() {
        TextComponent.Builder hitBuilder = Component.text(" " + source.getName(), NamedTextColor.GRAY).toBuilder();

        if (!isMeleeHit) {
            hitBuilder.append(Component.text("'s " + cause));
        }

        hitBuilder.append(Component.text(" hit you for "));

        return hitBuilder;
    }

    /**
     * Builds the text for the attacker's damage message
     */
    private TextComponent.Builder buildAttackerDamageText() {
        TextComponent.Builder hitBuilder = Component.text(" ", NamedTextColor.GRAY).toBuilder();

        if (isMeleeHit) {
            hitBuilder.append(Component.text("You hit "));
        } else {
            hitBuilder.append(Component.text("Your " + cause + " hit "));
        }

        hitBuilder.append(Component.text(warlordsEntity.getName() + " for "));

        return hitBuilder;
    }

    /**
     * Sends damage message to player based on their chat damage mode settings
     */
    private void sendDamageMessageBasedOnMode(WarlordsEntity entity, Component message) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(
                entity.getUuid(),
                entity instanceof WarlordsPlayer && entity.getEntity() instanceof Player
        );

        Component finalMessage = message.hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage()));

        switch (databasePlayer.getChatDamageMode()) {
            case ALL -> {
                entity.sendMessage(finalMessage, true);
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    entity.sendMessage(finalMessage, true);
                }
            }
        }
    }

}