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
import com.ebicep.warlords.util.warlords.modifiablevalues.filters.InstancePierce;
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
    private final float min;
    private final float max;
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
        this.max = event.getMax();
        this.critChance = new FloatModifiable(event.getCritChance());
        this.critMultiplier = new FloatModifiable(event.getCritMultiplier());
        this.flags = event.getFlags();
        this.isLastStandFromShield = flags.contains(InstanceFlags.LAST_STAND_FROM_SHIELD);
        this.pierce = flags.contains(InstanceFlags.PIERCE);
        this.trueHealing = flags.contains(InstanceFlags.TRUE_HEALING);
        this.initialHealth = warlordsEntity.getCurrentHealth();
        this.selfCooldownsDistinct = warlordsEntity.getCooldownManager().getCooldownsDistinct();
        this.attackersCooldownsDistinct = source.getCooldownManager().getCooldownsDistinct();
        this.healValue = new FloatModifiable((float) ((Math.random() * (max - min)) + min));
    }

    public Optional<WarlordsDamageHealingFinalEvent> process() {
        applyPreEventModifiers();

        if (!validateEntityState()) {
            return Optional.empty();
        }

        setupDebugMessages();
        calculateCriticalHealing();
        applyHealingModifiers();

        healValueAfterModify = healValue.getCalculatedValue();

        if (!isValidHealingTarget()) {
            return Optional.empty();
        }

        float cappedHealValue = calculateCappedHealValue();

        if (cappedHealValue <= 0) {
            return Optional.empty();
        }

        sendHealingMessages(cappedHealValue);
        applyOnHealModifiers(cappedHealValue);
        applyHealingToEntity(cappedHealValue);

        WarlordsDamageHealingFinalEvent finalEvent = createFinalEvent();
        updateStatistics(finalEvent);

        return Optional.of(finalEvent);
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
        debugMessage.appendTitle("Post Event", NamedTextColor.AQUA);
        debugMessage.appendEvent(event);
    }

    private void calculateCriticalHealing() {
        setupPierceFilter();
        calculateCritical();

        healValueBeforeReduction = healValue.getCalculatedValue();

        logCalculatedHeal();
    }

    private void setupPierceFilter() {
        if (pierce) {
            InstancePierce pierceFilter = new InstancePierce();
            healValue.addFilter(pierceFilter);
        }
    }

    private void calculateCritical() {
        double crit = ThreadLocalRandom.current().nextDouble(100);
        calculatedCritChance = critChance.getCalculatedValue();
        calculatedCritMultiplier = critMultiplier.getCalculatedValue();
        isCrit = calculatedCritChance > 0 && crit <= calculatedCritChance && source.isCanCrit();

        if (isCrit) {
            healValue.addMultiplicativeModifierMult("Crit Multiplier", calculatedCritMultiplier / 100f);
        }
    }

    private void logCalculatedHeal() {
        debugMessage.appendTitle("Calculated Heal", NamedTextColor.AQUA);
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create(NumberFormat.formatOptionalHundredths(healValueBeforeReduction), NamedTextColor.GOLD))
        );
        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Crit: ", NamedTextColor.GREEN))
                .value(ComponentBuilder.create("" + isCrit, NamedTextColor.GOLD))
        );
    }

    private void applyHealingModifiers() {
        debugMessage.appendTitle("Before Heal", NamedTextColor.AQUA);

        if (trueHealing) {
            return;
        }

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_MODIFY_SELF, m -> m.apply(event, healValue));
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_MODIFY_ATTACKER, m -> m.apply(event, healValue));
        }

        debugMessage.append(InstanceDebugHoverable.LevelBuilder
                .create(1)
                .prefix(ComponentBuilder.create("Heal Value: ", NamedTextColor.GREEN))
                .value(healValue));
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

    private float calculateMaxHealth() {
        float maxHealth = warlordsEntity.getHealth().getCalculatedValue();

        if (canOverheal()) {
            maxHealth *= 1.1f;
        }

        return maxHealth;
    }

    private boolean canOverheal() {
        boolean overhealSelf = warlordsEntity == source && flags.contains(InstanceFlags.CAN_OVERHEAL_SELF);
        boolean overhealOthers = warlordsEntity != source &&
                warlordsEntity.isTeammate(source) &&
                flags.contains(InstanceFlags.CAN_OVERHEAL_OTHERS);

        return overhealSelf || overhealOthers;
    }

    private void sendHealingMessages(float cappedHealValue) {
        boolean isOverheal = isOverhealing(cappedHealValue);

        if (warlordsEntity == source) {
            sendSelfHealingMessage(cappedHealValue, isOverheal);
        } else {
            sendHealingOthersMessage(cappedHealValue, isOverheal);
        }
    }

    private boolean isOverhealing(float healAmount) {
        float maxHealth = calculateMaxHealth();
        float newHealth = healAmount + warlordsEntity.getCurrentHealth();

        return maxHealth > warlordsEntity.getMaxHealth() &&
                newHealth > warlordsEntity.getMaxBaseHealth();
    }

    private void applyOnHealModifiers(float cappedHealValue) {
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_ON_HEAL_SELF,
                    m -> m.apply(event, cappedHealValue, isCrit)
            );
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.applyModifiers(Modifier.HEALING_ON_HEAL_ATTACKER,
                    m -> m.apply(event, cappedHealValue, isCrit)
            );
        }
    }

    private void applyHealingToEntity(float cappedHealValue) {
        float maxHealth = calculateMaxHealth();
        float actualHealing = Math.min(cappedHealValue, maxHealth - warlordsEntity.getCurrentHealth());

        source.addHealing(actualHealing, FlagHolder.isPlayerHolderFlag(warlordsEntity));
        warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() + cappedHealValue);

        if (!flags.contains(InstanceFlags.NO_HIT_SOUND)) {
            warlordsEntity.playHitSound(source);
        }
    }

    private WarlordsDamageHealingFinalEvent createFinalEvent() {
        return new WarlordsDamageHealingFinalEvent(
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
                healValueAfterModify,
                calculatedCritChance,
                calculatedCritMultiplier,
                isCrit,
                false,
                WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR
        );
    }

    private void updateStatistics(WarlordsDamageHealingFinalEvent finalEvent) {
        warlordsEntity.getSecondStats().addDamageHealingEventAsSelf(finalEvent);
        source.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
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

    /**
     * Sends message to player based on their chat healing mode settings
     */
    private void sendMessageBasedOnMode(WarlordsEntity entity, Component message) {
        sendMessageBasedOnMode(entity, message, false);
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

}