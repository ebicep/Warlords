package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class Berserk extends AbstractAbility {

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You go into a berserker rage,\n" +
                "§7increasing your damage by §c25% §7and\n" +
                "§7movement speed by §e30%§7. While active,\n" +
                "§7you also take §c10% §7more damage.\n" + "§7Lasts §618 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.getSpeed().addSpeedModifier("Berserk", 30, 18 * 20, "BASE");
        warlordsPlayer.setBerserk(18);
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.berserk.activation", 2, 1);
        }
    }
}
