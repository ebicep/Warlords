package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public enum Aspect {

    ARMOURED("Armoured", NamedTextColor.DARK_GRAY) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getSpec().setDamageResistance(warlordsEntity.getSpec().getDamageResistance() + 20);
        }
    },
    CHILLING("Chilling", NamedTextColor.AQUA) {
        @Override
        public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
            receiver.getSpeed().addSpeedModifier(attacker, "Chilling", -20, 40, "BASE");
        }
    },
    EVASIVE("Evasive", NamedTextColor.GRAY) {
        @Override
        public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
            if (!event.getWarlordsEntity().equals(self)) {
                return;
            }
            if (event.isHealingInstance()) {
                return;
            }
            if (ThreadLocalRandom.current().nextDouble() < .05) {
                event.getAttacker().sendMessage(self.getColoredName().append(Component.text(" dodged your attack.", NamedTextColor.GRAY)));
                event.setCancelled(true);
            }
        }
    }, //TODO lighter gray?
    INFERNAL("Infernal", NamedTextColor.RED) {
        @Override
        public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
            receiver.getCooldownManager().removeCooldownByName("Aspect - Burn");
            receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Aspect - Burn",
                    "BRN",
                    Aspect.class,
                    null,
                    attacker,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    40,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksLeft % 20 == 0) {
                            float healthDamage = receiver.getMaxHealth() * 0.005f;
                            if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                healthDamage = DamageCheck.MINIMUM_DAMAGE;
                            }
                            if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                            }
                            receiver.addDamageInstance(
                                    attacker,
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
                    return currentDamageValue * 1.2f;
                }
            });
        }
    },
    JUGGERNAUT("Juggernaut", NamedTextColor.GOLD) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.setMaxBaseHealth(warlordsEntity.getMaxBaseHealth() * 1.2f);
            warlordsEntity.heal();
        }
    },
    REGENERATIVE("Regenerative", NamedTextColor.GREEN) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Aspect - Regenerative",
                    "REGEN",
                    Aspect.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
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
    SWIFT("Swift", NamedTextColor.BLUE) {
        @Override
        public void apply(WarlordsEntity warlordsEntity) {
            warlordsEntity.getSpeed().addSpeedModifier(warlordsEntity, "Swift", 20, 40, "BASE");
        }
    },
    VAMPIRIC("Vampiric", NamedTextColor.DARK_RED) {
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
                            EffectUtils.displayParticle(
                                    Particle.REDSTONE,
                                    warlordsEntity.getLocation().add(
                                            (Math.random() - 0.5) * 1,
                                            1.2,
                                            (Math.random() - 0.5) * 1
                                    ),
                                    1,
                                    0,
                                    0,
                                    0,
                                    0,
                                    new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                            );
                        }
                    }
            ) {
                @Override
                public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
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

    public final String name;
    public final TextColor textColor;

    Aspect(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public void apply(WarlordsEntity warlordsEntity) {

    }

    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {

    }

}
