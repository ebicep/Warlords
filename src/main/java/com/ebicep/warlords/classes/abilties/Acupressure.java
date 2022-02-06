package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Acupressure extends AbstractAbility {

    private final int acuRange = 10;
    private final int duration = 6;

    public Acupressure() {
        super("Acupressure", 0, 0, 18, 0, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Mark an ally within §6" + acuRange + " §7blocks of you. You and\n" +
                "§7the marked ally become energized, increasing\n" +
                "§7energy per hit/second by §e80% §7for §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Acupressure tempAcupressure = new Acupressure();

        for (WarlordsPlayer acuTarget : PlayerFilter
                .entitiesAround(player, acuRange, acuRange, acuRange)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            // Ally target
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

            // Caster
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
            }

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

            return true;
        }

        return false;
    }

}
