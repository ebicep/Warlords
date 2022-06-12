package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarlordsDamageHealingFinalEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final List<CooldownRecord> playerCooldowns = new ArrayList<>();
    private final List<CooldownRecord> attackerCooldowns = new ArrayList<>();
    private final WarlordsEntity attacker;
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

    private final boolean attackerInCombat;

    private boolean isDamageInstance;

    private int inGameTick;

    public WarlordsDamageHealingFinalEvent(
            WarlordsEntity player,
            WarlordsEntity attacker,
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
        this.playerCooldowns.addAll(player.getCooldownManager().getCooldowns().stream()
                .map(CooldownRecord::new)
                .collect(Collectors.toList())
        );
        this.attackerCooldowns.addAll(attacker.getCooldownManager().getCooldowns().stream()
                .map(CooldownRecord::new)
                .collect(Collectors.toList())
        );
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
        this.isCrit = isCrit;
        this.hasFlag = player.hasFlag();
        this.isDead = isDamageInstance && player.getHealth() <= 0 && !player.getCooldownManager().checkUndyingArmy(false);

        this.attackerInCombat = attacker.getRegenTimer() > 6;

        this.isDamageInstance = isDamageInstance;

        this.inGameTick = player.getGameState().getTicksElapsed();
    }

    public List<CooldownRecord> getPlayerCooldowns() {
        return playerCooldowns;
    }

    public List<CooldownRecord> getAttackerCooldowns() {
        return attackerCooldowns;
    }

    public WarlordsEntity getAttacker() {
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
                "playerCooldowns=" + playerCooldowns +
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
                '}';
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
