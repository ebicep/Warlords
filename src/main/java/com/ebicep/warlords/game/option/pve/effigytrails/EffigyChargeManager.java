package com.ebicep.warlords.game.option.pve.effigytrails;

import com.ebicep.warlords.util.java.MathUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.Iterator;

public class EffigyChargeManager {

    private final Iterator<Integer> chargeIterator;
    private int currentChargeNeeded;
    private int currentCharge;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);

    public EffigyChargeManager(int... chargesNeeded) {
        this.chargeIterator = Arrays.stream(chargesNeeded).boxed().iterator();
    }

    public void updateBossBar() {
        bossBar.name(Component.textOfChildren(
                Component.text("Charge Left: ", NamedTextColor.RED),
                Component.text(Math.max(0, currentChargeNeeded - currentCharge), NamedTextColor.WHITE)
        ));
        bossBar.progress(MathUtils.clamp((float) currentCharge / currentChargeNeeded, 0, 1));
    }

    public boolean advance() {
        if (chargeIterator.hasNext()) {
            currentChargeNeeded = chargeIterator.next();
            return true;
        }
        return false;
    }

}
