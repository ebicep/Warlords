package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.FireWorkEffectPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Acupressure extends AbstractAbility {

    private final int acuRange = 8;
    private final int duration = 3;

    public Acupressure() {
        super("Acupressure", 378, 525, 12, 30, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a shockwave of special potions\n" +
                "§7around you, healing everyone in the range for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health and increase their\n" +
                "§7energy per second by §e150% §7for §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Acupressure tempAcupressure = new Acupressure();

        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
        wp.getCooldownManager().addRegularCooldown(
                "Acupressure",
                "ACU",
                Acupressure.class,
                tempAcupressure,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                duration * 20
        );

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 0.1f);
            player1.playSound(player.getLocation(), Sound.BLAZE_DEATH, 2, 0.6f);
            player1.playSound(player.getLocation(), Sound.GLASS, 2, 0.6f);
        }

        new FallingBlockWaveEffect(player.getLocation(), acuRange, 1, Material.DEAD_BUSH, (byte) 0).play();

        for (WarlordsPlayer acuTarget : PlayerFilter
                .entitiesAround(player, acuRange, acuRange, acuRange)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            FireWorkEffectPlayer.playFirework(acuTarget.getLocation(), FireworkEffect.builder()
                    .withColor(Color.ORANGE)
                    .with(FireworkEffect.Type.STAR)
                    .build());

            acuTarget.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
            acuTarget.getCooldownManager().addRegularCooldown(
                    "Acupressure",
                    "ACU",
                    Acupressure.class,
                    tempAcupressure,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    duration * 20
            );

            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    if (wp.getCooldownManager().hasCooldown(tempAcupressure)) {
                        Location lineLocation = player.getLocation().add(0, 1, 0);
                        lineLocation.setDirection(lineLocation.toVector().subtract(acuTarget.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                        for (int i = 0; i < Math.floor(player.getLocation().distance(acuTarget.getLocation())) * 2; i++) {
                            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 170, 0), lineLocation, 500);
                            lineLocation.add(lineLocation.getDirection().multiply(.5));
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 5);
        }

        return true;
    }

}
