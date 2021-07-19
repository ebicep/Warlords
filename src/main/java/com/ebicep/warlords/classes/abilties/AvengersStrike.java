package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class AvengersStrike extends AbstractStrikeBase {

    public AvengersStrike() {
        super("Avenger's Strike", -359, -485, 0, 90, 25, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7and removing §e6 §7energy.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(player, nearPlayer.getEntity())) {
            nearPlayer.addHealth(wp, name, (minDamageHeal * 1.2f), (maxDamageHeal * 1.2f), critChance, critMultiplier);
        } else {
            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }
        nearPlayer.subtractEnergy(6);
        if (!wp.getCooldownManager().getCooldown(AvengersWrath.class).isEmpty()) {
            for (WarlordsPlayer nearNearPlayer : PlayerFilter
                    .entitiesAround(nearPlayer, 5, 3, 5)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(2)
            ) {
                //checking if player is in consecrate
                if (standingOnConsecrate(player, nearNearPlayer.getEntity())) {
                    nearNearPlayer.addHealth(wp, name, minDamageHeal * 1.2f, maxDamageHeal * 1.2f, critChance, critMultiplier);
                } else {
                    nearNearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                }
                nearNearPlayer.subtractEnergy(6);
            }
        }
    }
}
