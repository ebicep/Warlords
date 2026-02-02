package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.Objects;
import java.util.function.Consumer;

public class NewItemCooldown extends PermanentCooldown<NewItemCooldown> {

    public static void giveCooldown(WarlordsEntity warlordsEntity, Consumer<NewItemCooldown> consumer) {
        new CooldownFilter<>(warlordsEntity, PermanentCooldown.class)
                .filterCooldownName("Item")
                .findAny()
                .ifPresentOrElse(permanentCooldown -> {
                            NewItemCooldown cd = (NewItemCooldown) permanentCooldown;
                            consumer.accept(cd);
                            warlordsEntity.addKnockbackModifier(warlordsEntity, "Item", -cd.kbMultiplier, cd);
                        }, () -> {
                            NewItemCooldown cd = new NewItemCooldown(warlordsEntity);
                            consumer.accept(cd);
                            warlordsEntity.addKnockbackModifier(warlordsEntity, "Item", -cd.kbMultiplier, cd);
                            warlordsEntity.getCooldownManager().addCooldown(cd);
                        }
                );
    }

    private float damageMultiplier = 1;
    private float damageBossMultiplier = 1;
    private float healMultiplier = 1;
    private float kbMultiplier = 0;
    private float thorns = 0;
    private int maxThornsDamage = 0;
    private float additionalCritChance = 0;
    private float additionalCritMultiplier = 0;

    public NewItemCooldown(WarlordsEntity from) {
        super(
                "Item",
                null,
                NewItemCooldown.class,
                null,
                from,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false
        );
        this.addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
                    currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, healMultiplier);
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
                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, damageMultiplier);
                    if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name + " Boss", damageBossMultiplier);
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
    }

    public void addDamageBoost(float damageBoost) {
        this.damageMultiplier += damageBoost / 100f;
    }

    public void addDamageBossBoost(float damageBoost) {
        this.damageBossMultiplier += damageBoost / 100f;
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


}
