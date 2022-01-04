package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import javafx.scene.media.VideoTrack;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Vindicate extends AbstractAbility {

    public Vindicate() {
        super("Vindicate", 0, 0, 55, 25, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        wp.getCooldownManager().addCooldown("Vindicate", this.getClass(), Vindicate.class, "VIND RES", 6, wp, CooldownTypes.BUFF);

        Vindicate allyVindicate = new Vindicate();
        PlayerFilter.entitiesAround(wp, 6, 6, 6)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                        nearPlayer.getCooldownManager().addCooldown("Vindicate", this.getClass(), allyVindicate, "VIND", 6, wp, CooldownTypes.BUFF);
                    }
                );
    }
}
