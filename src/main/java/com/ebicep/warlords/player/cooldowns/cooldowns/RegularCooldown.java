package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * This type of cooldown is used for any regular cooldowns that are removed when its timer reaches 0
 */
public class RegularCooldown<T> extends AbstractCooldown<T> {

    protected int startingTicks;
    protected int ticksLeft;

    public RegularCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int ticksLeft) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove);
        this.startingTicks = ticksLeft;
        this.ticksLeft = ticksLeft;
    }

    @Override
    public String getNameAbbreviation() {
        return (nameAbbreviation.equals("WND") ||
                nameAbbreviation.equals("CRIP") ||
                nameAbbreviation.equals("LEECH") ||
                nameAbbreviation.equals("MIASMA") ||
                nameAbbreviation.equals("SILENCE")

                ? ChatColor.RED : ChatColor.GREEN) + nameAbbreviation + ChatColor.GRAY + ":" + ChatColor.GOLD + (ticksLeft / 20 + 1) + " ";
    }

    @Override
    public void onTick() {
        subtractTime(1);
    }

    @Override
    public boolean removeCheck() {
        return ticksLeft <= 0;
    }

    public void subtractTime(int amount) {
        if (this.ticksLeft - amount <= 0) {
            ticksLeft = 0;
        } else {
            this.ticksLeft -= amount;
        }
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public boolean hasTicksLeft() {
        return ticksLeft > 0;
    }

    public int getStartingTicks() {
        return startingTicks;
    }
}
