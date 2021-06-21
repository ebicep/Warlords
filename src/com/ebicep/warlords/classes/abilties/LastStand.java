package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;


public class LastStand extends AbstractAbility {

    public LastStand() {
        super("Last Stand", 0, 0, 58, 40, 0, 0,
                "§7Enter a defensive stance,\n" +
                        "§7reducing all damage you take by\n" +
                        "§c50% §7for §612 §7seconds and also\n" +
                        "§7reduces all damage nearby allies take\n" +
                        "§7by §c40% §7for §66 §7seconds. You are\n" +
                        "§ahealed §7for the amount of damage\n" +
                        "§7prevented on allies.");
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setLastStandedBy(warlordsPlayer);
        warlordsPlayer.setLastStandDuration(12);
        PlayerFilter.entitiesAround(warlordsPlayer, 4, 4, 4)
            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
            .forEach((nearPlayer) -> {
                nearPlayer.setLastStandDuration(6);
                nearPlayer.setLastStandedBy(warlordsPlayer);
                player.sendMessage("you last standed " + nearPlayer.getName());
            });
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.laststand.activation", 2, 1);
        }
    }
}
