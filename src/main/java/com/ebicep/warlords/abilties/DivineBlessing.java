package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DivineBlessing extends AbstractAbility implements Duration {

    private int hexTickDurationIncrease = 40;
    private int hexHealingBonus = 50;
    private int lethalDamageHealing = 15;
    private int postHealthTickDelay = 40;
    private int postHealthHealAmount = 800;
    private int tickDuration = 240;


    public DivineBlessing() {
        super("Divine Blessing", 0, 0, 50, 10, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Imbue yourself with Holy Energy, increasing Merciful Hex duration by ")
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and causing Ray of Light to not consume Merciful Hex stacks. Allies with max stacks of Merciful Hex receive "))
                               .append(Component.text(hexHealingBonus + "%", NamedTextColor.GREEN))
                               .append(Component.text(" more healing from all sources and heal for "))
                               .append(Component.text(lethalDamageHealing + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of their maximum health when taking lethal damage for the first time. After "))
                               .append(Component.text(format(postHealthTickDelay / 20f), NamedTextColor.GOLD))
                               .append(Component.text("seconds all allies restore "))
                               .append(Component.text(postHealthHealAmount, NamedTextColor.GREEN))
                               .append(Component.text(" health. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "arcanist.divineblessing.activation", 2, 1.25f);
        Utils.playGlobalSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1.5f);

        DivineBlessing tempDivineBlessing = new DivineBlessing();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BLESS",
                DivineBlessing.class,
                tempDivineBlessing,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0 && ticksLeft != 0) {
                        PlayerFilter.playingGame(wp.getGame())
                                    .teammatesOf(wp)
                                    .filter(teammate -> new CooldownFilter<>(teammate, RegularCooldown.class)
                                            .filterCooldownFrom(wp)
                                            .filterCooldownClass(MercifulHex.class)
                                            .stream()
                                            .count() >= MercifulHex.getFromHex(wp).getMaxStacks())
                                    .forEach(teammate -> {
                                        teammate.getCooldownManager().removeCooldownByObject(tempDivineBlessing);
                                        teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                name,
                                                null,
                                                DivineBlessing.class,
                                                tempDivineBlessing,
                                                wp,
                                                CooldownTypes.ABILITY,
                                                cooldownManager -> {
                                                },
                                                21
                                        ) {
                                            @Override
                                            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                                return currentHealValue * (1 + hexHealingBonus / 100f);
                                            }

                                            @Override
                                            public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                                if (teammate.getHealth() - currentDamageValue < 0) {
                                                    float healAmount = teammate.getMaxHealth() * (lethalDamageHealing / 100f);
                                                    teammate.addHealingInstance(
                                                            wp,
                                                            name,
                                                            healAmount,
                                                            healAmount,
                                                            0,
                                                            100,
                                                            false,
                                                            false
                                                    );
                                                }
                                                return currentDamageValue;
                                            }
                                        });
                                    });
                    }
                })
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (cooldown.getFrom().equals(wp) &&
                                cooldown instanceof RegularCooldown<?> regularCooldown &&
                                cooldown.getCooldownObject() instanceof MercifulHex
                        ) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                        }
                    }
                };
            }
        });
        PlayerFilter.playingGame(wp.getGame())
                    .teammatesOf(wp)
                    .forEach(enemy -> {
                        new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownClass(MercifulHex.class)
                                .filterCooldownFrom(wp)
                                .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease));
                    });
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                PlayerFilter.playingGame(wp.getGame())
                            .teammatesOf(wp)
                            .forEach(teammate -> {
                                teammate.addHealingInstance(
                                        wp,
                                        name,
                                        postHealthHealAmount,
                                        postHealthHealAmount,
                                        0,
                                        100,
                                        false,
                                        false
                                );
                            });
            }
        }.runTaskLater(postHealthTickDelay);
        return true;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
