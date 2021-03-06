package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CrusadersStrike extends AbstractStrikeBase {
    protected int energyGivenToPlayers = 0;

    private final int energyGiven = 24;
    private final int energyRadius = 10;

    public CrusadersStrike() {
        super("Crusader's Strike", 326, 441, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage and\n" +
                "§7restoring §e" + energyGiven + " §7energy to two nearby\n" +
                "§7allies within §e" + energyRadius + " §7blocks." +
                "\n\n" +
                "§7MARKED allies get priority in restoring energy and\n" +
                "§7increases their speed by §e40% §7for §61 §7second.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Given", "" + energyGivenToPlayers));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (standingOnConsecrate(wp, nearPlayer)) {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * 1.15f,
                    maxDamageHeal * 1.15f,
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

        // Give energy to nearby allies and check if they have mark active
        for (WarlordsPlayer energyTarget : PlayerFilter
                .entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .sorted(
                    Comparator.comparing(
                    (WarlordsPlayer p) -> p.getCooldownManager().hasCooldown(HolyRadianceCrusader.class) ? 0 : 1)
                    .thenComparing(Utils.sortClosestBy(WarlordsPlayer::getLocation, wp.getLocation()))
                )
                .limit(2)
        ) {
            if (energyTarget.getCooldownManager().hasCooldown(HolyRadianceCrusader.class)) {
                energyTarget.getSpeed().addSpeedModifier("CRUSADER MARK", 40, 20, "BASE"); // 20 ticks
            }

            energyGivenToPlayers += energyTarget.addEnergy(wp, name, energyGiven);
        }
    }
}
