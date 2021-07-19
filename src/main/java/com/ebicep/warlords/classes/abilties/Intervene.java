package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Cooldown;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class Intervene extends AbstractAbility {
    private float damagePrevented = 0;

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
                "§e15 §7blocks of each other. Lasts §65 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        setDamagePrevented(0);
        PlayerFilter.entitiesAround(wp, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
                .first((nearWarlordsPlayer) -> {
                    //green line thingy
                    Location lineLocation = player.getLocation().add(0, 1, 0);
                    lineLocation.setDirection(lineLocation.toVector().subtract(nearWarlordsPlayer.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                    for (int i = 0; i < Math.floor(player.getLocation().distance(nearWarlordsPlayer.getLocation())) * 2; i++) {
                        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.35F, 1, lineLocation, 500);
                        lineLocation.add(lineLocation.getDirection().multiply(.5));
                    }

                    //new cooldown, both players have same instance of intervene now
                    Intervene tempIntervene = new Intervene();

                    //removing all other intervenes bc less work
                    wp.getCooldownManager().getCooldowns().removeIf(cd -> cd.getCooldownClass() == Intervene.class && nearWarlordsPlayer.getCooldownManager().hasCooldown(cd.getCooldownObject()));
                    nearWarlordsPlayer.getCooldownManager().getCooldowns().removeIf(cd -> {
                        if (cd.getCooldownClass() == Intervene.class) {
                            cd.getFrom().sendMessage("§c\u00AB§7 " + cd.getFrom().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                            nearWarlordsPlayer.sendMessage("§c\u00AB§7 " + cd.getFrom().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                            return true;
                        } else {
                            return false;
                        }
                    });

                    wp.sendMessage("§a\u00BB§7 You are now protecting " + nearWarlordsPlayer.getName() + " with your §eIntervene!");
                    wp.getCooldownManager().addCooldown(new Cooldown(Intervene.this.getClass(), tempIntervene, "VENE", 5, wp, CooldownTypes.ABILITY));
                    nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + wp.getName() + " is shielding you with their " + ChatColor.YELLOW + "Intervene" + ChatColor.GRAY + "!");
                    nearWarlordsPlayer.getCooldownManager().addCooldown(new Cooldown(Intervene.this.getClass(), tempIntervene, "VENE", 5, wp, CooldownTypes.ABILITY));

                    wp.getSpec().getBlue().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
                    wp.updateBlueItem();

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "warrior.intervene.impact", 1, 1);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (nearWarlordsPlayer.getCooldownManager().hasCooldown(tempIntervene)) {
                                if (nearWarlordsPlayer.getCooldownManager().getCooldown(tempIntervene).get().getTimeLeft() <= 1)
                                    nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + wp.getName() + "'s §eIntervene §7will expire in §6" + (int) (nearWarlordsPlayer.getCooldownManager().getCooldown(tempIntervene).get().getTimeLeft() + .5) + "§7 second!");
                                else
                                    nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + wp.getName() + "'s §eIntervene §7will expire in §6" + (int) (nearWarlordsPlayer.getCooldownManager().getCooldown(tempIntervene).get().getTimeLeft() + .5) + "§7 seconds!");
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 0, 20);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (nearWarlordsPlayer.getCooldownManager().hasCooldown(tempIntervene)) {
                                if (nearWarlordsPlayer.getCooldownManager().getCooldown(tempIntervene).get().getFrom().isDeath() ||
                                        nearWarlordsPlayer.getLocation().distanceSquared(nearWarlordsPlayer.getCooldownManager().getCooldown(tempIntervene).get().getFrom().getEntity().getLocation()) > 15 * 15
                                ) {
                                    wp.sendMessage("§c\u00AB§7 " + wp.getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                                    wp.getCooldownManager().removeCooldown(tempIntervene);

                                    nearWarlordsPlayer.sendMessage("§c\u00AB§7 " + wp.getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                                    nearWarlordsPlayer.getCooldownManager().removeCooldown(tempIntervene);
                                }
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 0, 0);
                });
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
