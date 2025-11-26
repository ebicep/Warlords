package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemAdditiveCooldown extends PermanentCooldown<AbstractItem> {

    public static void giveCooldown(WarlordsEntity warlordsEntity, Consumer<ItemAdditiveCooldown> consumer) {
        new CooldownFilter<>(warlordsEntity, PermanentCooldown.class)
                .filterCooldownName("Item Additive")
                .findAny()
                .ifPresentOrElse(permanentCooldown -> {
                            ItemAdditiveCooldown itemAdditiveCooldown = (ItemAdditiveCooldown) permanentCooldown;
                            consumer.accept(itemAdditiveCooldown);
                            warlordsEntity.addKnockbackModifier(warlordsEntity, "Item Additive", -itemAdditiveCooldown.kbMultiplier, itemAdditiveCooldown);
                        }, () -> {
                            ItemAdditiveCooldown itemAdditiveCooldown = new ItemAdditiveCooldown(warlordsEntity);
                            consumer.accept(itemAdditiveCooldown);
                            warlordsEntity.addKnockbackModifier(warlordsEntity, "Item Additive", -itemAdditiveCooldown.kbMultiplier, itemAdditiveCooldown);
                            warlordsEntity.getCooldownManager().addCooldown(itemAdditiveCooldown);
                        }
                );
    }

    private final Map<Aspect, AspectModifier> aspectModifiers = new HashMap<>();
    private float damageMultiplier = 1;
    private float healMultiplier = 1;
    private float kbMultiplier = 0;
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
        this.addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, healMultiplier);
                }
        );
        this.addModifier(Modifier.MODIFY_OUTGOING_CRIT_CHANCE, (event, currentCritChance) -> {
            currentCritChance.addModifier(FloatModifiable.ModifierType.ADDITIVE, name, additionalCritChance);
                }
        );
        this.addModifier(Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER, (event, currentCritMultiplier) -> {
            currentCritMultiplier.addModifier(FloatModifiable.ModifierType.ADDITIVE, name, additionalCritMultiplier);
                }
        );
        this.addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
                        Aspect aspect = warlordsNPC.getMob().getAspect();
                        if (aspect == null) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, damageMultiplier);
                            return;
                        }
                        AspectModifier aspectModifier = aspectModifiers.get(aspect);
                        if (aspectModifier == null) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, damageMultiplier);
                            return;
                        }
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, (damageMultiplier + aspectModifier.damageMultiplier - 1));
                    } else {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, damageMultiplier);
                    }
                }
        );
        this.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (event.getSource() instanceof WarlordsNPC warlordsNPC) {
                        Aspect aspect = warlordsNPC.getMob().getAspect();
                        if (aspect == null) {
                            return;
                        }
                        AspectModifier aspectModifier = aspectModifiers.get(aspect);
                        if (aspectModifier == null) {
                            return;
                        }
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, (aspectModifier.damageReductionMultiplier));
                    }
                }
        );
        this.addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
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
                    attacker.addInstance(InstanceBuilder
                            .damage()
                            .cause("Thorns")
                            .source(from)
                            .value(thornsDamage)
                            .flags(InstanceFlags.RECURSIVE, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                    );
                }
        );
        this.addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
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
        );
    }

    public void addDamageBoost(float damageBoost) {
        this.damageMultiplier += damageBoost / 100f;
    }

    public void addHealBoost(float healBoost) {
        this.healMultiplier += healBoost / 100f;
    }

    public void addKBRes(float kbRes) {
        this.kbMultiplier += kbRes / 2; //dividing more than 100 because reducing kb reduces too much
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

    /**
     * @param damageMultiplier          1.2 = 20% more damage
     * @param effectNegationTicks       20 = 1 second
     * @param damageReductionMultiplier 0.8 = 20% less damage
     */
    public record AspectModifier(float damageMultiplier, int effectNegationTicks, float damageReductionMultiplier) {
    }

}
