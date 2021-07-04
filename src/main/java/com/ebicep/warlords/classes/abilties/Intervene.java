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
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        setDamagePrevented(0);
        PlayerFilter.entitiesAround(warlordsPlayer, 10, 10, 10)
            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
            .requireLineOfSight(warlordsPlayer)
            .closestFirst(warlordsPlayer)
            .first((nearWarlordsPlayer) -> {
                //green line thingy
                Location lineLocation = player.getLocation().add(0, 1, 0);
                lineLocation.setDirection(lineLocation.toVector().subtract(nearWarlordsPlayer.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                for (int i = 0; i < Math.floor(player.getLocation().distance(nearWarlordsPlayer.getLocation())) * 4; i++) {
                    ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.35F, 1, lineLocation, 500);
                    lineLocation.add(lineLocation.getDirection().multiply(.25));
                }

                //new cooldown, both players have same instance of intervene now
                Cooldown interveneCooldown = new Cooldown(Intervene.this.getClass(), "VENE", 6, warlordsPlayer, CooldownTypes.ABILITY);

                warlordsPlayer.sendMessage("§a\u00BB§7 You are now protecting " + nearWarlordsPlayer.getName() + " with your §eIntervene!");
                warlordsPlayer.getCooldownManager().addCooldown(interveneCooldown);

                //removing all other intervenes bc less work
                nearWarlordsPlayer.getCooldownManager().getCooldowns().removeAll(nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.this.getClass()));

                nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + warlordsPlayer.getName() + " is shielding you with their " + ChatColor.YELLOW + "Intervene" + ChatColor.GRAY + "!");
                nearWarlordsPlayer.getCooldownManager().addCooldown(interveneCooldown);

                warlordsPlayer.getSpec().getBlue().setCurrentCooldown(cooldown);
                warlordsPlayer.updateBlueItem();

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "warrior.intervene.impact", 1, 1);
                }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).size() > 0) {
                                if (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getTimeLeft() <= 1)
                                    nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + warlordsPlayer.getName() + "'s §eIntervene §7will expire in §6" + (int) (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getTimeLeft() + .5) + "§7 second!");
                                else
                                    nearWarlordsPlayer.sendMessage("§a\u00BB§7 " + warlordsPlayer.getName() + "'s §eIntervene §7will expire in §6" + (int) (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getTimeLeft() + .5) + "§7 seconds!");
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 0, 20);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).size() > 0) {
                                if (nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getFrom().isDead() ||
                                        nearWarlordsPlayer.getLocation().distanceSquared(nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getFrom().getEntity().getLocation()) > 15 * 15
                                ) {
                                    nearWarlordsPlayer.sendMessage("§c\u00AB§7 " + nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0).getFrom().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                                    nearWarlordsPlayer.getCooldownManager().getCooldowns().remove(nearWarlordsPlayer.getCooldownManager().getCooldown(Intervene.class).get(0));
                                    // TODO: This is impossible with offline player support
                                    //nearWarlordsPlayer.removeMetadata("INTERVENE", Warlords.getInstance());
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
