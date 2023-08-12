package com.ebicep.warlords.game.option.raid;

import com.ebicep.warlords.player.ingame.WarlordsNPC;

public class BossAbilityPhase {

    private boolean triggered = false;
    private final WarlordsNPC bossEntity;
    private final float healthThreshold;
    private final Runnable bossAbility;

    /**
     *
     * @param bossEntity npc entity the boss ability should be assigned to.
     * @param healthThreshold health threshold in percent, indicates when the ability should be activated.
     * @param bossAbility what should happen during the ability?
     */
    public BossAbilityPhase(WarlordsNPC bossEntity, float healthThreshold, Runnable bossAbility) {
        this.bossEntity = bossEntity;
        this.healthThreshold = healthThreshold / 100f;
        this.bossAbility = bossAbility;
    }

    public void initialize(float currentHealth) {
        if (triggered) {
            return;
        }

        if (bossEntity.isDead()) {
            return;
        }

        if (bossAbility == null) {
            return;
        }

        if (currentHealth < (bossEntity.getMaxHealth() * healthThreshold)) {
            bossAbility.run();
            triggered = true;
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    public WarlordsNPC getBossEntity() {
        return bossEntity;
    }

    public float requiredHealthToActivate() {
        return bossEntity.getMaxHealth() * healthThreshold;
    }
}
