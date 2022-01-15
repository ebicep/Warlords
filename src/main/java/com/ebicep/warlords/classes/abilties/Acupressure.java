package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class Acupressure extends AbstractAbility {

    private final int acuRange = 6;
    private final int duration = 6;

    public Acupressure() {
        super("Acupressure", 0, 0, 22, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Mark an ally within §6" + acuRange + " §7blocks of you. You and\n" +
                "§7the marked ally gain become energized, increasing\n" +
                "§7energy per hit by §e50% §7for §6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        wp.getCooldownManager().addCooldown("Acupressure", this.getClass(), Acupressure.class, "ACU", duration, wp, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 0.1f);
            player1.playSound(player.getLocation(), Sound.BLAZE_DEATH, 2, 0.1f);
        }

        for (WarlordsPlayer acuTarget : PlayerFilter
                .entitiesAround(player, acuRange, acuRange, acuRange)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtChain(player, acuTarget.getEntity())) {
                acuTarget.getCooldownManager().addCooldown("Acupressure", this.getClass(), Acupressure.class, "ACU", duration, wp, CooldownTypes.BUFF);

                wp.getGame().getGameTasks().put(

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!wp.getCooldownManager().getCooldown(Acupressure.class).isEmpty()) {
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
                        }.runTaskTimer(Warlords.getInstance(), 0, 5),
                        System.currentTimeMillis()
                );
            }
        }

    }

}
