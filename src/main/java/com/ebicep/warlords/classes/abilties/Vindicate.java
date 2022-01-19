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
import org.bukkit.ChatColor;
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
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
            player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        }

        Vindicate tempVindicate = new Vindicate();
        wp.getCooldownManager().addRegularCooldown("Vindicate Resistance", "VIND RES", Vindicate.class, tempVindicate, wp, CooldownTypes.BUFF, cooldownManager -> {
        }, vindicateDuration * 20);

        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                            nearPlayer.getSpeed().removeSlownessModifiers();
                            nearPlayer.getCooldownManager().removeDebuffCooldowns();
                            wp.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " Your Vindicate is now protecting " + ChatColor.YELLOW + nearPlayer.getName() + ChatColor.GRAY + "!");
                            nearPlayer.getCooldownManager().addRegularCooldown("Vindicate Debuff Immunity", "VIND", Vindicate.class, tempVindicate, wp, CooldownTypes.BUFF, cooldownManager -> {
                            }, vindicateDuration * 20);
                            nearPlayer.getCooldownManager().addRegularCooldown("KB Resistance", "KB", Vindicate.class, tempVindicate, wp, CooldownTypes.BUFF, cooldownManager -> {
                            }, vindicateDuration * 20);
                        }
                );

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();

        EffectUtils.playHelixAnimation(player, radius, 230, 130, 5);
        EffectUtils.playCylinderAnimation(player, radius, 40, 40, 40);

        return true;
    }
}
