package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class JudgementStrike extends AbstractStrikeBase {

    int attacksDone = 0;

    public JudgementStrike() {
        super("Judgement Strike", 326, 441, 0, 40, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Every 3rd attack is a §cguaranteed §7critical strike.\n" +
                "§7Critical strikes temporarily increase your movement\n" +
                "§7speed by §e20% §7for §e2 §7seconds.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {

        attacksDone++;
        int critChance = this.critChance;
        if (attacksDone == 3) {
            attacksDone = 0;
            critChance = 100;
        }
        nearPlayer.damageHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }
}
