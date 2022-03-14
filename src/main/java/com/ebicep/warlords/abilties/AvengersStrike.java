package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class AvengersStrike extends AbstractStrikeBase {

    private final int energySteal = 10;

    public AvengersStrike() {
        super("Avenger's Strike", 359, 485, 0, 90, 25, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and removing §e" + energySteal + " §7energy.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player p, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(p, nearPlayer.getEntity())) {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal * 1.2f, maxDamageHeal * 1.2f, critChance, critMultiplier, false);
        } else {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        }
        nearPlayer.subtractEnergy(energySteal);
        if (wp.getCooldownManager().hasCooldown(AvengersWrath.class)) {
            for (WarlordsPlayer wrathTarget : PlayerFilter
                    .entitiesAround(nearPlayer, 5, 4, 5)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(2)
            ) {
                //checking if player is in consecrate
                if (standingOnConsecrate(p, wrathTarget.getEntity())) {
                    wrathTarget.addDamageInstance(wp, name, minDamageHeal * 1.2f, maxDamageHeal * 1.2f, critChance, critMultiplier, false);
                } else {
                    wrathTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                }
                wrathTarget.subtractEnergy(energySteal);
            }
        }
    }
}
