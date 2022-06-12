package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * This type of cooldown is used for any regular cooldowns that are removed when its timer reaches 0
 */
public class RegularCooldown<T> extends AbstractCooldown<T> {

    protected int startingTicks;
    protected int ticksLeft;
    protected int counter;
    /**
     * cooldown = this
     * ticksLeft = ticksLeft of cooldown
     * counter = counter incrementing every tick, separate from ticksLeft
     */
    protected final List<TriConsumer<RegularCooldown<T>, Integer, Integer>> consumers;

    public RegularCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsEntity from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int ticksLeft) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove);
        this.startingTicks = ticksLeft;
        this.ticksLeft = ticksLeft;
        this.consumers = new ArrayList<>();
    }

    @SafeVarargs
    public RegularCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsEntity from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int ticksLeft, TriConsumer<RegularCooldown<T>, Integer, Integer>... triConsumers) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove);
        this.startingTicks = ticksLeft;
        this.ticksLeft = ticksLeft;
        this.consumers = new ArrayList<>(Arrays.asList(triConsumers));
    }

    @Override
    public String getNameAbbreviation() {
        if (ticksLeft <= 0) {
            return "";
        }

        return (nameAbbreviation.equals("WND") ||
                nameAbbreviation.equals("CRIP") ||
                nameAbbreviation.equals("LCH") ||
                nameAbbreviation.equals("MIAS") ||
                nameAbbreviation.equals("AVE MARK") ||
                nameAbbreviation.equals("SILENCE")

                ? ChatColor.RED : ChatColor.GREEN) + nameAbbreviation + ChatColor.GRAY + ":" + ChatColor.GOLD + (ticksLeft / 20 + 1) + " ";
    }

    @Override
    public void onTick() {
        consumers.forEach(integerConsumer -> integerConsumer.accept(this, ticksLeft, counter));
        counter++;
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

    public void addTriConsumer(TriConsumer<RegularCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.add(triConsumer);
    }

    public void removeTriConsumer(TriConsumer<RegularCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.remove(triConsumer);
    }
}
