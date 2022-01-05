package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeartToHeart extends AbstractAbility {

    public HeartToHeart() {
        super("Heart To Heart", 0, 0, 13, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (WarlordsPlayer heartTarget : PlayerFilter
                .entitiesAround(wp, 12, 12, 12)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, heartTarget.getEntity()) && Utils.hasLineOfSight(player, heartTarget.getEntity())) {
                Location dashLoc = heartTarget.getLocation();
                player.setVelocity(dashLoc.getDirection().multiply(5).setY(.1));
            }
        }
    }
}
