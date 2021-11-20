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
import org.bukkit.ChatColor;
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

    public DeathsDebt() {
        super("Death's Debt", 0, 0, 60f + 10.49f, 20, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§2Spirits’ Respite§7: Place down a totem that\n" +
                "§7delays §c100% §7of incoming damage towards\n" +
                "§7yourself. Transforms into §dDeath’s Debt §7after\n" +
                "§64 §7- §66 §7seconds (increases with higher health),\n" +
                "§7or when you exit its §e10 §7block radius.\n" +
                "\n" +
                "§dDeath’s Debt§7: Take §c100% §7of the damage delayed\n" +
                "§7by §2Spirit's Respite §7over §66 §7seconds. The totem\n" +
                "§7will heal nearby allies for §a15% §7of all damage\n" +
                "§7that you take. If you survive, deal §c15% §7of the" +
                "\n\n" +
                "§7Successful Soulbind procs on enemies add §60.5 §7seconds\n" +
                "§7to your totem duration. (cap of §66 §7seconds)";
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

        DeathsDebt tempDeathsDebt = new DeathsDebt();

        wp.getCooldownManager().addCooldown("Spirits Respite", this.getClass(), tempDeathsDebt, "RESP", secondsLeft, wp, CooldownTypes.ABILITY);

        player.setMetadata("TOTEM", new FixedMetadataValue(Warlords.getInstance(), tempDeathsDebt));

        CircleEffect circle = new CircleEffect(wp, totemStand.getLocation().clone().add(0, 1.25, 0), 10);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL));
        circle.addEffect(new DoubleLineEffect(ParticleEffect.REDSTONE));
        BukkitTask particles = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circle::playEffects, 0, 1);

        tempDeathsDebt.setTimeLeftRespite(secondsLeft);

        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        if (wp.isDeath() || !wp.getCooldownManager().hasCooldown(tempDeathsDebt)) {
                            totemStand.remove();
                            particles.cancel();
                            this.cancel();
                        } else {
                            if (player.getWorld() != totemStand.getWorld()) {
                                totemStand.remove();
                                particles.cancel();
                                this.cancel();
                                return;
                            }
                            boolean isPlayerInRadius = player.getLocation().distanceSquared(totemStand.getLocation()) < 10 * 10;
                            if (!isPlayerInRadius && tempDeathsDebt.getTimeLeftRespite() != -1) {
                                tempDeathsDebt.setTimeLeftRespite(0);
                            }

                            int roundedTimeLeftRespite = (int) Math.round(tempDeathsDebt.getTimeLeftRespite());
                            int roundedTimeLeftDebt = (int) Math.round(tempDeathsDebt.getTimeLeftDebt());
                            //every second
                            if (counter % 20 == 0) {
                                if (roundedTimeLeftRespite > 0) {
                                    //respite
                                    for (Player player1 : player.getWorld().getPlayers()) {
                                        player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                                    }
                                    player.sendMessage(ChatColor.GREEN + "\u00BB §2Spirit's Respite §7delayed §c" + Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §6" + roundedTimeLeftRespite + " §7seconds left.");
                                } else if (roundedTimeLeftRespite == 0) {
                                    //beginning debt
                                    wp.getCooldownManager().removeCooldown(tempDeathsDebt);
                                    wp.getCooldownManager().addCooldown(name, this.getClass(), tempDeathsDebt, "DEBT", 6, wp, CooldownTypes.ABILITY);

                                    player.removeMetadata("TOTEM", Warlords.getInstance());
                                    if (!isPlayerInRadius) {
                                        player.sendMessage("§7You walked outside your §dDeath's Debt §7radius");
                                    } else {
                                        player.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §dYour debt must now be paid.");
                                    }
                                    circle.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(ParticleEffect.SPELL_WITCH));
                                    circle.setRadius(7.5);

                                    //blue to purple totem
                                    totemStand.setHelmet(new ItemStack(Material.DARK_OAK_FENCE_GATE));

                                    //first dmg tick
                                    onDebtTick(wp, player, totemStand, tempDeathsDebt);
                                    //cancel respite and initiate debt
                                    tempDeathsDebt.setTimeLeftRespite(-1);
                                    tempDeathsDebt.setTimeLeftDebt(6);
                                } else {
                                    //during debt
                                    if (roundedTimeLeftDebt > 0) {
                                        //5 dmg procs
                                        onDebtTick(wp, player, totemStand, tempDeathsDebt);
                                    } else {
                                        //final damage tick
                                        player.getWorld().spigot().strikeLightningEffect(totemStand.getLocation(), false);
                                        // Enemy damage
                                        PlayerFilter.entitiesAround(totemStand, 8, 7, 8)
                                                .aliveEnemiesOf(wp)
                                                .forEach((nearPlayer) -> {
                                                    nearPlayer.damageHealth(wp,
                                                            name,
                                                            tempDeathsDebt.getDelayedDamage() * .15f,
                                                            tempDeathsDebt.getDelayedDamage() * .15f,
                                                            critChance,
                                                            critMultiplier, false);
                                                });
                                        // 6 damage waves, stop the function
                                        totemStand.remove();
                                        particles.cancel();
                                        this.cancel();
                                    }
                                }
                            }
                            //counters
                            if (tempDeathsDebt.getTimeLeftRespite() > 0) {
                                tempDeathsDebt.setTimeLeftRespite(tempDeathsDebt.getTimeLeftRespite() - .05);
                            }
                            if (tempDeathsDebt.getTimeLeftDebt() > 0) {
                                tempDeathsDebt.setTimeLeftDebt(tempDeathsDebt.getTimeLeftDebt() - .05);
                            }
                            counter++;
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 0),
                System.currentTimeMillis()
        );
    }

    public void onDebtTick(WarlordsPlayer wp, Player player, ArmorStand totemStand, DeathsDebt tempDeathsDebt) {
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(totemStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);
        }
        // 100% of damage over 6 seconds
        float damage = (tempDeathsDebt.getDelayedDamage() * .1667f);
        // Player damage
        wp.damageHealth(wp, "",
                (float) (damage * Math.pow(.8, wp.getCooldownManager().getCooldown(SpiritLink.class).size())),
                (float) (damage * Math.pow(.8, wp.getCooldownManager().getCooldown(SpiritLink.class).size())),
                critChance,
                critMultiplier,
                false);
        // Teammate heal
        PlayerFilter.entitiesAround(totemStand, 8, 7, 8)
                .aliveTeammatesOf(wp)
                .forEach((nearPlayer) -> {
                    nearPlayer.healHealth(wp, name,
                            damage * .15f,
                            damage * .15f,
                            critChance, critMultiplier, false);
                });
    }

    public float getDelayedDamage() {
        return delayedDamage;
    }

    public void addDelayedDamage(float delayedDamage) {
        this.delayedDamage += delayedDamage;
    }

    public double getTimeLeftRespite() {
        return timeLeftRespite;
    }

    public void setTimeLeftRespite(double timeLeftRespite) {
        if(timeLeftRespite > 6) {
            this.timeLeftRespite = 6;
        } else {
            this.timeLeftRespite = timeLeftRespite;
        }
    }

    public double getTimeLeftDebt() {
        return timeLeftDebt;
    }

    public void setTimeLeftDebt(double timeLeftDebt) {
        this.timeLeftDebt = timeLeftDebt;
    }
}
