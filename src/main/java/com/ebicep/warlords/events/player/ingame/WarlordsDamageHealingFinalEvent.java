package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WarlordsDamageHealingFinalEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    private final WarlordsDamageHealingEvent warlordsDamageHealingEvent;
    private final List<CooldownRecord> playerCooldowns = new ArrayList<>();
    private final List<CooldownRecord> attackerCooldowns = new ArrayList<>();
    private final EnumSet<InstanceFlags> instanceFlags;
    private final WarlordsEntity attacker;
    private final String ability;
    private final float initialHealth;
    private final float finalHealth;
    private final float valueBeforeAllReduction;
    private final float valueBeforeInterveneReduction;
    private final float valueBeforeShieldReduction;
    private final float value;
    private final float critChance;
    private final float critMultiplier;
    private final boolean isCrit;
    private final boolean hasFlag;
    private final boolean isDead;

    private final boolean attackerInCombat;

    private final boolean isDamageInstance;

    private final int inGameTick;

    private final FinalEventFlag finalEventFlag;

    public WarlordsDamageHealingFinalEvent(
            WarlordsDamageHealingEvent warlordsDamageHealingEvent,
            EnumSet<InstanceFlags> instanceFlags,
            WarlordsEntity player,
            WarlordsEntity attacker,
            String ability,
            float initialHealth,
            float valueBeforeAllReduction,
            float valueBeforeInterveneReduction,
            float valueBeforeShieldReduction,
            float value,
            float critChance,
            float critMultiplier,
            boolean isCrit,
            boolean isDamageInstance,
            FinalEventFlag finalEventFlag
    ) {
        super(player);
        this.warlordsDamageHealingEvent = warlordsDamageHealingEvent;
        this.instanceFlags = instanceFlags;
        this.finalEventFlag = finalEventFlag;
        this.playerCooldowns.addAll(player.getCooldownManager().getCooldowns().stream()
                                          .map(CooldownRecord::new)
                                          .toList()
        );
        this.attackerCooldowns.addAll(attacker.getCooldownManager().getCooldowns().stream()
                                              .map(CooldownRecord::new)
                                              .toList()
        );
        this.attacker = attacker;
        this.ability = ability;
        this.initialHealth = initialHealth;
        this.finalHealth = player.getCurrentHealth();
        this.valueBeforeAllReduction = valueBeforeAllReduction;
        this.valueBeforeInterveneReduction = valueBeforeInterveneReduction;
        this.valueBeforeShieldReduction = valueBeforeShieldReduction;
        this.value = value;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.isCrit = isCrit;
        this.hasFlag = player.hasFlag();
        this.isDead = isDamageInstance && player.getCurrentHealth() <= 0 && !player.getCooldownManager().checkUndyingArmy(false);

        this.attackerInCombat = attacker.getRegenTickTimer() > 6 * 20;

        this.isDamageInstance = isDamageInstance;

        this.inGameTick = player.getGame().getState().getTicksElapsed();
    }

    public List<CooldownRecord> getPlayerCooldowns() {
        return playerCooldowns;
    }

    public List<CooldownRecord> getAttackerCooldowns() {
        return attackerCooldowns;
    }

    public EnumSet<InstanceFlags> getInstanceFlags() {
        return instanceFlags;
    }

    public WarlordsEntity getAttacker() {
        return attacker;
    }

    public String getAbility() {
        return ability;
    }

    public float getInitialHealth() {
        return initialHealth;
    }

    public float getFinalHealth() {
        return finalHealth;
    }

    public float getValueBeforeAllReduction() {
        return valueBeforeAllReduction;
    }

    public float getValueBeforeInterveneReduction() {
        return valueBeforeInterveneReduction;
    }

    public float getValueBeforeShieldReduction() {
        return valueBeforeShieldReduction;
    }

    public float getValue() {
        return value;
    }

    public float getCritChance() {
        return critChance;
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    public boolean isCrit() {
        return isCrit;
    }

    public boolean isHasFlag() {
        return hasFlag;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isAttackerInCombat() {
        return attackerInCombat;
    }

    public boolean isDamageInstance() {
        return isDamageInstance;
    }

    public boolean isHealingInstance() {
        return !isDamageInstance;
    }

    public int getInGameTick() {
        return inGameTick;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return "WarlordsDamageHealingFinalEvent{" +
                "warlordsDamageHealingEvent=" + warlordsDamageHealingEvent +
                ", playerCooldowns=" + playerCooldowns +
                ", attackerCooldowns=" + attackerCooldowns +
                ", attacker=" + attacker +
                ", ability='" + ability + '\'' +
                ", initialHealth=" + initialHealth +
                ", finalHealth=" + finalHealth +
                ", valueBeforeAllReduction=" + valueBeforeAllReduction +
                ", valueBeforeInterveneReduction=" + valueBeforeInterveneReduction +
                ", valueBeforeShieldReduction=" + valueBeforeShieldReduction +
                ", value=" + value +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                ", isCrit=" + isCrit +
                ", hasFlag=" + hasFlag +
                ", isDead=" + isDead +
                ", attackerInCombat=" + attackerInCombat +
                ", isDamageInstance=" + isDamageInstance +
                ", inGameTick=" + inGameTick +
                ", finalEventFlag=" + finalEventFlag +
                '}';
    }

    public FinalEventFlag getFinalEventFlag() {
        return finalEventFlag;
    }

    public enum FinalEventFlag {

        REGULAR,
        INTERVENED,
        SHIELDED

    }

    public static class CooldownRecord {

        private final AbstractCooldown<?> abstractCooldown;
        private final int ticksLeft;

        public CooldownRecord(AbstractCooldown<?> abstractCooldown) {
            this.abstractCooldown = abstractCooldown;
            if (abstractCooldown instanceof RegularCooldown) {
                this.ticksLeft = ((RegularCooldown<?>) abstractCooldown).getTicksLeft();
            } else {
                this.ticksLeft = -1;
            }
        }

        public AbstractCooldown<?> getAbstractCooldown() {
            return abstractCooldown;
        }

        public int getTicksLeft() {
            return ticksLeft;
        }
    }
}
