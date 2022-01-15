package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
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
            player1.playSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.8f);
            player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        }

        wp.getCooldownManager().addCooldown("Vindicate Resistance", this.getClass(), Vindicate.class, "VIND RES", vindicateDuration, wp, CooldownTypes.BUFF);

        Vindicate allyVindicate = new Vindicate();
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                        nearPlayer.getSpeed().removeSlownessModifiers();
                        nearPlayer.getCooldownManager().removeDebuffCooldowns();
                        nearPlayer.getCooldownManager().addCooldown("Vindicate Debuff Immunity", this.getClass(), allyVindicate, "VIND", vindicateDuration, wp, CooldownTypes.BUFF);
                        nearPlayer.getCooldownManager().addCooldown("KB Resistance", this.getClass(), Vindicate.class, "KB", vindicateDuration, wp, CooldownTypes.BUFF);
                    }
                );

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();

        EffectUtils.playHelixAnimation(player, radius, 230, 130, 5);
        EffectUtils.playCylinderAnimation(player, radius, 40, 40, 40);
    }
}
