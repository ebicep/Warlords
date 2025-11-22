package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class HealingInstanceProcessor {

    public static Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(
            InstanceDebugHoverable debugMessage,
            WarlordsDamageHealingEvent event
    ) {
        HealingInstanceProcessor processor = new HealingInstanceProcessor(debugMessage, event);
        return processor.process();
    }

    // Core event data
    private final InstanceDebugHoverable debugMessage;
    private final WarlordsDamageHealingEvent event;
    private final WarlordsEntity warlordsEntity;
    private final WarlordsEntity source;
    private final AbstractAbility ability;
    private final String cause;
    // Healing values
    private final FloatModifiable min;
    private final FloatModifiable max;
    private final FloatModifiable critChance;
    private final FloatModifiable critMultiplier;
    private final FloatModifiable healValue;
    // Flags
    private final EnumSet<InstanceFlags> flags;
    private final boolean isLastStandFromShield;
    private final boolean pierce;
    private final boolean trueHealing;
    // State tracking
    private final float initialHealth;
    private final List<AbstractCooldown<?>> selfCooldownsDistinct;
    private final List<AbstractCooldown<?>> attackersCooldownsDistinct;
    private WarlordsDamageHealingFinalEvent finalEvent;
    // Calculated values
    private float healValueBeforeReduction;
    private float healValueAfterModify;
    private float calculatedCritChance;
    private float calculatedCritMultiplier;
    private boolean isCrit;

    public HealingInstanceProcessor(InstanceDebugHoverable debugMessage, WarlordsDamageHealingEvent event) {
        this.debugMessage = debugMessage;
        this.event = event;
        this.warlordsEntity = event.getWarlordsEntity();
        this.source = event.getSource();
        this.ability = event.getAbility();
        this.cause = event.getCause();
        this.min = event.getMin();
        this.min.refresh();
        this.max = event.getMax();
        this.max.refresh();
        this.critChance = event.getCritChance();
        this.critMultiplier = event.getCritMultiplier();
        this.flags = event.getFlags();
        this.isLastStandFromShield = flags.contains(InstanceFlags.LAST_STAND_FROM_SHIELD);
        this.pierce = flags.contains(InstanceFlags.PIERCE);
        this.trueHealing = flags.contains(InstanceFlags.TRUE_HEALING);
        this.initialHealth = warlordsEntity.getCurrentHealth();
        this.selfCooldownsDistinct = warlordsEntity.getCooldownManager().getCooldownsDistinct();
        this.attackersCooldownsDistinct = source.getCooldownManager().getCooldownsDistinct();
        this.finalEvent = null;
        this.healValue = new FloatModifiable(0);
    }

    public Optional<WarlordsDamageHealingFinalEvent> process() {
        applyPreEventModifiers();
        this.min.refresh();
        this.max.refresh();
        float maxVal = max.getCalculatedValue();
        float minVal = min.getCalculatedValue();
        this.healValue.setBaseValue(ThreadLocalRandom.current().nextFloat() * (maxVal - minVal) + minVal);


        if (!validateEntityState()) {
            return Optional.empty();
        }

        setupDebugMessages();
        applyBeforeReductionModifiers();
        calculateCriticals();
        applyHealingModifiers();

        if (!isValidHealingTarget()) {
            return Optional.empty();
        }

        float cappedHealValue = calculateCappedHealValue();

        if (cappedHealValue <= 0) {
            return Optional.empty();
        }

        applyFinalHealing(cappedHealValue);

        return Optional.ofNullable(finalEvent);
    }

    private void applyFinalHealing(float cappedHealValue) {
        healValue.callContributionCallbacks();

        applyOnHealModifiers(cappedHealValue);

        if (!flags.contains(InstanceFlags.NO_MESSAGE)) {
            boolean isOverHeal = isOverHealing(cappedHealValue);
            if (warlordsEntity == source) {
                sendSelfHealingMessage(cappedHealValue, isOverHeal);
            } else {
                sendHealingOthersMessage(cappedHealValue, isOverHeal);
            }
        }

        float maxHealth = calculateMaxHealth();
        float actualHealing = Math.min(cappedHealValue, maxHealth - warlordsEntity.getCurrentHealth());
        source.addHealing(actualHealing, FlagHolder.isPlayerHolderFlag(warlordsEntity));
        warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() + cappedHealValue);
        warlordsEntity.updateHealth();

        if (!flags.contains(InstanceFlags.NO_HIT_SOUND)) {
            warlordsEntity.playHitSound(source);
        }

        finalEvent = new WarlordsDamageHealingFinalEvent(
                event, flags, warlordsEntity, source, ability, cause,
                initialHealth, healValueBeforeReduction,
                healValueBeforeReduction, healValueBeforeReduction, healValueAfterModify,
                calculatedCritChance, calculatedCritMultiplier, isCrit, false,
                WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR
        );

        warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent);
        source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
    }

    private boolean isOverHealing(float healAmount) {
        float maxHealth = calculateMaxHealth();
        float newHealth = healAmount + warlordsEntity.getCurrentHealth();

        return maxHealth > warlordsEntity.getMaxHealth() &&
                newHealth > warlordsEntity.getMaxBaseHealth();
    }

    /**
     * Sends healing message for self-healing
     */
    private void sendSelfHealingMessage(float healValue, boolean isOverHeal) {
        TextComponent.Builder message = buildHealingMessage(healValue);

        TextComponent.Builder ownFeed = Component
                .text()
                .append(WarlordsEntity.GIVE_ARROW_GREEN)
                .append(buildSelfHealText())
                .append(message);

        sendMessageBasedOnMode(warlordsEntity, ownFeed.build());
    }

    /**
     * Sends healing message for healing others
     */
    private void sendHealingOthersMessage(float healValue, boolean isOverHeal) {
        TextComponent.Builder healInfo = buildHealingMessage(healValue);

        // Send message to healer (source)
        TextComponent.Builder senderFeed = Component
                .text()
                .append(WarlordsEntity.GIVE_ARROW_GREEN)
                .append(buildHealerText(isOverHeal))
                .append(healInfo);

        sendMessageBasedOnMode(source, senderFeed.build(), true);

        // Send message to target (warlordsEntity)
        TextComponent.Builder receiverFeed = Component
                .text()
                .append(WarlordsEntity.RECEIVE_ARROW_GREEN)
                .append(buildReceiverText(isOverHeal))
                .append(healInfo);

        sendMessageBasedOnMode(warlordsEntity, receiverFeed.build(), true);
    }

    private void applyOnHealModifiers(float cappedHealValue) {
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.ON_INCOMING_HEALING,
                    m -> m.apply(event, cappedHealValue, isCrit)
            );
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.ON_OUTGOING_HEALING,
                    m -> m.apply(event, cappedHealValue, isCrit)
            );
        }
    }

    private float calculateMaxHealth() {
        float maxHealth = warlordsEntity.getHealth().getCalculatedValue();

        if (canOverheal()) {
            maxHealth *= 1.1f;
        }

        return maxHealth;
    }

    /**
     * Builds the healing amount portion of the message
     */
    private TextComponent.Builder buildHealingMessage(float healValue) {
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

        return secondHalf;
    }

    /**
     * Builds the text for self-healing message
     */
    private TextComponent.Builder buildSelfHealText() {
        TextComponent.Builder hitBuilder = Component.text(" Your " + cause, NamedTextColor.GRAY).toBuilder();

        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }

        hitBuilder.append(Component.text(" healed you for "));

        return hitBuilder;
    }

    /**
     * Sends message to player based on their chat healing mode settings
     */
    private void sendMessageBasedOnMode(WarlordsEntity entity, Component message) {
        sendMessageBasedOnMode(entity, message, false);
    }

    /**
     * Builds the text for the healer's message when healing others
     */
    private TextComponent.Builder buildHealerText(boolean isOverHeal) {
        TextComponent.Builder hitBuilder = Component.text(" Your " + cause, NamedTextColor.GRAY).toBuilder();

        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }

        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed " + warlordsEntity.getName() + " for "));
        } else {
            hitBuilder.append(Component.text(" healed " + warlordsEntity.getName() + " for "));
        }

        return hitBuilder;
    }

    /**
     * Sends message to player based on their chat healing mode settings
     *
     * @param actionBar whether to send as action bar message
     */
    private void sendMessageBasedOnMode(WarlordsEntity entity, Component message, boolean actionBar) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(
                entity.getUuid(),
                entity instanceof WarlordsPlayer && entity.getEntity() instanceof Player
        );

        Component finalMessage = message.hoverEvent(HoverEvent.showText(debugMessage.getDebugMessage()));

        switch (databasePlayer.getChatHealingMode()) {
            case ALL -> {
                if (actionBar) {
                    entity.sendMessage(finalMessage, true);
                } else {
                    entity.sendMessage(finalMessage);
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (actionBar) {
                        entity.sendMessage(finalMessage, true);
                    } else {
                        entity.sendMessage(finalMessage);
                    }
                }
            }
        }
    }

    /**
     * Builds the text for the receiver's message when being healed by others
     */
    private TextComponent.Builder buildReceiverText(boolean isOverHeal) {
        TextComponent.Builder hitBuilder = Component.text(" " + source.getName() + "'s " + cause, NamedTextColor.GRAY).toBuilder();

        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }

        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed you for "));
        } else {
            hitBuilder.append(Component.text(" healed you for "));
        }

        return hitBuilder;
    }

    private boolean canOverheal() {
        return warlordsEntity == source && flags.contains(InstanceFlags.CAN_OVERHEAL_SELF) // self
                ||
                warlordsEntity != source && // others
                        warlordsEntity.isTeammate(source) &&
                        flags.contains(InstanceFlags.CAN_OVERHEAL_OTHERS);
    }

    private void applyPreEventModifiers() {
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_BEFORE_VARIABLE_SET_SELF, m -> m.apply(event));
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_BEFORE_VARIABLE_SET_ATTACKER, m -> m.apply(event));
        }
    }

    private boolean validateEntityState() {
        return !warlordsEntity.isDead() && warlordsEntity.isActive();
    }

    private void setupDebugMessages() {
        debugMessage.appendTitle("Initial", NamedTextColor.AQUA);
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
            debugMessage.append(InstanceDebugHoverable.LevelBuilder
                    .create(2)
                    .prefix(abstractCooldown));
        }
    }

    private void calculateCriticals() {
        critChance.refresh();
        critMultiplier.refresh();

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit Chance: ", NamedTextColor.LIGHT_PURPLE))
                .value(critChance));
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit Multiplier: ", NamedTextColor.LIGHT_PURPLE))
                .value(critMultiplier));

        applyCriticalHit();
    }

    private void applyHealingModifiers() {
        debugMessage.appendTitle("Before Heal", NamedTextColor.AQUA);

        if (trueHealing) {
            healValue.addModifierListener(
                    InstanceFlags.TRUE_HEALING.createDisabledReason(),
                    FloatModifiable.ModifierType.ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE
            );
        }
        // source / self modifiers
        if (pierce) { // ignore healing reduction
            toggleNegativeBoosts(InstanceFlags.PIERCE, true);
        }
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.MODIFY_INCOMING_HEALING, m -> m.apply(event, healValue));
        }
        if (pierce) { // ignore healing reduction
            toggleNegativeBoosts(InstanceFlags.PIERCE, false);
        }
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.MODIFY_OUTGOING_HEALING, m -> m.apply(event, healValue));
        }

        healValue.refresh();
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.LIGHT_PURPLE))
                .value(healValue));

        healValueAfterModify = healValue.getCalculatedValue();
    }

    private void applyCriticalHit() {
        double crit = ThreadLocalRandom.current().nextDouble(100);
        calculatedCritChance = critChance.getCalculatedValue();
        calculatedCritMultiplier = critMultiplier.getCalculatedValue();
        isCrit = calculatedCritChance > 0 && crit <= calculatedCritChance && source.isCanCrit();

        if (isCrit) {
            healValue.addMultiplicativeModifierMult("Crit Multiplier", calculatedCritMultiplier / 100f);
        }

        healValueBeforeReduction = healValue.getCalculatedValue();

        debugMessage.appendTitle("Calculated Heal", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.LIGHT_PURPLE))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(healValueBeforeReduction), NamedTextColor.GOLD))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit: ", NamedTextColor.LIGHT_PURPLE))
                .value(ComponentBuilder.create("" + isCrit, NamedTextColor.GOLD))
        );
    }

    private void toggleNegativeBoosts(InstanceFlags f, boolean addListener) {
        if (addListener) {
            healValue.addModifierListener(
                    f.ignoreNegativeAdditive,
                    FloatModifiable.ModifierType.ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE
            );
            healValue.addModifierListener(
                    f.ignoreNegativeMultiplicative,
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE
            );
        } else {
            healValue.removeModifierListener(
                    f.ignoreNegativeAdditive,
                    FloatModifiable.ModifierType.ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE
            );
            healValue.removeModifierListener(
                    f.ignoreNegativeMultiplicative,
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE
            );
        }
    }

    private void togglePositiveBoosts(InstanceFlags f, boolean addListener) {
        if (addListener) {
            healValue.addModifierListener(
                    f.ignorePositiveAdditive,
                    FloatModifiable.ModifierType.ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE
            );
            healValue.addModifierListener(
                    f.ignorePositiveMultiplicative,
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE
            );
        } else {
            healValue.removeModifierListener(
                    f.ignorePositiveAdditive,
                    FloatModifiable.ModifierType.ADDITIVE, FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE
            );
            healValue.removeModifierListener(
                    f.ignorePositiveMultiplicative,
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE
            );
        }
    }

    private boolean isValidHealingTarget() {
        return warlordsEntity == source || warlordsEntity.isTeammate(source);
    }

    private float calculateCappedHealValue() {
        float maxHealth = calculateMaxHealth();
        float potentialNewHealth = warlordsEntity.getCurrentHealth() + healValueAfterModify;

        if (potentialNewHealth > maxHealth) {
            return maxHealth - warlordsEntity.getCurrentHealth();
        }

        return healValueAfterModify;
    }

}