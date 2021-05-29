package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Totem extends EntityArmorStand {

    private WarlordsPlayer owner;
    private ArmorStand totemArmorStand;
    private int secondsLeft;
    private List<WarlordsPlayer> playersHit;

    public Totem(World world, WarlordsPlayer owner, ArmorStand totemArmorStand, int secondsLeft) {
        super(world);
        this.owner = owner;
        this.totemArmorStand = totemArmorStand;
        this.secondsLeft = secondsLeft;
        playersHit = new ArrayList<>();
    }

    public static double getLocationUnderPlayer(Player player) {
        Location location = player.getLocation().clone();
        if (player.getWorld().getHighestBlockYAt(location) < player.getLocation().getY()) {
            return player.getWorld().getHighestBlockYAt(location);
        } else {
            for (int i = 0; i < 20; i++) {
                if (player.getWorld().getBlockAt(location).getType() == Material.AIR) {
                    location.add(0, -1, 0);
                } else {
                    break;
                }
            }
            return location.getY();
        }
    }

    public WarlordsPlayer getOwner() {
        return owner;
    }

    public ArmorStand getTotemArmorStand() {
        return totemArmorStand;
    }

    public void setTotemArmorStand(ArmorStand totemArmorStand) {
        this.totemArmorStand = totemArmorStand;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<WarlordsPlayer> playersHit) {
        this.playersHit = playersHit;
    }

    public static class TotemThunderlord extends AbstractAbility {

        public TotemThunderlord() {
            super("Capacitor Totem", -404, -523, 60 + 2, 20, 20, 200,
                    "§7Place a highly conductive totem\n" +
                    "§7on the ground. Casting Chain Lightning\n" +
                    "§7or Lightning Rod on the totem will cause\n" +
                    "§7it to pulse, dealing §c404 §7- §c523 §7damage\n" +
                    "§7to all enemies nearby. Lasts §68 §7seconds.");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(Totem.getLocationUnderPlayer(player));
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setMarker(true);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 4));
            totemStand.setMetadata("Capacitor Totem - " + player.getName(), new FixedMetadataValue(Warlords.getInstance(), true));

            Totem capacitorTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 8);

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "TOTEM", capacitorTotem.getSecondsLeft()));

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (capacitorTotem.getSecondsLeft() == 0) {
                        capacitorTotem.getTotemArmorStand().remove();
                        this.cancel();
                    }
                    capacitorTotem.setSecondsLeft(capacitorTotem.getSecondsLeft() - 1);
                }

            }.runTaskTimer(Warlords.getInstance(), 0, 20);
        }
    }

    public static class TotemSpiritguard extends AbstractAbility {
        private float delayedDamage = 0;
        private int debt = 0;

        public TotemSpiritguard() {
            super("Death's Debt", 0, 0, 60 + 12, 20, -1, 100,
                    "§2Spirits’ Respite§7: Place down a totem that\n" +
                            "§7delays §c100% §7of incoming damage towards\n" +
                            "§7yourself. Transforms into §dDeath’s Debt §7after\n" +
                            "§64 §7- §68 §7seconds (increases with higher health),\n" +
                            "§7or when you exit its §e10 §7block radius.\n" +
                            "\n" +
                            "§dDeath’s Debt§7: Take §c100% §7of the damage delayed\n" +
                            "§7by §2Spirit's Respite §7over §66 §7seconds. The totem\n" +
                            "§7will heal nearby allies for §a15% §7of all damage\n" +
                            "§7that you take. If you survive, deal §c15% §7of the\n" +
                            "§7damage delayed to nearby enemies.");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(Totem.getLocationUnderPlayer(player));
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setMarker(true);
            totemStand.setHelmet(new ItemStack(Material.JUNGLE_FENCE_GATE));

            Totem deathsDebtTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 4 + (4 * (int) Math.round((double) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth())));

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "RESP", deathsDebtTotem.getSecondsLeft()));

            player.setMetadata("TOTEM", new FixedMetadataValue(Warlords.getInstance(), this));

            new BukkitRunnable() {

                @Override
                public void run() {
                    boolean isPlayerInRadius = player.getLocation().distanceSquared(standLocation) < 5 * 5;
                    int secondsLeft = Math.min(
                            deathsDebtTotem.getSecondsLeft() - 1,
                            isPlayerInRadius ? Integer.MAX_VALUE : 0
                    );
                    deathsDebtTotem.setSecondsLeft(secondsLeft);
                    if(secondsLeft > 0) {
                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(standLocation, "shaman.earthlivingweapon.impact", 1, 1.5F);
                        }

                        player.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + -Math.round(getDelayedDamage()) + " §7damage. §6" + secondsLeft + " §7seconds left.");
                        // TODO: render particles for the "colllecting damage" phase
                    } else {
                        if (secondsLeft == 0) {
                            warlordsPlayer.getActionBarStats().removeIf(actionBarStats -> actionBarStats.getName().equals("RESP"));
                            warlordsPlayer.getActionBarStats().add(new ActionBarStats(deathsDebtTotem.getOwner(), "DEBT", 6));
                            player.removeMetadata("TOTEM", Warlords.getInstance());

                            if(!isPlayerInRadius) {
                                player.sendMessage("§7You walked outside the radius");
                            } else {
                                player.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + -Math.round(getDelayedDamage()) + " §7damage. §dYour debt must now be paid.");
                            }

                            if (TotemSpiritguard.this.getDelayedDamage() == 0) {
                                this.cancel();

                            }
                        }

                        // TODO: render particles for the damage phase
                        // TODO: fix totem not disappearing sometimes?

                        int damageTick = -secondsLeft;
                        if(damageTick < 6) {

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(standLocation, "shaman.lightningbolt.impact", 1, 1.5F);
                            }

                            // 100% of damage over 6 seconds
                            int damage = (int) (TotemSpiritguard.this.getDelayedDamage() * .1667);
                            // Player damage
                            deathsDebtTotem.getOwner().addHealth(deathsDebtTotem.getOwner(), "",
                                    damage,
                                    damage,
                                    TotemSpiritguard.this.getCritChance(),
                                    TotemSpiritguard.this.getCritMultiplier()
                            );
                            // Teammate heal
                            List<Entity> near = deathsDebtTotem.getTotemArmorStand().getNearbyEntities(10.0D, 4.0D, 10.0D);
                            near = Utils.filterOnlyTeammates(near, deathsDebtTotem.getOwner().getPlayer());
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                        Warlords.getPlayer(nearPlayer).addHealth(deathsDebtTotem.getOwner(), deathsDebtTotem.getOwner().getSpec().getOrange().getName(),
                                                (int) (damage * -.15),
                                                (int) (damage * -.15),
                                                deathsDebtTotem.getOwner().getSpec().getOrange().getCritChance(), deathsDebtTotem.getOwner().getSpec().getOrange().getCritMultiplier());
                                    }
                                }
                            }
                        } else {
                            // Enemy damage
                            player.getWorld().spigot().strikeLightningEffect(standLocation, false);
                            List<Entity> near = deathsDebtTotem.getTotemArmorStand().getNearbyEntities(10.0D, 4.0D, 10.0D);
                            near = Utils.filterOutTeammates(near, deathsDebtTotem.getOwner().getPlayer());
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                        Warlords.getPlayer(nearPlayer).addHealth(deathsDebtTotem.getOwner(), deathsDebtTotem.getOwner().getSpec().getOrange().getName(),
                                                (int) (TotemSpiritguard.this.getDelayedDamage() * .15),
                                                (int) (TotemSpiritguard.this.getDelayedDamage() * .15),
                                                deathsDebtTotem.getOwner().getSpec().getOrange().getCritChance(), deathsDebtTotem.getOwner().getSpec().getOrange().getCritMultiplier());
                                    }
                                }
                            }
                            // 6 damage waves, stop the function
                            deathsDebtTotem.getTotemArmorStand().remove();
                            this.cancel();
                        }
                    }
                }

            }.runTaskTimer(Warlords.getInstance(), 0, 20);
        }

        public float getDelayedDamage() {
            return delayedDamage;
        }

        public void setDelayedDamage(float delayedDamage) {
            this.delayedDamage = delayedDamage;
        }

        public void addDelayedDamage(float delayedDamage) {
            this.delayedDamage += delayedDamage;
        }
    }

    public static class TotemEarthwarden extends AbstractAbility {

        public TotemEarthwarden() {
            super("Healing Totem", 168, 841, 60 + 12, 60, 15, 200,
                    "§7Place a totem on the ground that\n" +
                    "§7pulses constantly, healing nearby\n" +
                    "§7allies for §a168 §7- §a227 §7every\n" +
                    "§7second. Before disappearing, the totem\n" +
                    "§7will let out a final pulse that heals for\n" +
                    "§a841 §7- §a1138§7. Lasts §65 §7seconds.");

            //168 - 227
            //841 - 1138
            //1.35x
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(Totem.getLocationUnderPlayer(player));
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setMarker(true);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 7));

            Totem healingTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 5);

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "TOTEM", healingTotem.getSecondsLeft()));

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "shaman.totem.activation", 2, 1);
            }

            new BukkitRunnable() {

                @Override
                public void run() {

                    if (healingTotem.getSecondsLeft() != 0) {

                        Location particleLoc = standLocation.add(0, 1.5, 0);
                        ParticleEffect.VILLAGER_HAPPY.display(0.2F, 0.2F, 0.2F, 0.05F, 2, particleLoc, 500);

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                        }

                        List<Entity> near = healingTotem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                        near = Utils.filterOnlyTeammates(near, healingTotem.getOwner().getPlayer());
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player nearPlayer = (Player) entity;
                                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                    Warlords.getPlayer(nearPlayer).addHealth(healingTotem.getOwner(),
                                            healingTotem.getOwner().getSpec().getOrange().getName(),
                                            healingTotem.getOwner().getSpec().getOrange().getMinDamageHeal(),
                                            (int) (healingTotem.getOwner().getSpec().getOrange().getMinDamageHeal() * 1.35),
                                            healingTotem.getOwner().getSpec().getOrange().getCritChance(),
                                            healingTotem.getOwner().getSpec().getOrange().getCritMultiplier());
                                }
                            }
                        }
                    } else {

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.heal.impact", 2, 1);
                        }

                        List<Entity> near = healingTotem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                        near = Utils.filterOnlyTeammates(near, healingTotem.getOwner().getPlayer());
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player nearPlayer = (Player) entity;
                                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                    Warlords.getPlayer(nearPlayer).addHealth(healingTotem.getOwner(),
                                            healingTotem.getOwner().getSpec().getOrange().getName(),
                                            healingTotem.getOwner().getSpec().getOrange().getMaxDamageHeal(),
                                            (int) (healingTotem.getOwner().getSpec().getOrange().getMaxDamageHeal() * 1.35),
                                            healingTotem.getOwner().getSpec().getOrange().getCritChance(),
                                            healingTotem.getOwner().getSpec().getOrange().getCritMultiplier());
                                }
                            }
                        }
                        healingTotem.getTotemArmorStand().remove();
                        this.cancel();
                    }
                    healingTotem.setSecondsLeft(healingTotem.getSecondsLeft() - 1);
                }

            }.runTaskTimer(Warlords.getInstance(), 0, 20);
        }
    }
}
