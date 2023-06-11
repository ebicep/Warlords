package com.ebicep.warlords;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergySeer extends AbstractAbility implements Duration {

    private int tickDuration = 120;
    private int energyRestore = 80;
    private int damageIncrease = 10;
    private int damageTickDuration = 100;

    public EnergySeer() {
        super("Energy Seer", 0, 0, 24, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Heal for 5 times the energy expended for the next ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If you healed for 5 instances, restore energy "))
                               .append(Component.text(energyRestore, NamedTextColor.YELLOW))
                               .append(Component.text(" and increase your damage by "))
                               .append(Component.text(damageIncrease + "%", NamedTextColor.RED))
                               .append(Component.text(" for "))
                               .append(Component.text(format(damageTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds after Energy Seer ends."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        AtomicInteger timesHealed = new AtomicInteger();
        Listener listener = new Listener() {
            @EventHandler
            public void onEnergyUsed(WarlordsEnergyUsedEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), wp)) {
                    return;
                }
                float healAmount = event.getEnergyUsed() * 5;
                wp.addHealingInstance(wp, name, healAmount, healAmount, 0, 100, false, false);
                timesHealed.getAndIncrement();
            }
        };
        wp.getGame().registerEvents(listener);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ESEER",
                EnergySeer.class,
                new EnergySeer(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    HandlerList.unregisterAll(listener);
                    //TODO maybe add tick delay
                    if (timesHealed.get() > 5) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                name,
                                "ESEER",
                                EnergySeer.class,
                                new EnergySeer(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {

                                },
                                damageTickDuration
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * 1.1f;
                            }
                        });
                    }
                },
                damageTickDuration
        ));
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
