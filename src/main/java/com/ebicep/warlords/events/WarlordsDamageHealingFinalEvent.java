package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class WarlordsDamageHealingFinalEvent extends AbstractWarlordsPlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final WarlordsPlayer attacker;
    private final String ability;
    private final int initialHealth;
    private final int finalHealth;
    private final float valueBeforeAllReduction;
    private final float valueBeforeInterveneReduction;
    private final float valueBeforeShieldReduction;
    private final float value;
    private final int critChance;
    private final int critMultiplier;
    private final boolean isCrit;
    private final boolean hasFlag;
    private final boolean isDead;

    private boolean isDamageInstance;

    private int inGameTick;

    private boolean cancelled;

    public WarlordsDamageHealingFinalEvent(
            WarlordsPlayer player,
            WarlordsPlayer attacker,
            String ability,
            int initialHealth,
            float valueBeforeAllReduction,
            float valueBeforeInterveneReduction,
            float valueBeforeShieldReduction,
            float value,
            int critChance,
            int critMultiplier,
            boolean isCrit,
            boolean isDamageInstance
    ) {
        super(player);
        this.attacker = attacker;
        this.ability = ability;
        this.initialHealth = initialHealth;
        this.finalHealth = player.getHealth();
        this.valueBeforeAllReduction = valueBeforeAllReduction;
        this.valueBeforeInterveneReduction = valueBeforeInterveneReduction;
        this.valueBeforeShieldReduction = valueBeforeShieldReduction;
        this.value = value;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.hasFlag = player.hasFlag();
        this.isDead = isDamageInstance && player.getHealth() <= 0 && !player.getCooldownManager().checkUndyingArmy(false);
        this.isCrit = isCrit;
        this.isDamageInstance = isDamageInstance;

        this.inGameTick = player.getGameState().getTicksElapsed();
    }

    public WarlordsPlayer getAttacker() {
        return attacker;
    }

    public String getAbility() {
        return ability;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public int getFinalHealth() {
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

    public int getCritChance() {
        return critChance;
    }

    public int getCritMultiplier() {
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

    public boolean isDamageInstance() {
        return isDamageInstance;
    }

    public boolean isHealingInstance() {
        return !isDamageInstance;
    }

    public int getInGameTick() {
        return inGameTick;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

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
                "attacker=" + attacker +
                ", ability='" + ability + '\'' +
                ", initialHealth=" + initialHealth +
                ", finalHealth=" + finalHealth +
                ", value=" + value +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                ", hasFlag=" + hasFlag +
                ", isDead=" + isDead +
                ", isDamageInstance=" + isDamageInstance +
                ", inGameSecond=" + inGameTick +
                ", cancelled=" + cancelled +
                ", player=" + player +
                '}';
    }
}
