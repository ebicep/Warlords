package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Chain extends AbstractAbility {

    public Chain(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        Classes selected = Classes.getSelected(player);
        if (selected == Classes.THUNDERLORD) {
            description = "§7Discharge a bolt of lightning at the\n" +
                    "§7targeted enemy player that deals\n" +
                    "§c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage and jumps to\n" +
                    "§e4 §7additional targets within §e15\n" +
                    "§7blocks. Each time the lightning jumps\n" +
                    "§7the damage is decreased by §c15%§7.\n" +
                    "§7You gain §e10% §7damage resistance for\n" +
                    "§7each target hit, up to §e30% §7damage\n" +
                    "§7resistance. This buff lasts §64.5 §7seconds.";
        } else if (selected == Classes.EARTHWARDEN) {
            description = "§7Discharge a beam of energizing lightning\n" +
                    "§7that heals you and a targeted friendly\n" +
                    "§7player for §a" + minDamageHeal + " §7- §a" + maxDamageHeal + " §7health and\n" +
                    "§7jumps to §e2 §7additional targets within\n" +
                    "§e10 §7blocks." +
                    "\n\n" +
                    "§7Each ally healed reduces the cooldown of\n" +
                    "§7Boulder by §62 §7seconds.";
        } else if (selected == Classes.SPIRITGUARD) {
            description = "§7Links your spirit with up to §c3 §7enemy\n" +
                    "§7players, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7to the first target hit. Each additional hit\n" +
                    "§7deals §c10% §7reduced damage. You gain §e40%\n" +
                    "§7speed for §61.5 §7seconds, and take §c20%\n" +
                    "§7reduced damage for §64.5 §7seconds.";
        }
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        //TODO add Your enemy is too far away!

        int totemEffectRadius = 5;
        /* CHAINS
           TOTEM -> PLAYER -> PLAYER
           PLAYER -> TOTEM -> PLAYER
           PLAYER -> PLAYER -> TOTEM
           PLAYER -> PLAYER -> PLAYER
         */
        int hitCounter = 0;
        if (name.contains("Lightning")) {
            // TOTEM -> PLAYER -> PLAYER
            if (lookingAtTotem(player)) {
                // (TOTEM) -> PLAYER -> PLAYER
                System.out.println("(TOTEM) -> PLAYER -> PLAYER");
                ArmorStand totem = getTotem(player);
                chain(player.getLocation(), totem.getLocation().add(0, .5, 0));
                List<Entity> nearTotem = totem.getNearbyEntities(4.0D, 4.0D, 4.0D);
                nearTotem = Utils.filterOutTeammates(nearTotem, player);
                pulseDamage(warlordsPlayer, nearTotem);
                hitCounter++;
                if (getTotem(player) != null) {
                    new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                    }
                }
                // TOTEM -> (PLAYER) -> PLAYER
                List<Entity> near = totem.getNearbyEntities(20.0D, 18.0D, 20.0D);
                near = Utils.filterOutTeammates(near, player);
                //TODO maybe fix this, may be performance heavy - getNearbyEntities is not in order of closest
                System.out.println(near);
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        System.out.println("TOTEM -> (PLAYER) -> PLAYER");
                        Player nearPlayer = (Player) entity;
                        if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                            chain(totem.getLocation().add(0, .5, 0), nearPlayer.getLocation());
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            hitCounter++;
                            if (getTotem(player) != null) {
                                new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                                for (Player player1 : player.getWorld().getPlayers()) {
                                    player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                                }
                            }
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                            nearNearPlayers.remove(nearPlayer);
                            nearNearPlayers = Utils.filterOutTeammates(nearNearPlayers, player);
                            // TOTEM -> PLAYER -> (PLAYER)
                            for (Entity entity1 : nearNearPlayers) {
                                if (entity1 instanceof Player) {
                                    System.out.println("TOTEM -> PLAYER -> (PLAYER)");
                                    Player nearNearPlayer = (Player) entity1;
                                    if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                        chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                                        Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .85f), (maxDamageHeal * .85f), critChance, critMultiplier);

                                        hitCounter++;
                                        if (getTotem(player) != null) {
                                            new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                                            for (Player player1 : player.getWorld().getPlayers()) {
                                                player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                                            }
                                        }
                                        List<Entity> nearNearNearPlayers = nearNearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                                        nearNearNearPlayers.remove(nearPlayer);
                                        nearNearNearPlayers.remove(nearNearPlayer);
                                        nearNearNearPlayers = Utils.filterOutTeammates(nearNearNearPlayers, player);
                                        for (Entity entity2 : nearNearNearPlayers) {
                                            if (entity2 instanceof Player) {
                                                Player nearNearNearPlayer = (Player) entity2;
                                                if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                    chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                                                    Warlords.getPlayer(nearNearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .7f), (maxDamageHeal * .7f), critChance, critMultiplier);
                                                    hitCounter++;
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                List<Entity> near = player.getNearbyEntities(20.0D, 18.0D, 20.0D);
                near = Utils.filterOutTeammates(near, player);
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        // (PLAYER) -> TOTEM -> PLAYER
                        Player nearPlayer = (Player) entity;
                        if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAtChain(player, nearPlayer) && Utils.hasLineOfSight(player, nearPlayer)) {
                            System.out.println("(PLAYER) -> TOTEM -> PLAYER");
                            chain(player.getLocation(), nearPlayer.getLocation());
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            hitCounter++;
                            if (getTotem(player) != null) {
                                new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                                for (Player player1 : player.getWorld().getPlayers()) {
                                    player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                                }
                            }
                            List<Entity> nearNearEntities = nearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                            nearNearEntities.sort(new Utils.ArmorStandComparator());
                            nearNearEntities.remove(nearPlayer);
                            nearNearEntities = Utils.filterOutTeammates(nearNearEntities, player);
                            for (Entity entity1 : nearNearEntities) {
                                if (Utils.totemDownAndClose(warlordsPlayer, nearPlayer)) {
                                    // PLAYER -> (TOTEM) -> PLAYER   THIS IS SO TRASH

                                    System.out.println("PLAYER -> (TOTEM) -> PLAYER");
                                    ArmorStand totem = getTotem(player);
                                    chain(nearPlayer.getLocation(), totem.getLocation().add(0, .5, 0));
                                    List<Entity> totemNear = totem.getNearbyEntities(4.0D, 4.0D, 4.0D);
                                    totemNear = Utils.filterOutTeammates(totemNear, player);
                                    pulseDamage(warlordsPlayer, totemNear);
                                    hitCounter++;
                                    List<Entity> nearNearNearPlayers = totem.getNearbyEntities(10.0D, 9.0D, 10.0D);
                                    nearNearNearPlayers.remove(nearPlayer);
                                    nearNearNearPlayers = Utils.filterOutTeammates(nearNearNearPlayers, player);
                                    for (Entity entity2 : nearNearNearPlayers) {
                                        if (entity2 instanceof Player) {
                                            // PLAYER -> TOTEM -> (PLAYER)
                                            Player nearNearNearPlayer = (Player) entity2;
                                            if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                System.out.println("PLAYER -> TOTEM -> (PLAYER)");
                                                chain(totem.getLocation().add(0, .5, 0), nearNearNearPlayer.getLocation());
                                                Warlords.getPlayer(nearNearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .7f), (maxDamageHeal * .7f), critChance, critMultiplier);
                                                hitCounter++;
                                                if (getTotem(player) != null) {
                                                    new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                                                    for (Player player1 : player.getWorld().getPlayers()) {
                                                        player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }

                                    if (hitCounter > 1) {
                                        break;
                                    }
                                } else if (entity1 instanceof Player) {
                                    System.out.println("PLAYER -> (PLAYER) -> TOTEM/PLAYER");
                                    // PLAYER -> (PLAYER) -> TOTEM/PLAYER
                                    Player nearNearPlayer = (Player) entity1;
                                    if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                        System.out.println("PLAYER -> (PLAYER) -> TOTEM/PLAYER");
                                        chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                                        Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .85f), (maxDamageHeal * .85f), critChance, critMultiplier);
                                        hitCounter++;
                                        List<Entity> nearNearNearEntities = nearNearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                                        nearNearNearEntities.sort(new Utils.ArmorStandComparator());
                                        nearNearNearEntities.remove(nearPlayer);
                                        nearNearNearEntities.remove(nearNearPlayer);
                                        nearNearNearEntities = Utils.filterOutTeammates(nearNearNearEntities, player);
                                        for (Entity entity2 : nearNearNearEntities) {
                                            if (Utils.totemDownAndClose(warlordsPlayer, nearNearPlayer)) {
                                                // PLAYER -> PLAYER -> (TOTEM)

                                                System.out.println("PLAYER -> PLAYER -> (TOTEM)");
                                                ArmorStand totem = getTotem(player);

                                                chain(nearNearPlayer.getLocation(), totem.getLocation().add(0, .5, 0));
                                                List<Entity> totemNear = totem.getNearbyEntities(4.0D, 4.0D, 4.0D);
                                                totemNear = Utils.filterOutTeammates(totemNear, player);
                                                pulseDamage(warlordsPlayer, totemNear);
                                                hitCounter++;
                                                if (getTotem(player) != null) {
                                                    new FallingBlockWaveEffect(getTotem(player).getLocation().clone().add(0, 1, 0), totemEffectRadius, 1.2, Material.SAPLING, (byte) 0).play();
                                                    for (Player player1 : player.getWorld().getPlayers()) {
                                                        player1.playSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                                                    }
                                                }

                                                break;
                                            } else if (entity2 instanceof Player) {
                                                // PLAYER -> PLAYER -> (PLAYER)
                                                Player nearNearNearPlayer = (Player) entity2;
                                                if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                    System.out.println("PLAYER -> PLAYER -> (PLAYER)");
                                                    chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                                                    Warlords.getPlayer(nearNearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .7f), (maxDamageHeal * .7f), critChance, critMultiplier);
                                                    hitCounter++;
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (name.contains("Heal")) {
            List<Entity> near = player.getNearbyEntities(10.0D, 9.0D, 10.0D);
            near = Utils.filterOnlyTeammates(near, player);
            for (Entity entity : near) {
                if (entity instanceof Player) {
                    Player nearPlayer = (Player) entity;
                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAtChain(player, nearPlayer) && Utils.hasLineOfSight(player, nearPlayer)) {
                        //self heal
                        warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        chain(player.getLocation(), nearPlayer.getLocation());
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        hitCounter++;

                        List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 4.0D, 5.0D);
                        nearNearPlayers.remove(player);
                        nearNearPlayers.remove(nearPlayer);
                        nearNearPlayers = Utils.filterOnlyTeammates(nearNearPlayers, player);

                        for (Entity entity1 : nearNearPlayers) {
                            if (entity1 instanceof Player) {
                                Player nearNearPlayer = (Player) entity1;
                                if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                    chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                                    Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .9f), (maxDamageHeal * .9f), critChance, critMultiplier);
                                    hitCounter++;

                                    List<Entity> nearNearNearPlayers = nearNearPlayer.getNearbyEntities(5.0D, 4.0D, 5.0D);
                                    nearNearNearPlayers.remove(player);
                                    nearNearNearPlayers.remove(nearPlayer);
                                    nearNearNearPlayers.remove(nearNearPlayer);
                                    nearNearNearPlayers = Utils.filterOnlyTeammates(nearNearNearPlayers, player);

                                    for (Entity entity2 : nearNearNearPlayers) {
                                        if (entity2 instanceof Player) {
                                            Player nearNearNearPlayer = (Player) entity2;
                                            if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                                                Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .8f), (maxDamageHeal * .8f), critChance, critMultiplier);
                                                hitCounter++;
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } else if (name.contains("Spirit")) {
            List<Entity> near = player.getNearbyEntities(15.0D, 13.0D, 15.0D);
            near = Utils.filterOutTeammates(near, player);
            for (Entity entity : near) {
                if (entity instanceof Player) {
                    Player nearPlayer = (Player) entity;
                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAtChain(player, nearPlayer) && Utils.hasLineOfSight(player, nearPlayer)) {
                        chain(player.getLocation(), nearPlayer.getLocation());
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        hitCounter++;

                        if (warlordsPlayer.hasBoundPlayerLink(Warlords.getPlayer(nearPlayer))) {
                            healNearPlayers(player);
                        }

                        List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                        nearNearPlayers.remove(nearPlayer);
                        nearNearPlayers = Utils.filterOutTeammates(nearNearPlayers, player);

                        for (Entity entity1 : nearNearPlayers) {
                            if (entity1 instanceof Player) {
                                Player nearNearPlayer = (Player) entity1;
                                if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                    chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                                    Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .8f), (maxDamageHeal * .8f), critChance, critMultiplier);
                                    hitCounter++;

                                    if (warlordsPlayer.hasBoundPlayerLink(Warlords.getPlayer(nearNearPlayer))) {
                                        healNearPlayers(player);
                                    }

                                    List<Entity> nearNearNearPlayers = nearNearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                                    nearNearNearPlayers.remove(nearPlayer);
                                    nearNearNearPlayers.remove(nearNearPlayer);
                                    nearNearNearPlayers = Utils.filterOutTeammates(nearNearNearPlayers, player);

                                    for (Entity entity2 : nearNearNearPlayers) {
                                        if (entity2 instanceof Player) {
                                            Player nearNearNearPlayer = (Player) entity2;
                                            if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                                                Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * .6f), (maxDamageHeal * .6f), critChance, critMultiplier);
                                                hitCounter++;

                                                if (warlordsPlayer.hasBoundPlayerLink(Warlords.getPlayer(nearNearNearPlayer))) {
                                                    healNearPlayers(player);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (hitCounter != 0) {
            PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
            warlordsPlayer.subtractEnergy(energyCost);
            if (name.contains("Lightning")) {
                warlordsPlayer.getCooldownManager().addCooldown(Chain.this.getClass(), "CHAIN(" + hitCounter + ")", 4, warlordsPlayer, CooldownTypes.BUFF);

                warlordsPlayer.getSpec().getRed().setCurrentCooldown(cooldown);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainlightning.activation", 2, 1);
                }
                player.playSound(player.getLocation(), "shaman.chainlightning.impact", 1F, 1);

            } else if (name.contains("Heal")) {
                if (hitCounter * 2 > warlordsPlayer.getSpec().getRed().getCurrentCooldown()) {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(0);
                } else {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - hitCounter * 2);
                }
                warlordsPlayer.updateRedItem();
                warlordsPlayer.getSpec().getBlue().setCurrentCooldown(cooldown);
                warlordsPlayer.updateBlueItem();

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainheal.activation", 2, 1);
                }
            } else if (name.contains("Spirit")) {
                // speed buff
                warlordsPlayer.getSpeed().changeCurrentSpeed("Spirit Link", 40, 30); // 30 is ticks
                warlordsPlayer.getCooldownManager().addCooldown(Chain.this.getClass(), "LINK", 4.5f, warlordsPlayer, CooldownTypes.BUFF);

                warlordsPlayer.getSpec().getRed().setCurrentCooldown(cooldown);

                player.playSound(player.getLocation(), "mage.firebreath.activation", 1.5F, 1);
            }
        }

    }

    private void pulseDamage(WarlordsPlayer warlordsPlayer, List<Entity> near) {
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            }
        }
    }

    private void healNearPlayers(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100);
        int playersHealed = 0;
        List<Entity> near = player.getNearbyEntities(2.5D, 2D, 2.5D);
        near = Utils.filterOnlyTeammates(near, player);
        near.remove(player);
        for (Entity entity : near) {
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                Player nearPlayer = (Player) entity;
                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100);
                playersHealed++;
                if (playersHealed == 2) break;
            }
        }
    }

    private void chain(Location from, Location to) {
        Location location = from.subtract(0, .5, 0);
        location.setDirection(location.toVector().subtract(to.subtract(0, .5, 0).toVector()).multiply(-1));
        spawnChain((int) Math.round(from.distance(to)), location);
    }

    private void spawnChain(int distance, Location location) {

        List<ArmorStand> chains = new ArrayList<>();

        if (name.contains("Lightning")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.RED_MUSHROOM));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        } else if (name.contains("Heal")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 1));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        } else {//if (name.contains("Spirit")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.SPRUCE_FENCE_GATE));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.size() == 0) {
                    this.cancel();
                }

                for (int i = 0; i < chains.size(); i++) {
                    ArmorStand armorStand = chains.get(i);
                    if (armorStand.getTicksLived() > 12) {
                        armorStand.remove();
                        chains.remove(i);
                        i--;
                    }
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    private boolean lookingAtTotem(Player player) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + .5);
        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
                Vector toEntity = ((ArmorStand) entity).getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
                float dot = (float) toEntity.normalize().dot(eye.getDirection());
                return dot > .95f;
            }
        }
        return false;
    }

    private ArmorStand getTotem(Player player) {
        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }

}
