package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AvengersStrike extends AbstractStrikeBase {

    protected float energyStole = 0;
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
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Removed", "" + Math.round(energyStole)));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player p, @Nonnull WarlordsEntity nearPlayer) {
        if (standingOnConsecrate(wp, nearPlayer)) {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * 1.2f,
                    maxDamageHeal * 1.2f,
                    critChance,
                    critMultiplier,
                    false
            );
        } else {
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
        }

        energyStole += nearPlayer.subtractEnergy(energySteal);

        if (wp.getCooldownManager().hasCooldown(AvengersWrath.class)) {
            for (WarlordsEntity wrathTarget : PlayerFilter
                    .entitiesAround(nearPlayer, 5, 4, 5)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(2)
            ) {
                wp.doOnStaticAbility(AvengersWrath.class, AvengersWrath::addExtraPlayersStruck);
                //checking if player is in consecrate
                if (standingOnConsecrate(wp, wrathTarget)) {
                    wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
                    wrathTarget.addDamageInstance(
                            wp,
                            name,
                            minDamageHeal * 1.2f,
                            maxDamageHeal * 1.2f,
                            critChance,
                            critMultiplier,
                            false
                    );
                } else {
                    wrathTarget.addDamageInstance(
                            wp,
                            name,
                            minDamageHeal,
                            maxDamageHeal,
                            critChance,
                            critMultiplier,
                            false
                    );
                }

                energyStole += wrathTarget.subtractEnergy(energySteal);
            }
        }
    }
}
