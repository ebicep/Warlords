package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
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
    private int vindicateSelfDuration = 6;

    public Vindicate() {
        super("Vindicate", 0, 0, 55, 25, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Allies within an §e" + radius + " §7block radius gain the\n" +
                "§7status §6VIND §7which clears all de-buffs. In\n" +
                "§7addition, the status §6VIND §7prevents allies from being\n" +
                "§7affected by de-buffs and grants §625% §7knockback\n" +
                "§7resistance for §6" + vindicateDuration + " §7seconds." +
                "\n\n" +
                "§7You gain §e25% §7damage reduction for §6" + vindicateSelfDuration + " §7seconds instead.\n" +
                "§7Each ally within your Vindicate radius increases\n" +
                "§7the duration by §61 §7second. (up to §610 §7seconds.)";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
            player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        }

        Vindicate tempVindicate = new Vindicate();

        for (WarlordsPlayer vindicateTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
        ) {
            wp.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " Your Vindicate is now protecting " + ChatColor.YELLOW + vindicateTarget.getName() + ChatColor.GRAY + "!");

            // Vindicate Immunity
            vindicateTarget.getSpeed().removeSlownessModifiers();
            vindicateTarget.getCooldownManager().removeDebuffCooldowns();
            vindicateTarget.getCooldownManager().removeCooldown(Vindicate.class);
            vindicateTarget.getCooldownManager().addRegularCooldown(
                    "Vindicate Debuff Immunity",
                    "VIND",
                    Vindicate.class,
                    tempVindicate,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    vindicateDuration * 20
            );

            wp.getSpeed().removeSlownessModifiers();
            wp.getCooldownManager().removeDebuffCooldowns();
            wp.getCooldownManager().removeCooldown(Vindicate.class);
            wp.getCooldownManager().addRegularCooldown(
                    "Vindicate Debuff Immunity",
                    "VIND",
                    Vindicate.class,
                    tempVindicate,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    vindicateDuration * 20
            );

            // KB Resistance
            vindicateTarget.getCooldownManager().addRegularCooldown(
                    "KB Resistance",
                    "KB",
                    Vindicate.class,
                    tempVindicate,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    vindicateDuration * 20
            );

            wp.getCooldownManager().addRegularCooldown(
                    "KB Resistance",
                    "KB",
                    Vindicate.class,
                    tempVindicate,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    vindicateDuration * 20
            );

            vindicateSelfDuration++;
        }

        if (vindicateSelfDuration > 10) {
            vindicateSelfDuration = 10;
        }

        wp.getCooldownManager().addRegularCooldown(
                "Vindicate Resistance",
                "VIND RES",
                Vindicate.class,
                tempVindicate,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                vindicateSelfDuration * 20);

        vindicateSelfDuration = 6;

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();

        EffectUtils.playHelixAnimation(player, radius, 230, 130, 5);

        return true;
    }
}
