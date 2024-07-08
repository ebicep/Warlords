package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTimeWarp extends AbstractAbility implements PurpleAbilityIcon {

    protected int timesSuccessful = 0;

    protected int tickDuration = 100;
    protected int warpHealPercentage = 30; //TODO

    public AbstractTimeWarp() {
        super("Time Warp", 28.19f, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Activate to place a time rune on the ground. After ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, you will warp back to that location and restore "))
                               .append(Component.text(warpHealPercentage + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of your health.", NamedTextColor.GRAY));
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
