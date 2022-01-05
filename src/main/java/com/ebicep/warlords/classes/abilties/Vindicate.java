package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import javafx.scene.media.VideoTrack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Vindicate extends AbstractAbility {

    private final int radius = 6;

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

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "rogue.vindicate.activation", 2, 1.1f);
            player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        }

        wp.getCooldownManager().addCooldown("Vindicate Resistance", this.getClass(), Vindicate.class, "VIND RES", 6, wp, CooldownTypes.BUFF);

        Vindicate allyVindicate = new Vindicate();
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                        nearPlayer.getCooldownManager().addCooldown("Vindicate Debuff Immunity", this.getClass(), allyVindicate, "VIND", 6, wp, CooldownTypes.BUFF);
                    }
                );

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();
    }
}
