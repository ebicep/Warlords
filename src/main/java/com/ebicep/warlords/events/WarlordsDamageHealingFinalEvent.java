package com.ebicep.warlords.events;

import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class WarlordsDamageHealingFinalEvent extends WarlordsPlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private WarlordsPlayer attacker;
    private String ability;
    private int finalHealth;
    private float value;
    private int critChance;
    private int critMultiplier;

    private boolean hasFlag;
    private boolean isDamageInstance;

    private int inGameSecond;

    private boolean cancelled;

    public WarlordsDamageHealingFinalEvent(
            WarlordsPlayer player,
            WarlordsPlayer attacker,
            String ability,
            float value,
            int critChance,
            int critMultiplier,
            boolean isDamageInstance
    ) {
        super(player);
        this.attacker = attacker;
        this.ability = ability;
        this.finalHealth = player.getHealth();
        this.value = value;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.hasFlag = player.getFlagDamageMultiplier() != 1;

        this.isDamageInstance = isDamageInstance;

        if (getGame().getState() instanceof PlayingState) {
            this.inGameSecond = ((PlayingState) getGame().getState()).getTicksElapsed() / 20;
        }
    }

    public WarlordsPlayer getAttacker() {
        return attacker;
    }

    public String getAbility() {
        return ability;
    }

    public int getFinalHealth() {
        return finalHealth;
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

    public boolean isHasFlag() {
        return hasFlag;
    }

    public boolean isDamageInstance() {
        return isDamageInstance;
    }

    public boolean isHealingInstance() {
        return !isDamageInstance;
    }

    public int getInGameSecond() {
        return inGameSecond;
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
}
