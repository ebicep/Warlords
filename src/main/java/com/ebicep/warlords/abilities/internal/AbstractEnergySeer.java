package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEnergySeer<T> extends AbstractAbility implements PurpleAbilityIcon, Duration {

    protected int speedBuff = 30;
    protected float healingMultiplier = 4;
    protected int tickDuration = 100;
    protected int energyRestore = 80;
    protected int bonusDuration = 100;

    public AbstractEnergySeer() {
        super("Energy Seer", 0, 0, 28, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Gain ")
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" speed and heal for "))
                               .append(Component.text(format(healingMultiplier * 100) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the energy expended for the next "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If you healed for 4 instances, restore energy "))
                               .append(Component.text(energyRestore, NamedTextColor.YELLOW))
                               .append(Component.text(" and "))
                               .append(getBonus())
                               .append(Component.text(" for "))
                               .append(Component.text(format(bonusDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds after Energy Seer ends."));
    }

    public abstract Component getBonus();

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.energyseer.activation", 2, 0.9f);
        wp.addSpeedModifier(wp, name, 30, tickDuration);
        AtomicInteger timesHealed = new AtomicInteger();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SEER",
                getEnergySeerClass(),
                getObject(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (timesHealed.get() >= 4 && wp.isAlive()) {
                        wp.addEnergy(wp, name, energyRestore);
                        wp.getCooldownManager().addCooldown(getBonusCooldown(wp));
                    }
                },
                bonusDuration
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onEnergyUsed(WarlordsEnergyUsedEvent event) {
                        if (!Objects.equals(event.getWarlordsEntity(), wp)) {
                            return;
                        }
                        float energyUsed = event.getEnergyUsed();
                        if (energyUsed <= 0) {
                            return;
                        }
                        float healAmount = energyUsed * healingMultiplier;
                        wp.addHealingInstance(
                                wp,
                                name,
                                healAmount,
                                healAmount,
                                0,
                                100
                        );
                        timesHealed.getAndIncrement();
                    }
                };
            }
        });
        return true;
    }

    public abstract Class<T> getEnergySeerClass();

    public abstract T getObject();

    public abstract RegularCooldown<T> getBonusCooldown(@Nonnull WarlordsEntity wp);

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }

    public float getHealingMultiplier() {
        return healingMultiplier;
    }

    public void setHealingMultiplier(float healingMultiplier) {
        this.healingMultiplier = healingMultiplier;
    }

    public int getBonusDuration() {
        return bonusDuration;
    }

    public void setBonusDuration(int bonusDuration) {
        this.bonusDuration = bonusDuration;
    }
}
