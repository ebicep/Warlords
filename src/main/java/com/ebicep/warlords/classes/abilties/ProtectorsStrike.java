package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ProtectorsStrike extends AbstractStrikeBase {

    // Percentage
    private int minConvert = 50;
    private int maxConvert = 100;

    public ProtectorsStrike() {
        super("Protector's Strike", 261, 352, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c261 §7- §c352 §7damage\n" +
                "§7and healing two nearby allies for\n" +
                "§a" + maxConvert + "-" + minConvert + "% §7of the damage done. Also\n" +
                "§7heals yourself by §a" + minConvert + "-" + maxConvert + "% §7of the\n" +
                "§7damage done. Based on your current\n" +
                "health.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(player, nearPlayer)) {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier, false);
        } else {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        }
    }

    public void setMinConvert(int convertPercent) {
        this.minConvert = convertPercent;
    }

    public void setMaxConvert(int selfConvertPercent) {
        this.maxConvert = selfConvertPercent;
    }
}