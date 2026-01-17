package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.fixeditems.DisasterFragment;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Decay extends BaseSet {

    private int activationChance;
    private float duration;

    private static final RandomCollection<String> DEBUFFS = new RandomCollection<String>()
            .add(25, "Wound")
            .add(15, "Burn")
            .add(15, "Bleed")
            .add(10, "Leech")
            .add(5, "Silence")
            .add(5, "Stun");

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
            ).addModifier(
                    Modifier.ON_INCOMING_DAMAGE,
                    (event, currentDamageValue, isCrit) -> {
                        WarlordsEntity victim = event.getWarlordsEntity();
                        WarlordsEntity attacker = event.getSource();
                        if (!Objects.equals(victim, warlordsPlayer)) {
                            return;
                        }
                        if (event.isHealingInstance()) {
                            return;
                        }
                        if (event.getFlags().contains(InstanceFlags.DOT)) {
                            return;
                        }
                        if (ThreadLocalRandom.current().nextDouble() > activationChance / 100.0) {
                            return;
                        }
                        String debuff = DEBUFFS.next();
                        if (debuff == null) {
                            return;
                        }
                        switch (debuff) {
                            case "Wound" -> {
                                WoundingCooldown.addWoundingCooldown(
                                        victim,
                                        getName() + " - Wound",
                                        attacker,
                                        25,
                                        (int) duration * 20
                                );
                            }
                            case "Burn" -> {
                                attacker.getCooldownManager().removeCooldownByName(getName() + " - Burn");
                                attacker.getCooldownManager().addCooldown(new RegularCooldown<>(
                                        getName() + " - Burn",
                                        "BRN",
                                        Decay.class,
                                        null,
                                        victim,
                                        CooldownTypes.LOW_LEVEL_DEBUFF,
                                        cooldownManager -> {
                                        },
                                        (int) duration * 20,
                                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                            if (ticksLeft % 20 == 0) {
                                                float healthDamage = attacker.getMaxHealth() * 0.005f;
                                                healthDamage = DamageCheck.clamp(healthDamage);
                                                attacker.addInstance(InstanceBuilder
                                                        .damage()
                                                        .cause("Burn")
                                                        .source(victim)
                                                        .value(healthDamage)
                                                        .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                                                );
                                            }
                                        })
                                ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue2) -> {
                                            currentDamageValue2.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,  getName() + " - Burn", 1.15f);
                                }));
                            }
                            case "Bleed" -> {
                                attacker.getCooldownManager().removeCooldownByName(getName() + " - Bleed");
                                attacker.getCooldownManager().addCooldown(new RegularCooldown<>(
                                        getName() + " - Bleed",
                                        "BLEED",
                                        Decay.class,
                                        null,
                                        victim,
                                        CooldownTypes.LOW_LEVEL_DEBUFF,
                                        cooldownManager -> {
                                        },
                                        (int) duration * 20,
                                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                            if (ticksLeft % 20 == 0) {
                                                float healthDamage = attacker.getMaxHealth() * 0.005f;
                                                healthDamage = DamageCheck.clamp(healthDamage);
                                                attacker.addInstance(InstanceBuilder
                                                        .damage()
                                                        .cause("Bleed")
                                                        .source(victim)
                                                        .value(healthDamage)
                                                        .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                                                );
                                            }
                                        })
                                ).addModifier(Modifier.MODIFY_INCOMING_HEALING, (e, currentHealValue) -> {
                                    currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName() + " - Bleed", 0.2f);
                                }));
                            }
                            case "Leech" -> {
                                AtomicReference<Float> totalHealingDone = new AtomicReference<>((float) 0);
                                attacker.getCooldownManager().removeCooldownByName(getName() + " - Leech");
                                attacker.getCooldownManager().addCooldown(new RegularCooldown<>(
                                        getName() + " - Leech",
                                        "LCH",
                                        Decay.class,
                                        null,
                                        attacker,
                                        CooldownTypes.LOW_LEVEL_DEBUFF,
                                        cooldownManager -> {
                                        },
                                        (int) duration * 20
                                ).addModifier(Modifier.ON_INCOMING_DAMAGE, (e, currentDamageValue3, isCrit3) -> {
                                            float healingMultiplier;
                                            if (e.getSource() == attacker) {
                                                healingMultiplier = 15;
                                            } else {
                                                healingMultiplier = 25;
                                            }
                                            float healValue = Math.min(300, currentDamageValue * healingMultiplier);
                                            e.getSource().addInstance(InstanceBuilder
                                                    .healing()
                                                    .cause("Leech")
                                                    .source(victim)
                                                    .value(healValue)
                                            ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                                totalHealingDone.updateAndGet(v -> v + warlordsDamageHealingFinalEvent.getValue());
                                            });
                                }));
                            }
                            case "Silence" -> {
                                attacker.getCooldownManager().removeCooldownByName(getName() + " - Silence");
                                attacker.getCooldownManager().addRegularCooldown(
                                        getName() + " - Silence",
                                        "SILENCE",
                                        SoulShackle.class,
                                        null,
                                        victim,
                                        CooldownTypes.LOW_LEVEL_DEBUFF,
                                        cooldownManager -> {
                                        },
                                        (int) duration * 20,
                                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                            if (ticksElapsed == 0) {
                                                attacker.getEntity().showTitle(Title.title(
                                                        Component.empty(),
                                                        Component.text("SILENCED", NamedTextColor.RED),
                                                        Title.Times.times(Ticks.duration(0), Ticks.duration((long) duration), Ticks.duration(0))
                                                ));
                                            }
                                            if (ticksElapsed % 10 == 0) {
                                                Utils.playGlobalSound(attacker.getLocation(), Sound.BLOCK_SAND_BREAK, 2, 2);

                                                Location playerLoc = attacker.getLocation();
                                                EffectUtils.playCylinderAnimation(playerLoc, 1, 25, 25, 25, 6, 7, .3);
                                            }
                                        })
                                );
                            }
                            case "Stun" -> {
                                attacker.setStunTicks((int) duration * 20);
                            }
                        }
                    }
            ));
        }
    }
}
