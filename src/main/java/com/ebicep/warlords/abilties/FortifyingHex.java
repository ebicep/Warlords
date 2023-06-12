package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class FortifyingHex extends AbstractAbility {

    private int abilityTimerTickIncrease = 10;
    private int hexShieldAmount = 500;
    private int hexTickDuration = 60;
    private int range = 15;

    public FortifyingHex() {
        super("Fortifying Hex", 0, 0, 0, 80, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Fling a hexed tendril forward, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to a single target. Upon contact, the hex explodes, increasing all nearby enemy’s rune timers by "))
                               .append(Component.text(format(abilityTimerTickIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. You receive one stack of Fortifying Hex, which shields you from any next attack less than"))
                               .append(Component.text(hexShieldAmount, NamedTextColor.RED))
                               .append(Component.text(" for "))
                               .append(Component.text(format(hexTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Fortifying Hex stacks up to 3 times. \n\nHas an optimal range of "))
                               .append(Component.text(15, NamedTextColor.YELLOW))
                               .append(Component.text("blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        return false;
    }
}
