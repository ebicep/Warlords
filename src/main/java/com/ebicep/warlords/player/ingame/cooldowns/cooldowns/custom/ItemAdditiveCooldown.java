package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.types.ItemTome;
import org.bukkit.util.Vector;

public class ItemAdditiveCooldown extends PermanentCooldown<ItemTome> {

    public static void increaseDamage(WarlordsPlayer warlordsPlayer, int damageBoost) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown) {
                ItemAdditiveCooldown damageHealCooldown = (ItemAdditiveCooldown) cooldown;
                damageHealCooldown.addDamageBoost(damageBoost);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, damageBoost, 0, 0));
    }

    public void addDamageBoost(int damageBoost) {
        this.damageMultiplier += damageBoost / 100f;
    }

    public static void increaseHealing(WarlordsPlayer warlordsPlayer, int healBoost) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown) {
                ItemAdditiveCooldown damageHealCooldown = (ItemAdditiveCooldown) cooldown;
                damageHealCooldown.addHealBoost(healBoost);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, healBoost, 0));
    }

    public void addHealBoost(int healBoost) {
        this.healMultiplier += healBoost / 100f;
    }

    public static void increaseKBRes(WarlordsPlayer warlordsPlayer, int kbRes) {
        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
            if (cooldown instanceof ItemAdditiveCooldown) {
                ItemAdditiveCooldown damageHealCooldown = (ItemAdditiveCooldown) cooldown;
                damageHealCooldown.addKBRes(kbRes);
                return;
            }
        }
        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveCooldown(warlordsPlayer, 0, 0, kbRes));
    }

    public void addKBRes(int kbRes) {
        this.kbMultiplier -= kbRes / 200f; //200 because kb is halved since it reduces too much
    }

    private float damageMultiplier = 1;
    private float healMultiplier = 1;
    private float kbMultiplier = 1;

    public ItemAdditiveCooldown(WarlordsEntity from, int damageBoost, int healBoost, int kbRes) {
        super(
                "Item Tome Damage",
                null,
                ItemTome.class,
                null,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                false
        );
        addDamageBoost(damageBoost);
        addHealBoost(healBoost);
        addKBRes(kbRes);
    }

    @Override
    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue * damageMultiplier;
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
