package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CrusadersStrike extends AbstractStrikeBase {

    private final int energyGiven = 24;
    private final int energyRadius = 10;

    public CrusadersStrike() {
        super("Crusader's Strike", -326, -441, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage and\n" +
                "§7restoring §e" + energyGiven + " §7energy to two nearby\n" +
                "§7allies within §e" + energyRadius + " §7blocks.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(player, nearPlayer)) {
            nearPlayer.addHealth(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier, false);
        } else {
            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        }
        //reloops near players to give energy to
        PlayerFilter.entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(2)
                .forEach((nearTeamPlayer) ->
                        nearTeamPlayer.addEnergy(wp, name, energyGiven)
                );
    }
}
