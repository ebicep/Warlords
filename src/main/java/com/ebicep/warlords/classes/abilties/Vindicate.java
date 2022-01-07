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

    private final int radius = 8;
    private final int vindicateDuration = 6;

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
            player1.playSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.85f);
            player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        }

        wp.getCooldownManager().addCooldown("Vindicate Resistance", this.getClass(), Vindicate.class, "VIND RES", vindicateDuration, wp, CooldownTypes.BUFF);

        Vindicate allyVindicate = new Vindicate();
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                        nearPlayer.getSpeed().removeNegTimeModifier();
                        nearPlayer.getCooldownManager().removeDebuffCooldowns();
                        nearPlayer.getCooldownManager().addCooldown("Vindicate Debuff Immunity", this.getClass(), allyVindicate, "VIND", vindicateDuration, wp, CooldownTypes.BUFF);
                        nearPlayer.getCooldownManager().addCooldown("KB Resistance", this.getClass(), Vindicate.class, "KB", vindicateDuration, wp, CooldownTypes.BUFF);
                    }
                );

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();

        double rotation = Math.PI / 4;
        int particles = 40;
        int strands = 8;
        int curve = 10;
        Location location = player.getLocation();
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = Math.cos(angle) * ratio * radius;
                double z = Math.sin(angle) * ratio * radius;
                location.add(x, 0, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(230, 130, 5), location, 500);
                location.subtract(x, 0, z);
            }
        }

        Location particleLoc = location.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                double width = radius;
                particleLoc.setX(location.getX() + Math.sin(angle) * width);
                particleLoc.setY(location.getY() + i / 7D);
                particleLoc.setZ(location.getZ() + Math.cos(angle) * width);

                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(65, 65, 65), particleLoc, 500);
            }
        }
    }
}
