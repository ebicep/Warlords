package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Optional;


public class Intervene extends AbstractAbility {

    private float damagePrevented = 0;

    private final int duration = 5;
    private final int radius = 10;

    public Intervene() {
        super("Intervene", 0, 0, 14.09f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Protect the target ally, reducing\n" +
                "§7the damage they take by §e100%\n" +
                "§7and redirecting §e50% §7of the damage\n" +
                "§7they would have taken back to you.\n" +
                "§7You can protect the target for a maximum\n" +
                "§7of §c3600 §7damage. You must remain within\n" +
                "§e15 §7blocks of each other. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        setDamagePrevented(0);
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp)
                .lookingAtFirst(wp)
                .first((vt) -> {
                    //green line thingy
                    Location lineLocation = player.getLocation().add(0, 1, 0);
                    lineLocation.setDirection(lineLocation.toVector().subtract(vt.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                    for (int i = 0; i < Math.floor(player.getLocation().distance(vt.getLocation())) * 2; i++) {
                        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1, lineLocation, 500);
                        lineLocation.add(lineLocation.getDirection().multiply(.5));
                    }

                    //new cooldown, both players have same instance of intervene now
                    Intervene tempIntervene = new Intervene();

                    //removing all other intervenes bc less work
                    wp.getCooldownManager().getCooldowns().removeIf(cd -> cd.getCooldownClass() == Intervene.class && vt.getCooldownManager().hasCooldown(cd.getCooldownObject()));
                    vt.getCooldownManager().getCooldowns().removeIf(cd -> {
                        if (cd.getCooldownClass() == Intervene.class) {
                            cd.getFrom().sendMessage("§c\u00AB§7 " + cd.getFrom().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                            vt.sendMessage("§c\u00AB§7 " + cd.getFrom().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                            return true;
                        } else {
                            return false;
                        }
                    });

                    wp.sendMessage("§a\u00BB§7 You are now protecting " + vt.getName() + " with your §eIntervene!");
                    wp.getCooldownManager().addRegularCooldown(name, "VENE", Intervene.class, tempIntervene, wp, CooldownTypes.ABILITY, cooldownManager -> {
                    }, duration * 20);
                    vt.sendMessage("§a\u00BB§7 " + wp.getName() + " is shielding you with their " + ChatColor.YELLOW + "Intervene" + ChatColor.GRAY + "!");
                    vt.getCooldownManager().addRegularCooldown(name, "VENE", Intervene.class, tempIntervene, wp, CooldownTypes.ABILITY, cooldownManager -> {
                    }, duration * 20);

                    wp.getSpec().getBlue().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
                    wp.updateBlueItem();

                    wp.subtractEnergy(energyCost);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "warrior.intervene.impact", 1, 1);
                    }
                    wp.getGame().getGameTasks().put(

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Optional<RegularCooldown> optionalRegularCooldown = new CooldownFilter<>(vt, RegularCooldown.class).filterCooldownObject(tempIntervene).findFirst();
                                    if (optionalRegularCooldown.isPresent()) {
                                        RegularCooldown interveneRegularCooldown = optionalRegularCooldown.get();
                                        if (interveneRegularCooldown.getTicksLeft() <= 20)
                                            vt.sendMessage("§a\u00BB§7 " + wp.getName() + "'s §eIntervene §7will expire in §6" + (int) (interveneRegularCooldown.getTicksLeft() / 20 + .5) + "§7 second!");
                                        else
                                            vt.sendMessage("§a\u00BB§7 " + wp.getName() + "'s §eIntervene §7will expire in §6" + (int) (interveneRegularCooldown.getTicksLeft() / 20 + .5) + "§7 seconds!");
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 20),
                            System.currentTimeMillis()
                    );
                    wp.getGame().getGameTasks().put(

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Optional<RegularCooldown> optionalRegularCooldown = new CooldownFilter<>(vt, RegularCooldown.class).filterCooldownObject(tempIntervene).findFirst();
                                    if (wp.isDeath() ||
                                            tempIntervene.damagePrevented >= (3600 / 2.0) ||
                                            !optionalRegularCooldown.isPresent() ||
                                            vt.getLocation().distanceSquared(optionalRegularCooldown.get().getFrom().getEntity().getLocation()) > 15 * 15
                                    ) {
                                        wp.sendMessage("§c\u00AB§7 " + wp.getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                                        wp.getCooldownManager().removeCooldown(tempIntervene);

                                        vt.sendMessage("§c\u00AB§7 " + wp.getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                                        vt.getCooldownManager().removeCooldown(tempIntervene);

                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 0),
                            System.currentTimeMillis()
                    );
                });

        return true;
    }

    public void setDamagePrevented(float damagePrevented) {
        this.damagePrevented = damagePrevented;
    }

    public float getDamagePrevented() {
        return damagePrevented;
    }

    public void addDamagePrevented(float amount) {
        this.damagePrevented += amount;
    }

}
