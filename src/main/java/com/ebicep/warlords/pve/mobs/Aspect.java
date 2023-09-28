package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public enum Aspect {

    ARMOURED("Armoured", TextColor.color(121, 121, 121)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Armoured",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {

                    }
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (Aspect.isNegated(warlordsEntity)) {
                        return currentDamageValue;
                    }
                    return currentDamageValue * .8f;
                }
            });
        }
    },
    CHILLING("Chilling", TextColor.color(68, 204, 204)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Chilling",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {

                    }
            ) {
                @Override
                public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (Aspect.isNegated(warlordsEntity)) {
                        return;
                    }
                    warlordsEntity.getSpeed().addSpeedModifier(warlordsEntity, "Chilling", -20, 40);
                }
            });
        }
    },
    EVASIVE("Evasive", TextColor.color(242, 242, 242)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Evasive",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {

                    }
            ) {

                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onDamageHeal(WarlordsDamageHealingEvent event) {
                            if (Aspect.isNegated(warlordsEntity)) {
                                return;
                            }
                            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                                return;
                            }
                            if (event.isHealingInstance()) {
                                return;
                            }
                            if (ThreadLocalRandom.current().nextDouble() < .05) {
                                event.getAttacker().sendMessage(warlordsEntity.getColoredName().append(Component.text(" dodged your attack.", NamedTextColor.GRAY)));
                                event.setCancelled(true);
                            }
                        }
                    };
                }
            });
        }
    },
    INFERNAL("Infernal", TextColor.color(255, 121, 121)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Armoured",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {

                    }
            ) {
                @Override
                public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (Aspect.isNegated(warlordsEntity)) {
                        return;
                    }
                    WarlordsEntity receiver = event.getWarlordsEntity();
                    receiver.getCooldownManager().removeCooldownByName("Aspect - Burn");
                    receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Aspect - Burn",
                            "BRN",
                            Aspect.class,
                            null,
                            warlordsEntity,
                            CooldownTypes.DEBUFF,
                            cooldownManager -> {
                            },
                            40,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksLeft % 20 == 0) {
                                    if (Aspect.isNegated(warlordsEntity)) {
                                        return;
                                    }
                                    float healthDamage = receiver.getMaxHealth() * 0.005f;
                                    if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                        healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                    }
                                    if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                        healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                    }
                                    receiver.addDamageInstance(
                                            warlordsEntity,
                                            "Burn",
                                            healthDamage,
                                            healthDamage,
                                            0,
                                            100
                                    );
                                }
                            })
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            if (Aspect.isNegated(warlordsEntity)) {
                                return currentDamageValue;
                            }
                            return currentDamageValue * 1.2f;
                        }
                    });
                }
            });
        }
    },
    JUGGERNAUT("Juggernaut", TextColor.color(255, 242, 0)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            float additionalHealth = warlordsEntity.getMaxBaseHealth() * .2f;
            warlordsEntity.setMaxBaseHealth(warlordsEntity.getMaxHealth() + additionalHealth);
            warlordsEntity.heal();
            AtomicBoolean hasEffect = new AtomicBoolean(true);
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Juggernaut",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (Aspect.isNegated(warlordsEntity)) {
                            if (hasEffect.get()) {
                                hasEffect.set(false);
                                warlordsEntity.setMaxBaseHealth(warlordsEntity.getMaxBaseHealth() - additionalHealth);
                            }
                        } else if (!hasEffect.get()) {
                            hasEffect.set(true);
                            warlordsEntity.setMaxBaseHealth(warlordsEntity.getMaxBaseHealth() + additionalHealth);
                        }
                    }
            ));
        }
    },
    REGENERATIVE("Regenerative", TextColor.color(121, 255, 121)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Regenerative",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            if (Aspect.isNegated(warlordsEntity)) {
                                return;
                            }
                            warlordsEntity.addHealingInstance(
                                    warlordsEntity,
                                    "Regenerative",
                                    300,
                                    300,
                                    0,
                                    100
                            );
                        }
                    }
            ));
        }
    },
    SWIFT("Swift", TextColor.color(121, 121, 255)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            CalculateSpeed.Modifier modifier = new CalculateSpeed.Modifier(warlordsEntity, "Swift", 20, 400000, Collections.emptyList(), false);
            CalculateSpeed calculateSpeed = warlordsEntity.getSpeed();
            calculateSpeed.addSpeedModifier(modifier);
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Armoured",
                    null,
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (Aspect.isNegated(warlordsEntity)) {
                            modifier.setModifier(0);
                            calculateSpeed.setChanged(true);
                        } else if (modifier.modifier != 20) {
                            modifier.setModifier(20);
                            calculateSpeed.setChanged(true);
                        }
                    }
            ));
        }
    },
    VAMPIRIC("Vampiric", TextColor.color(242, 0, 0)) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Vampiric",
                    "VAMP",
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 3 == 0) {
                            if (Aspect.isNegated(warlordsEntity)) {
                                return;
                            }
                            EffectUtils.displayParticle(
                                    Particle.REDSTONE,
                                    warlordsEntity.getLocation().add(
                                            0,
                                            1.2,
                                            0
                                    ),
                                    1,
                                    .5,
                                    .5,
                                    .5,
                                    0,
                                    new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                            );
                        }
                    }
            ) {
                @Override
                public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (Aspect.isNegated(warlordsEntity)) {
                        return;
                    }
                    WarlordsEntity attacker = event.getAttacker();
                    float healAmount = currentDamageValue * .2f;
                    attacker.addHealingInstance(
                            attacker,
                            name,
                            healAmount,
                            healAmount,
                            0,
                            100
                    );
                }
            });
        }
    },

    ;

    public static final Aspect[] VALUES = values();

    private static boolean isNegated(WarlordsEntity entity) {
        return entity.getCooldownManager().hasCooldown(AspectNegationCooldown.class);
    }

    @Nullable
    public static Aspect getRandomAspect(List<Aspect> excluding) {
        List<Aspect> aspects = Arrays.stream(VALUES)
                                     .filter(aspect -> !excluding.contains(aspect))
                                     .collect(Collectors.toList());
        Collections.shuffle(aspects);
        if (aspects.isEmpty()) {
            return null;
        }
        return aspects.get(0);
    }

    public final String name;
    public final TextColor textColor;

    Aspect(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public void apply(WarlordsEntity warlordsEntity) {

    }

    public static class AspectNegationCooldown extends RegularCooldown<AspectNegationCooldown> {

        public static void giveAspectNegationCooldown(WarlordsEntity from, WarlordsEntity to, int ticks) {
            new CooldownFilter<>(to, RegularCooldown.class)
                    .filterCooldownClass(AspectNegationCooldown.class)
                    .filterCooldownFrom(from)
                    .findAny()
                    .ifPresentOrElse(
                            regularCooldown -> regularCooldown.setTicksLeft(Math.min(regularCooldown.getTicksLeft() + ticks, ticks * 2)),
                            () -> to.getCooldownManager().addCooldown(new AspectNegationCooldown(from, ticks))
                    );
        }

        public AspectNegationCooldown(WarlordsEntity from, int ticksLeft) {
            super(
                    "Aspect Negation",
                    null,
                    AspectNegationCooldown.class,
                    null,
                    from,
                    CooldownTypes.ASPECT,
                    cooldownManager -> {
                    },
                    ticksLeft
            );
        }

    }

}
