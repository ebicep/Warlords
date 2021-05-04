package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {

    public static boolean getLookingAt(Player player, Player player1) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + .5);
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());

        return dot > 0.98D;
    }

    public static boolean lookingAtTotem(Player player) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + .5);
        AtomicBoolean lookingAt = new AtomicBoolean(false);
        Warlords.getTotems().stream().filter(o -> o.getOwner().equals(Warlords.getPlayer(player))).forEach(o -> {
                    Vector toEntity = o.getTotemArmorStand().getEyeLocation().toVector().subtract(eye.toVector());
                    float dot = (float) toEntity.normalize().dot(eye.getDirection());
                    System.out.println(dot);
                    lookingAt.set(dot > .98f);
                }
        );

        return lookingAt.get();
    }

}
