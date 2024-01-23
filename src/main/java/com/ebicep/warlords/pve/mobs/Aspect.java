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
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
                    "Aspect - Infernal",
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
                    if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
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
                                    healthDamage = DamageCheck.clamp(healthDamage);
                                    receiver.addDamageInstance(
                                            warlordsEntity,
                                            "Burn",
                                            healthDamage,
                                            healthDamage,
                                            0,
                                            100,
                                            EnumSet.of(InstanceFlags.RECURSIVE)
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
            AtomicReference<FloatModifiable.FloatModifier> modifier = new AtomicReference<>(warlordsEntity.getHealth().addAdditiveModifier(name + " (Base)", additionalHealth));
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
                                modifier.get().forceEnd();
                            }
                        } else if (!hasEffect.get()) {
                            hasEffect.set(true);
                            modifier.set(warlordsEntity.getHealth().addAdditiveModifier(name + " (Base)", additionalHealth));
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
    private static final Map<Pair<Aspect, Aspect>, String> NAMING = new HashMap<>() {{
        put(new Pair<>(ARMOURED, null), "Shattering");
        put(new Pair<>(CHILLING, null), "Heated");
        put(new Pair<>(EVASIVE, null), "Insightful");
        put(new Pair<>(INFERNAL, null), "Divine");
        put(new Pair<>(JUGGERNAUT, null), "Indomitable");
        put(new Pair<>(REGENERATIVE, null), "Stagnant");
        put(new Pair<>(SWIFT, null), "Paralyzing");
        put(new Pair<>(VAMPIRIC, null), "Coagulated");
        put(new Pair<>(ARMOURED, CHILLING), "Fiery");
        put(new Pair<>(ARMOURED, EVASIVE), "Precise");
        put(new Pair<>(ARMOURED, INFERNAL), "Judgmental");
        put(new Pair<>(ARMOURED, JUGGERNAUT), "Implosive");
        put(new Pair<>(ARMOURED, REGENERATIVE), "Lifeless");
        put(new Pair<>(ARMOURED, SWIFT), "Inebriating");
        put(new Pair<>(ARMOURED, VAMPIRIC), "Curdled");
        put(new Pair<>(CHILLING, EVASIVE), "Heat-Seeking");
        put(new Pair<>(CHILLING, INFERNAL), "Jolly");
        put(new Pair<>(CHILLING, JUGGERNAUT), "Nuclear");
        put(new Pair<>(CHILLING, REGENERATIVE), "Fusing");
        put(new Pair<>(CHILLING, SWIFT), "Exhausted");
        put(new Pair<>(CHILLING, VAMPIRIC), "Saharan");
        put(new Pair<>(EVASIVE, INFERNAL), "Angry");
        put(new Pair<>(EVASIVE, JUGGERNAUT), "Blatant");
        put(new Pair<>(EVASIVE, REGENERATIVE), "Murderous");
        put(new Pair<>(EVASIVE, SWIFT), "Lazy");
        put(new Pair<>(EVASIVE, VAMPIRIC), "Concentrating");
        put(new Pair<>(INFERNAL, JUGGERNAUT), "Veiled");
        put(new Pair<>(INFERNAL, REGENERATIVE), "Fiery");
        put(new Pair<>(INFERNAL, SWIFT), "Heavy");
        put(new Pair<>(INFERNAL, VAMPIRIC), "Religious");
        put(new Pair<>(JUGGERNAUT, REGENERATIVE), "Obliterating");
        put(new Pair<>(JUGGERNAUT, SWIFT), "Sticky");
        put(new Pair<>(JUGGERNAUT, VAMPIRIC), "Gelatinous");
        put(new Pair<>(REGENERATIVE, SWIFT), "Acidic");
        put(new Pair<>(REGENERATIVE, VAMPIRIC), "Lethal");
        put(new Pair<>(SWIFT, VAMPIRIC), "Pillared");
    }};

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

    @Nonnull
    public static String getAspectName(@Nullable Aspect aspect1, @Nullable Aspect aspect2) {
        Pair<Aspect, Aspect> aspectAspectPair1 = new Pair<>(aspect1, aspect2);
        Pair<Aspect, Aspect> aspectAspectPair2 = new Pair<>(aspect2, aspect1);
        return NAMING.getOrDefault(aspectAspectPair1, NAMING.getOrDefault(aspectAspectPair2, ""));
    }

    public final String name;
    public final TextColor textColor;

    Aspect(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public void apply(WarlordsEntity warlordsEntity) {

    }

    public Aspect next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
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
