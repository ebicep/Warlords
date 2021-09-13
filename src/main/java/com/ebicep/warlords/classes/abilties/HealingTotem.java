package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class HealingTotem extends AbstractTotemBase {

    private final int radius = 7;
    private final int duration = 6;

    public HealingTotem() {
        super("Healing Totem", 168, 224, 62.64f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Place a totem on the ground that\n" +
                "§7pulses constantly, healing nearby\n" +
                "§7allies in a §e" + radius + " §7block radius for §a" + minDamageHeal + " §7- §a" + maxDamageHeal + "\n" +
                "§7every second. The healing will gradually\n" +
                "§7increase by §a40% §7(up to 240%) every\n" +
                "§7second. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Pressing SHIFT causes your totem to\n" +
                "§7pulse with immense force, crippling all\n" +
                "§7enemies for §63 §7seconds. Crippled enemies\n" +
                "§7deal §c25% §7less damage.";
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 7);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
        totemStand.setMetadata("healing-totem-" + warlordsPlayer.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
    }

    @Override
    protected void playSound(Player player, Location location) {
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(location, "shaman.totem.activation", 2, 1);
        }
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        wp.getCooldownManager().addCooldown(name, this.getClass(), new HealingTotem(), "TOTEM", duration, wp, CooldownTypes.ABILITY);


        new BukkitRunnable() {
            int timeLeft = duration;

            @Override
            public void run() {

                if (timeLeft != 0) {
                    Location initParticleLoc = totemStand.getLocation().clone().add(0, 1.6, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.4F, 0.2F, 0.4F, 0.05F, 5, initParticleLoc, 500);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 0.9f);
                    }

                    Location totemLoc = totemStand.getLocation();
                    totemLoc.add(0, 2, 0);
                    Location particleLoc = totemLoc.clone();
                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 12; j++) {
                            double angle = j / 10D * Math.PI * 2;
                            double width = radius;
                            particleLoc.setX(totemLoc.getX() + Math.sin(angle) * width);
                            particleLoc.setY(totemLoc.getY() + i / 2D);
                            particleLoc.setZ(totemLoc.getZ() + Math.cos(angle) * width);

                            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 255, 40), particleLoc, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, particleLoc, 500);
                        }
                    }

                    CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), totemStand.getLocation().add(0, 1, 0), radius);
                    circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE).particlesPerCircumference(2));
                    circle.playEffects();

                    //1
                    //1.4
                    //1.8
                    //2.2
                    //2.6
                    //3.0
                    //3.4
                    float healMultiplier = 1 + (.4f * (6 - timeLeft));
                    PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealth(
                                        wp,
                                        name,
                                        minDamageHeal * healMultiplier,
                                        maxDamageHeal * healMultiplier,
                                        critChance,
                                        critMultiplier,
                                        false);
                            });
                } else {
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), Sound.BLAZE_DEATH, 1.2f, 0.7f);
                    }

                    new FallingBlockWaveEffect(totemStand.getLocation().clone().add(0, 1, 0), radius, 1.2, Material.SAPLING, (byte) 1).play();

                    PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealth(
                                        wp,
                                        name,
                                        maxDamageHeal,
                                        maxDamageHeal * 1.354f,
                                        critChance,
                                        critMultiplier,
                                        false);
                            });

                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (wp.isDeath() || counter >= 20 * duration) {
                    this.cancel();
                } else if (player.isSneaking()) {
                    PlayerFilter.entitiesAround(totemStand.getLocation(), radius, radius, radius)
                            .aliveEnemiesOf(wp)
                            .forEach((p) -> {
                                p.getCooldownManager().addCooldown("Totem Crippling", HealingTotem.class, new HealingTotem(), "CRIP", 3, wp, CooldownTypes.DEBUFF);
                            });
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "paladin.hammeroflight.impact", 1.5f, 0.2f);
                    }
                    player.sendMessage("§aAll enemies in your totem are now §ccrippled §afor 3 seconds!");
                    new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), 7, 2, Material.SAPLING, (byte) 1).play();
                    this.cancel();
                }
                counter++;
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }


}