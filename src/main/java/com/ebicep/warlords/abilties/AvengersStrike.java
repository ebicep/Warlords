package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvengersStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;

    protected float energyStole = 0;
    private int energySteal = 10;

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
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {

        Optional<Consecrate> oc = new CooldownFilter<>(wp, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Consecrate.class)
                .findAny();
        if (standingOnConsecrate(wp, nearPlayer) && oc.isPresent()) {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * (1 + oc.get().getStrikeDamageBoost() / 100f),
                    maxDamageHeal * (1 + oc.get().getStrikeDamageBoost() / 100f),
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

        if (pveUpgrade) {
            tripleHit(wp, nearPlayer);
        }
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public void setEnergySteal(int energySteal) {
        this.energySteal = energySteal;
    }
}
