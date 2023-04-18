package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTimeWarpBase extends AbstractAbility {

    protected int timesSuccessful = 0;

    protected int tickDuration = 100;
    protected int warpHealPercentage = 30;

    public AbstractTimeWarpBase() {
        super("Time Warp", 0, 0, 28.19f, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Activate to place a time rune on the ground. After §6" + format(tickDuration / 20f) +
                " §7seconds, you will warp back to that location and restore §a" + warpHealPercentage + "% §7of your health";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Successful", "" + timesSuccessful));

        return info;
    }

    public int getTimesSuccessful() {
        return timesSuccessful;
    }

    public int getWarpHealPercentage() {
        return warpHealPercentage;
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }

    public int getTickDuration() {
        return tickDuration;
    }

    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
