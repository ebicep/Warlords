package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class DisasterFragment extends AbstractFixedItem implements FixedItemAppliesToPlayer {

    public static final HashMap<BasicStatPool, Float> STAT_POOL = new HashMap<>() {{
        put(BasicStatPool.MAX_ENERGY, 20f);
        put(BasicStatPool.EPH, 1.5f);
        put(BasicStatPool.SPEED, 60f);
        put(BasicStatPool.CRIT_MULTI, 100f);
    }};
    private static final RandomCollection<String> DEBUFFS = new RandomCollection<String>()
            .add(25, "Wound")
            .add(15, "Burn")
            .add(15, "Bleed")
            .add(10, "Leech")
            .add(5, "Silence")
            .add(5, "Stun");


    public DisasterFragment() {
        super(ItemTier.DELTA);
    }

    @Override
    protected ItemStack getItemStack() {
        return new ItemStack(Material.AMETHYST_SHARD);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                WarlordsEntity victim = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getSource();
                if (!Objects.equals(attacker, warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (!event.getCause().contains("Strike")) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > .2) {
                    return;
                }
                String debuff = DEBUFFS.next();
                attacker.sendMessage(Component.text("Your Disaster Fragment applied the ", NamedTextColor.GREEN)
                                              .append(Component.text(debuff, NamedTextColor.RED))
                                              .append(Component.text(" debuff to "))
                                              .append(victim.getColoredName())
                                              .append(Component.text("."))
                );
                switch (debuff) {
                    case "Wound" -> {
                        WoundingCooldown.addWoundingCooldown(
                                victim,
                                "Disaster Fragment - Wounding",
                                attacker,
                                25,
                                40
                        );
                    }
                    case "Burn" -> {
                        victim.getCooldownManager().removeCooldownByName("Disaster Fragment - Burn");
                        victim.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Disaster Fragment - Burn",
                                "BRN",
                                DisasterFragment.class,
                                null,
                                attacker,
                                CooldownTypes.LOW_LEVEL_DEBUFF,
                                cooldownManager -> {
                                },
                                40,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksLeft % 20 == 0) {
                                        float healthDamage = victim.getMaxHealth() * 0.005f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        victim.addInstance(InstanceBuilder
                                                .damage()
                                                .cause("Burn")
                                                .source(attacker)
                                                .value(healthDamage)
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
                    case "Bleed" -> {
                        victim.getCooldownManager().removeCooldownByName("Disaster Fragment - Bleed");
                        victim.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Disaster Fragment - Bleed",
                                "BLEED",
                                DisasterFragment.class,
                                null,
                                attacker,
                                CooldownTypes.LOW_LEVEL_DEBUFF,
                                cooldownManager -> {
                                },
                                40,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksLeft % 20 == 0) {
                                        float healthDamage = victim.getMaxHealth() * 0.005f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        victim.addInstance(InstanceBuilder
                                                .damage()
                                                .cause("Bleed")
                                                .source(attacker)
                                                .value(healthDamage)
                                                .flags(InstanceFlags.DOT)
                                        );
                                    }
                                })
                        ) {
                            @Override
                            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                return currentHealValue * .2f;
                            }
                        });
                    }
                    case "Leech" -> {
                        AtomicReference<Float> totalHealingDone = new AtomicReference<>((float) 0);
                        victim.getCooldownManager().removeCooldownByName("Disaster Fragment - Leech");
                        victim.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Disaster Fragment - Leech",
                                "LCH",
                                DisasterFragment.class,
                                null,
                                attacker,
                                CooldownTypes.LOW_LEVEL_DEBUFF,
                                cooldownManager -> {
                                },
                                40
                        ) {
                            @Override
                            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                if (totalHealingDone.get() >= 1000) {
                                    setTicksLeft(0);
                                    return;
                                }
                                float healingMultiplier;
                                if (event.getSource() == attacker) {
                                    healingMultiplier = 15;
                                } else {
                                    healingMultiplier = 25;
                                }
                                float healValue = Math.min(500, currentDamageValue * healingMultiplier);
                                event.getSource().addInstance(InstanceBuilder
                                        .healing()
                                        .cause("Leech")
                                        .source(attacker)
                                        .value(healValue)
                                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                    totalHealingDone.updateAndGet(v -> v + warlordsDamageHealingFinalEvent.getValue());
                                });
                            }
                        });
                    }
                    case "Silence" -> {
                        victim.getCooldownManager().removeCooldownByName("Disaster Fragment - Silence");
                        victim.getCooldownManager().addRegularCooldown(
                                "Disaster Fragment - Silence",
                                "SILENCE",
                                SoulShackle.class,
                                null,
                                attacker,
                                CooldownTypes.LOW_LEVEL_DEBUFF,
                                cooldownManager -> {
                                },
                                40,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed == 0) {
                                        victim.getEntity().showTitle(Title.title(
                                                Component.empty(),
                                                Component.text("SILENCED", NamedTextColor.RED),
                                                Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                                        ));
                                    }
                                    if (ticksElapsed % 10 == 0) {
                                        Utils.playGlobalSound(victim.getLocation(), Sound.BLOCK_SAND_BREAK, 2, 2);

                                        Location playerLoc = victim.getLocation();
                                        EffectUtils.playCylinderAnimation(playerLoc, 1, 25, 25, 25, 6, 7, .3);
                                    }
                                })
                        );
                    }
                    case "Stun" -> {
                        victim.setStunTicks(40);
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Disaster Fragment";
    }

    @Override
    public HashMap<BasicStatPool, Float> getStatPool() {
        return STAT_POOL;
    }

    @Override
    public int getWeight() {
        return 45;
    }

    @Override
    public ItemType getType() {
        return ItemType.GAUNTLET;
    }

    @Override
    public String getEffect() {
        return "Mark of Chaos";
    }

    @Override
    public String getEffectDescription() {
        return """
                Your strikes have 20% chance to give mobs a random debuff below for 2s.
                
                25% Wounding
                15% Burn
                15% Bleed
                10% Leech
                5% Silence
                5% Stun
                """;
    }

}
