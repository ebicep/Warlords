package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.Aspect;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemAdditiveCooldown extends PermanentCooldown<AbstractItem> {

    public static void giveCooldown(WarlordsEntity warlordsEntity, Consumer<ItemAdditiveCooldown> consumer) {
        new CooldownFilter<>(warlordsEntity, PermanentCooldown.class)
                .filterCooldownName("Item Additive")
                .findAny()
                .ifPresentOrElse(permanentCooldown -> {
                    ItemAdditiveCooldown itemAdditiveCooldown = (ItemAdditiveCooldown) permanentCooldown;
                    consumer.accept(itemAdditiveCooldown);
                }, () -> {
                    ItemAdditiveCooldown itemAdditiveCooldown = new ItemAdditiveCooldown(warlordsEntity);
                    consumer.accept(itemAdditiveCooldown);
                    warlordsEntity.getCooldownManager().addCooldown(itemAdditiveCooldown);
                });
    }

    private final Map<Aspect, AspectModifier> aspectModifiers = new HashMap<>();
    private float damageMultiplier = 1;
    private float healMultiplier = 1;
    private float kbMultiplier = 1;
    private float thorns = 0;
    private int maxThornsDamage = 0;
    private float additionalCritChance = 0;
    private float additionalCritMultiplier = 0;

    public ItemAdditiveCooldown(WarlordsEntity from) {
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
    }

    public void addDamageBoost(float damageBoost) {
        this.damageMultiplier += damageBoost / 100f;
    }

    public void addHealBoost(float healBoost) {
        this.healMultiplier += healBoost / 100f;
    }

    public void addKBRes(float kbRes) {
        this.kbMultiplier -= kbRes / 250f; //dividing more than 100 because reducing kb reduces too much
    }

    public void addThorns(float thorns, int maxThornsDamage) {
        this.thorns += thorns / 100;
        this.maxThornsDamage = Math.max(this.maxThornsDamage, maxThornsDamage);
    }

    public void addCritChance(float additionalCritChance) {
        this.additionalCritChance += additionalCritChance;
    }

    public void addCritMultiplier(float additionalCritMultiplier) {
        this.additionalCritMultiplier += additionalCritMultiplier;
    }

    public void addAspectModifier(Aspect aspect, AspectModifier aspectModifier) {
        aspectModifiers.put(aspect, aspectModifier);
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
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
            Aspect aspect = warlordsNPC.getMob().getAspect();
            if (aspect == null) {
                return currentDamageValue * damageMultiplier;
            }
            AspectModifier aspectModifier = aspectModifiers.get(aspect);
            if (aspectModifier == null) {
                return currentDamageValue * damageMultiplier;
            }
            return currentDamageValue * (damageMultiplier + aspectModifier.damageMultiplier - 1);
        }
        return currentDamageValue * damageMultiplier;
    }

    @Override
    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
        if (event.getSource() instanceof WarlordsNPC warlordsNPC) {
            Aspect aspect = warlordsNPC.getMob().getAspect();
            if (aspect == null) {
                return currentDamageValue;
            }
            AspectModifier aspectModifier = aspectModifiers.get(aspect);
            if (aspectModifier == null) {
                return currentDamageValue;
            }
            return currentDamageValue * (aspectModifier.damageReductionMultiplier);
        }
        return currentDamageValue;
    }

    @Override
    public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
        // prevent recursion
        WarlordsEntity attacker = event.getSource();
        if (Objects.equals(attacker, from) || event.getFlags().contains(InstanceFlags.RECURSIVE)) {
            return;
        }
        if (thorns <= 0) {
            return;
        }
        float thornsDamage = currentDamageValue * thorns;
        if (thornsDamage > maxThornsDamage) {
            thornsDamage = maxThornsDamage;
        }
        attacker.addDamageInstance(from, "Thorns", thornsDamage, thornsDamage, 0, 100, EnumSet.of(InstanceFlags.RECURSIVE, InstanceFlags.IGNORE_DAMAGE_BOOST));
    }

    @Override
    public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
            Aspect aspect = warlordsNPC.getMob().getAspect();
            if (aspect == null) {
                return;
            }
            AspectModifier aspectModifier = aspectModifiers.get(aspect);
            if (aspectModifier == null) {
                return;
            }
            Aspect.AspectNegationCooldown.giveAspectNegationCooldown(from, warlordsNPC, aspectModifier.effectNegationTicks);
        }
    }

    @Override
    public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue * healMultiplier;
    }

    @Override
    public void multiplyKB(Vector currentVector) {
        currentVector.multiply(kbMultiplier);
    }

    /**
     * @param damageMultiplier          1.2 = 20% more damage
     * @param effectNegationTicks       20 = 1 second
     * @param damageReductionMultiplier 0.8 = 20% less damage
     */
    public record AspectModifier(float damageMultiplier, int effectNegationTicks, float damageReductionMultiplier) {
    }
}
