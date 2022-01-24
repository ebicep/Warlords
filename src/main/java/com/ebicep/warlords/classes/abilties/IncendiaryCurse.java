package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class IncendiaryCurse extends AbstractAbility {

    public IncendiaryCurse() {
        super("Incendiary Curse", 408, 575, 10, 30, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Ignite the targeted area with a cross flame,\n" +
                    "§7dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage. §7After 3 seconds,\n" +
                    "§7enemies hit will burn for §f101 §7- §f146 §7true damage\n" +
                    "§7every second for §63 §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return false;
        DamageHealCircle curse = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation(), 4, 1, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        curse.getLocation().add(0, 1, 0);

        curse.spawn();

        PlayerFilter.entitiesAround(curse.getLocation(), curse.getRadius(), curse.getRadius(), curse.getRadius())
                .aliveEnemiesOf(wp)
                .forEach((warlordsPlayer) -> {
                    warlordsPlayer.addDamageInstance(
                            curse.getWarlordsPlayer(),
                            curse.getName(),
                            curse.getMinDamage(),
                            curse.getMaxDamage(),
                            curse.getCritChance(),
                            curse.getCritMultiplier(),
                            false);
                });

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 2, 1.6f);
            player1.playSound(player.getLocation(), "mage.fireball.activation", 2, 1.8f);
        }

        wp.subtractEnergy(energyCost);

        return true;
    }
}
