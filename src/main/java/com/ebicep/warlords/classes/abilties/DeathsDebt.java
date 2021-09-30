package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DeathsDebt extends AbstractTotemBase {
    private float delayedDamage = 0;
    private double timeLeftRespite = 0;
    private double timeLeftDebt = 0;

    public double getTimeLeftRespite() {
        return timeLeftRespite;
    }

    public void addTimeLeftRespite() {
        this.timeLeftRespite += .5;
    }

    public DeathsDebt() {
        super("Death's Debt", 0, 0, 60f + 10.49f, 20, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§2Spirits’ Respite§7: Place down a totem that\n" +
                "§7delays §c100% §7of incoming damage towards\n" +
                "§7yourself. Transforms into §dDeath’s Debt §7after\n" +
                "§64 §7- §68 §7seconds (increases with higher health),\n" +
                "§7or when you exit its §e10 §7block radius.\n" +
                "\n" +
                "§dDeath’s Debt§7: Take §c100% §7of the damage delayed\n" +
                "§7by §2Spirit's Respite §7over §66 §7seconds. The totem\n" +
                "§7will heal nearby allies for §a15% §7of all damage\n" +
                "§7that you take. If you survive, deal §c15% §7of the\n" +
                "§7damage delayed to nearby enemies.";
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.JUNGLE_FENCE_GATE);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
    }

    @Override
    protected void playSound(Player player, Location location) {
        for (Player player1 : player.getWorld().getPlayers()) {
            //TODO find the right sound - this aint right chief
            player1.playSound(location, "shaman.chainlightning.impact", 2, 2);
        }
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        final int secondsLeft = 4 + (2 * (int) Math.round((double) wp.getHealth() / wp.getMaxHealth()));
        wp.getCooldownManager().addCooldown("Spirits Respite", this.getClass(), new DeathsDebt(), "RESP", secondsLeft, wp, CooldownTypes.ABILITY);

        player.setMetadata("TOTEM", new FixedMetadataValue(Warlords.getInstance(), this));

        CircleEffect circle = new CircleEffect(wp, totemStand.getLocation().clone().add(0, 1.25, 0), 10);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL));
        circle.addEffect(new DoubleLineEffect(ParticleEffect.REDSTONE));
        BukkitTask particles = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circle::playEffects, 0, 1);

        delayedDamage = 0;
        timeLeftRespite = secondsLeft;

        //more accurate
        BukkitTask timer = new BukkitRunnable() {
            @Override
            public void run() {
                if(wp.isDeath()) {
                    this.cancel();
                } else {
                    boolean isPlayerInRadius = player.getLocation().distanceSquared(totemStand.getLocation()) < 10 * 10;
                    if (!isPlayerInRadius) {
                        timeLeftRespite = 0;
                    }
                    if(timeLeftRespite >= .05) {
                        timeLeftRespite -= .05;
                    }
                    if(timeLeftDebt >= .05) {
                        timeLeftDebt -= .05;
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (wp.isDeath()) {
                    totemStand.remove();
                    this.cancel();
                    particles.cancel();
                } else {
                    boolean isPlayerInRadius = player.getLocation().distanceSquared(totemStand.getLocation()) < 10 * 10;
                    if (timeLeftRespite > 0) {
                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                        }

                        player.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + -Math.round(getDelayedDamage()) + " §7damage. §6" + Math.round(timeLeftRespite) + " §7seconds left.");
                    } else {
                        if (timeLeftRespite == 0) {
                            wp.getCooldownManager().getCooldowns().removeIf(cd -> cd.getActionBarName().equals("RESP"));
                            wp.getCooldownManager().addCooldown(name, this.getClass(), new DeathsDebt(), "DEBT", 6, wp, CooldownTypes.ABILITY);
                            player.removeMetadata("TOTEM", Warlords.getInstance());

                            if (!isPlayerInRadius) {
                                player.sendMessage("§7You walked outside your §dDeath's Debt §7radius");
                            } else {
                                player.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + -Math.round(getDelayedDamage()) + " §7damage. §dYour debt must now be paid.");
                            }
                            circle.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(ParticleEffect.SPELL_WITCH));
                            circle.setRadius(7.5);

                            timeLeftDebt = 6;
                        }

                        if (timeLeftDebt > 0) {

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(totemStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);
                            }

                            // 100% of damage over 6 seconds
                            float damage = (getDelayedDamage() * .1667f);

                            // Player damage
                            wp.addHealth(wp, "",
                                    (float) (damage * Math.pow(.8, wp.getCooldownManager().getCooldown(SpiritLink.class).size())),
                                    (float) (damage * Math.pow(.8, wp.getCooldownManager().getCooldown(SpiritLink.class).size())),
                                    critChance,
                                    critMultiplier,
                                    false);
                            // Teammate heal
                            PlayerFilter.entitiesAround(totemStand, 8, 7, 8)
                                    .aliveTeammatesOf(wp)
                                    .forEach((nearPlayer) -> {
                                        nearPlayer.addHealth(wp, name,
                                                damage * -.15f,
                                                damage * -.15f,
                                                critChance, critMultiplier, false);
                                    });
                        } else {
                            player.getWorld().spigot().strikeLightningEffect(totemStand.getLocation(), false);
                            // Enemy damage
                            PlayerFilter.entitiesAround(totemStand, 8, 7, 8)
                                    .aliveEnemiesOf(wp)
                                    .forEach((nearPlayer) -> {
                                        nearPlayer.addHealth(wp,
                                                name,
                                                getDelayedDamage() * .15f,
                                                getDelayedDamage() * .15f,
                                                critChance,
                                                critMultiplier, false);
                                    });
                            // 6 damage waves, stop the function
                            totemStand.remove();
                            this.cancel();
                            particles.cancel();
                            timer.cancel();
                        }
                    }
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(wp.isDeath()) {
                    totemStand.remove();
                    particles.cancel();
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    public float getDelayedDamage() {
        return delayedDamage;
    }

    public void addDelayedDamage(float delayedDamage) {
        this.delayedDamage += delayedDamage;
    }

}
