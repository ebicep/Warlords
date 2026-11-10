package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Decay extends BaseSet {

    private static final RandomCollection<String> DEBUFFS = new RandomCollection<String>()
            .add(25, "Wound")
            .add(15, "Burn")
            .add(15, "Bleed")
            .add(10, "Leech")
            .add(5, "Silence")
            .add(5, "Stun");

    private int activationChance;
    private float duration;

    @Override
    public void init() {
        super.init();
        this.activationChance = getValue("activationChance", int.class);
        this.duration = getValue("duration", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "decay";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(activationChance, duration);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Decay.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                    tryApply(warlordsPlayer, event.getSource(), event);
                }
            }).addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                if (!event.getSource().equals(warlordsPlayer)) {
                    return;
                }
                if (event.getFlags().contains(InstanceFlags.DOT)) {
                    return;
                }
                tryApply(warlordsPlayer, event.getWarlordsEntity(), event);
            }));
        }

    }

    private void tryApply(WarlordsPlayer source, WarlordsEntity target, WarlordsDamageHealingEvent event) {
        if (target == null || target.isTeammate(source) ||
                event.isHealingInstance() ||
                event.getFlags().contains(InstanceFlags.DOT) ||
                ThreadLocalRandom.current().nextDouble() > activationChance / 100.0) {
            return;
        }
        String debuff = DEBUFFS.next();
        if (debuff == null) {
            return;
        }
        int ticks = Math.round(duration * 20);
        switch (debuff) {
            case "Wound" -> WoundingCooldown.addWoundingCooldown(target, getName() + " - Wound", source, 25, ticks);
            case "Burn" -> applyBurn(source, target, ticks);
            case "Bleed" -> applyBleed(source, target, ticks);
            case "Leech" -> applyLeech(source, target, ticks);
            case "Silence" -> applySilence(source, target, ticks);
            case "Stun" -> target.setStunTicks(ticks);
        }
        source.sendMessage(Component.text("Your Decay applied the ", NamedTextColor.GREEN)
                .append(Component.text(debuff, NamedTextColor.RED))
                .append(Component.text(" debuff to "))
                .append(target.getColoredName())
                .append(Component.text("."))
        );
    }

    private void applyBurn(WarlordsPlayer source, WarlordsEntity target, int ticks) {
        target.getCooldownManager().removeCooldownByName(getName() + " - Burn");
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                getName() + " - Burn",
                "BRN",
                Decay.class,
                null,
                source,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                ticks,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                        float damage = DamageCheck.clamp(target.getMaxHealth() * .005f);
                        target.addInstance(InstanceBuilder
                                .damage()
                                .cause(getName() + " - Burn")
                                .source(source)
                                .value(damage)
                                .flags(
                                        InstanceFlags.DOT,
                                        InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST,
                                        InstanceFlags.NO_HEALING_ORBS
                                )
                        );
                    }
                })
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) ->
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName() + " - Burn",
                        1.15f
                )
        ));
    }

    private void applyBleed(WarlordsPlayer source, WarlordsEntity target, int ticks) {
        target.getCooldownManager().removeCooldownByName(getName() + " - Bleed");
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                getName() + " - Bleed",
                "BLEED",
                Decay.class,
                null,
                source,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                ticks,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                        float damage = DamageCheck.clamp(target.getMaxHealth() * .005f);
                        target.addInstance(InstanceBuilder
                                .damage()
                                .cause(getName() + " - Bleed")
                                .source(source)
                                .value(damage)
                                .flags(
                                        InstanceFlags.DOT,
                                        InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST,
                                        InstanceFlags.NO_HEALING_ORBS
                                )
                        );
                    }
                })
        ).addModifier(Modifier.MODIFY_INCOMING_HEALING, (event, currentHealValue) ->
                currentHealValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName() + " - Bleed",
                        .2f
                )
        ));
    }

    private void applyLeech(WarlordsPlayer source, WarlordsEntity target, int ticks) {
        target.getCooldownManager().removeCooldownByName(getName() + " - Leech");
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                getName() + " - Leech",
                "LCH",
                Decay.class,
                null,
                source,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                ticks
        ).addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
            WarlordsEntity attacker = event.getSource();
            if (attacker != null && attacker != target) {
                attacker.addInstance(InstanceBuilder
                        .healing()
                        .cause(getName() + " - Leech")
                        .source(attacker)
                        .value(Math.min(300, currentDamageValue * .25f))
                );
            }
        }));
    }

    private void applySilence(WarlordsPlayer source, WarlordsEntity target, int ticks) {
        target.getCooldownManager().removeCooldownByName(getName() + " - Silence");
        target.getCooldownManager().addRegularCooldown(
                getName() + " - Silence",
                "SILENCE",
                SoulShackle.class,
                null,
                source,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                ticks,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed == 0) {
                        target.getEntity().showTitle(Title.title(
                                Component.empty(),
                                Component.text("SILENCED", NamedTextColor.RED),
                                Title.Times.times(Ticks.duration(0), Ticks.duration(ticks), Ticks.duration(0))
                        ));
                    }
                    if (ticksElapsed % 10 == 0) {
                        Utils.playGlobalSound(target.getLocation(), Sound.BLOCK_SAND_BREAK, 2, 2);
                        Location location = target.getLocation();
                        EffectUtils.playCylinderAnimation(location, 1, 25, 25, 25, 6, 7, .3);
                    }
                })
        );
    }

}
