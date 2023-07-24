package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Objects;

public class ItemAdditiveCooldown extends PermanentCooldown<AbstractItem> {

    public static void increaseDamage(WarlordsPlayer warlordsPlayer, float damageBoost) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addDamageBoost(damageBoost);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, damageBoost, 0, 0, 0, 0, 0, 0));
    }

    public void addDamageBoost(float damageBoost) {
        this.damageMultiplier += damageBoost / 100f;
    }

    public static void increaseHealing(WarlordsPlayer warlordsPlayer, float healBoost) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addHealBoost(healBoost);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, healBoost, 0, 0, 0, 0, 0));
    }

    public void addHealBoost(float healBoost) {
        this.healMultiplier += healBoost / 100f;
    }

    public static void increaseKBRes(WarlordsPlayer warlordsPlayer, float kbRes) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addKBRes(kbRes);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, 0, kbRes, 0, 0, 0, 0));
    }

    public void addKBRes(float kbRes) {
        this.kbMultiplier -= kbRes / 250f; //dividing more than 100 because reducing kb reduces too much
    }

    public static void increaseThorns(WarlordsPlayer warlordsPlayer, float thorns, int maxThornsDamage) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addThorns(thorns, maxThornsDamage);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, 0, 0, thorns, maxThornsDamage, 0, 0));
    }

    public void addThorns(float thorns, int maxThornsDamage) {
        this.thorns += thorns / 100;
        this.maxThornsDamage = Math.max(this.maxThornsDamage, maxThornsDamage);
    }

    public static void increaseCritChance(WarlordsPlayer warlordsPlayer, float additionalCritChance) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addCritChance(additionalCritChance);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, 0, 0, 0, 0, additionalCritChance, 0));
    }

    public void addCritChance(float additionalCritChance) {
        this.additionalCritChance += additionalCritChance;
    }

    public static void increaseCritMultiplier(WarlordsPlayer warlordsPlayer, float additionalCritMultiplier) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown damageHealCooldown) {
                damageHealCooldown.addCritMultiplier(additionalCritMultiplier);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, 0, 0, 0, 0, 0, additionalCritMultiplier));
    }

    public void addCritMultiplier(float additionalCritMultiplier) {
        this.additionalCritMultiplier += additionalCritMultiplier;
    }

    private float damageMultiplier = 1;
    private float healMultiplier = 1;
    private float kbMultiplier = 1;
    private float thorns = 0;
    private int maxThornsDamage = 0;
    private float additionalCritChance = 0;
    private float additionalCritMultiplier = 0;


    public ItemAdditiveCooldown(
            WarlordsEntity from,
            float damageBoost,
            float healBoost,
            float kbRes,
            float thorns,
            int maxThornsDamage,
            float additionalCritChance,
            float additionalCritMultiplier
    ) {
        super(
                "Item Additive",
                null,
                AbstractItem.class,
                null,
                from,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false
        );
        addDamageBoost(damageBoost);
        addHealBoost(healBoost);
        addKBRes(kbRes);
        addThorns(thorns, maxThornsDamage);
        addCritChance(additionalCritChance);
        addCritMultiplier(additionalCritMultiplier);
    }

    @Override
    public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
        return currentCritChance + additionalCritChance;
    }

    @Override
    public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
        return currentCritMultiplier + additionalCritMultiplier;
    }

    @Override
    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue * damageMultiplier;
    }

    @Override
    public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
        // prevent recursion
        if (Objects.equals(event.getAttacker(), from) || event.getFlags().contains(InstanceFlags.THORNS)) {
            return;
        }
        if (thorns <= 0) {
            return;
        }
        float thornsDamage = currentDamageValue * thorns;
        if (thornsDamage > maxThornsDamage) {
            thornsDamage = maxThornsDamage;
        }
        event.getAttacker().addDamageInstance(from, "Thorns", thornsDamage, thornsDamage, 0, 100, EnumSet.of(InstanceFlags.THORNS));
    }

    @Override
    public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue * healMultiplier;
    }

    @Override
    public void multiplyKB(Vector currentVector) {
        currentVector.multiply(kbMultiplier);
    }
}
