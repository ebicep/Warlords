package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CrusadersStrike extends AbstractStrikeBase {

    public CrusadersStrike() {
        super("Crusader's Strike", -326, -441, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " damage\n" +
                "§7and restoring §e24 §7energy to two nearby\n" +
                "§7within §e10 §7blocks.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(player, nearPlayer)) {
            nearPlayer.addHealth(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier);
        } else {
            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }
        //reloops near players to give energy to
        PlayerFilter.entitiesAround(wp, 10.0, 10.0, 10.0)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(2)
                .forEach((nearTeamPlayer) ->
                        nearTeamPlayer.addEnergy(wp, name, 24)
                );
    }
}
