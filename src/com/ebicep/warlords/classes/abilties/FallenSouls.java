package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FallenSouls extends AbstractAbility {

    private List<FallenSoul> fallenSouls = new ArrayList<>();
    private static final float fallenSoulHitBox = .9f;
    private static final float fallenSoulSpeed = 2.0f;

    public FallenSouls() {
        super("Fallen Souls", -164, -212, 0, 55, 20, 180,
                "§7Summon a wave of fallen souls, dealing\n" +
                        "§c164 §7- §c212 §7damage to all enemies they\n" +
                        "§7pass through. Each target hit reduces the\n" +
                        "§7cooldown of Spirit Link by §62 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        ArmorStand fallenSoulLeft = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationLeft = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        //TODO fix spread, gets shorter the higher the pitch
        locationLeft.setYaw(location.getYaw() - 15);// - (int)(location.getPitch()/-10f * 1.6));
        location.add(0, .5, 0);
        ArmorStand fallenSoulMiddle = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationMiddle = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        locationMiddle.setYaw(location.getYaw() - 0);
        location.add(0, .5, 0);
        ArmorStand fallenSoulRight = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationRight = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        locationRight.setYaw(location.getYaw() + 15);// + (int)(location.getPitch()/-10f * 1.6));
        location.add(0, .5, 0);

        FallenSoul fallenSoul = new FallenSoul(Warlords.getPlayer(player), fallenSoulLeft, fallenSoulMiddle, fallenSoulRight, player.getLocation(), player.getLocation(), player.getLocation(), locationLeft.getDirection(), locationMiddle.getDirection(), locationRight.getDirection(), this);

        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningbolt.impact", 2, 1.5f);
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                if (fallenSoul.isLeftRemoved() && fallenSoul.isMiddleRemoved() && fallenSoul.isRightRemoved()) {
                    this.cancel();
                }

                ArmorStand leftSoul = fallenSoul.getFallenSoulLeft();
                ArmorStand middleSoul = fallenSoul.getFallenSoulMiddle();
                ArmorStand rightSoul = fallenSoul.getFallenSoulRight();

                leftSoul.teleport(leftSoul.getLocation().add(fallenSoul.getDirectionLeft().clone().multiply(fallenSoulSpeed)));
                middleSoul.teleport(middleSoul.getLocation().add(fallenSoul.getDirectionMiddle().clone().multiply(fallenSoulSpeed)));
                rightSoul.teleport(rightSoul.getLocation().add(fallenSoul.getDirectionRight().clone().multiply(fallenSoulSpeed)));

                List<Entity> nearLeft = (List<Entity>) leftSoul.getWorld().getNearbyEntities(leftSoul.getLocation().clone().add(0, 2, 0), fallenSoulHitBox, 0, fallenSoulHitBox);
                List<Entity> nearMiddle = (List<Entity>) middleSoul.getWorld().getNearbyEntities(middleSoul.getLocation().clone().add(0, 2, 0), fallenSoulHitBox, 0, fallenSoulHitBox);
                List<Entity> nearRight = (List<Entity>) rightSoul.getWorld().getNearbyEntities(rightSoul.getLocation().clone().add(0, 2, 0), fallenSoulHitBox, 0, fallenSoulHitBox);

                damageNearByPlayers(nearLeft, player, fallenSoul);
                damageNearByPlayers(nearMiddle, player, fallenSoul);
                damageNearByPlayers(nearRight, player, fallenSoul);

                ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1F, 1, leftSoul.getLocation().add(0, 2, 0), 500);
                ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1F, 1, middleSoul.getLocation().add(0, 2, 0), 500);
                ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1F, 1, rightSoul.getLocation().add(0, 2, 0), 500);

                if (!fallenSoul.isLeftRemoved() && leftSoul.getLocation().getWorld().getBlockAt(leftSoul.getLocation().clone().add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulLeft().getTicksLived() > 50 / fallenSoulSpeed * 1.2) {
                    ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, leftSoul.getLocation().add(0, 1, 0), 500);
                    fallenSoul.getFallenSoulLeft().remove();
                    fallenSoul.setLeftRemoved(true);
                }

                if (!fallenSoul.isMiddleRemoved() && middleSoul.getLocation().getWorld().getBlockAt(middleSoul.getLocation().clone().add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulMiddle().getTicksLived() > 50 / fallenSoulSpeed * 1.2) {
                    ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, middleSoul.getLocation().add(0, 1, 0), 500);
                    fallenSoul.getFallenSoulMiddle().remove();
                    fallenSoul.setMiddleRemoved(true);
                }

                if (!fallenSoul.isRightRemoved() && rightSoul.getLocation().getWorld().getBlockAt(rightSoul.getLocation().clone().add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulRight().getTicksLived() > 50 / fallenSoulSpeed * 1.2) {
                    ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, rightSoul.getLocation().add(0, 1, 0), 500);
                    fallenSoul.getFallenSoulRight().remove();
                    fallenSoul.setRightRemoved(true);
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    public void damageNearByPlayers(List<Entity> near, Player player, FallenSoul fallenSoul) {
        for (Entity entity : near) {
            if (entity instanceof Player) {
                WarlordsPlayer warlordsPlayer = Warlords.getPlayer((Player) entity);
                if (!fallenSoul.getPlayersHit().contains(warlordsPlayer) && warlordsPlayer.getPlayer().getGameMode() != GameMode.SPECTATOR && !Warlords.game.onSameTeam((Player) entity, player)) {
                    warlordsPlayer.addHealth(fallenSoul.getShooter(), fallenSoul.getFallenSouls().getName(), fallenSoul.getFallenSouls().getMinDamageHeal(), fallenSoul.getFallenSouls().getMaxDamageHeal(), fallenSoul.getFallenSouls().getCritChance(), fallenSoul.getFallenSouls().getCritMultiplier());
                    fallenSoul.getPlayersHit().add(warlordsPlayer);
                    fallenSoul.getShooter().getSpec().getRed().subtractCooldown(2);
                    fallenSoul.getShooter().updateRedItem();
                    if (fallenSoul.getShooter().getSoulBindCooldown() != 0 && fallenSoul.getShooter().hasBoundPlayerSoul(warlordsPlayer)) {
                        fallenSoul.getShooter().getSpec().getRed().subtractCooldown(1.5F);
                        fallenSoul.getShooter().getSpec().getPurple().subtractCooldown(1.5F);
                        fallenSoul.getShooter().getSpec().getBlue().subtractCooldown(1.5F);
                        fallenSoul.getShooter().getSpec().getOrange().subtractCooldown(1.5F);

                        fallenSoul.getShooter().updateRedItem();
                        fallenSoul.getShooter().updatePurpleItem();
                        fallenSoul.getShooter().updateBlueItem();
                        fallenSoul.getShooter().updateOrangeItem();

                        List<Entity> teammatesNear = player.getNearbyEntities(2, 2, 2);
                        teammatesNear = Utils.filterOnlyTeammates(teammatesNear, player);
                        int counter = 0;
                        for (Entity entity1 : teammatesNear) {
                            if (entity1 instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                WarlordsPlayer warlordsPlayer1 = Warlords.getPlayer((Player) entity1);
                                warlordsPlayer1.getSpec().getRed().subtractCooldown(.5F);
                                warlordsPlayer1.getSpec().getPurple().subtractCooldown(.5F);
                                warlordsPlayer1.getSpec().getBlue().subtractCooldown(.5F);
                                warlordsPlayer1.getSpec().getOrange().subtractCooldown(.5F);

                                warlordsPlayer1.updateRedItem();
                                warlordsPlayer1.updatePurpleItem();
                                warlordsPlayer1.updateBlueItem();
                                warlordsPlayer1.updateOrangeItem();

                                counter++;
                                if (counter == 2) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static class FallenSoul {

        private WarlordsPlayer shooter;
        private ArmorStand fallenSoulLeft;
        private ArmorStand fallenSoulMiddle;
        private ArmorStand fallenSoulRight;
        private Location locationLeft;
        private Location locationMiddle;
        private Location locationRight;
        private Vector directionLeft;
        private Vector directionMiddle;
        private Vector directionRight;
        private boolean leftRemoved;
        private boolean middleRemoved;
        private boolean rightRemoved;
        private FallenSouls fallenSouls;
        private List<WarlordsPlayer> playersHit;

        public FallenSoul(WarlordsPlayer shooter, ArmorStand fallenSoulLeft, ArmorStand fallenSoulMiddle, ArmorStand fallenSoulRight, Location locationLeft, Location locationMiddle, Location locationRight, Vector directionLeft, Vector directionMiddle, Vector directionRight, FallenSouls fallenSouls) {
            this.shooter = shooter;
            this.fallenSoulLeft = fallenSoulLeft;
            fallenSoulLeft.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulLeft.setGravity(false);
            fallenSoulLeft.setVisible(false);
            fallenSoulLeft.setMarker(true);
            fallenSoulLeft.setHeadPose(new EulerAngle(directionLeft.getY() * -fallenSoulSpeed, 0, 0));
            //fallenSoulLeft.setHeadPose(new EulerAngle(directionLeft.getY() * -fallenSoulSpeed, -.25, 0));
            this.fallenSoulMiddle = fallenSoulMiddle;
            fallenSoulMiddle.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulMiddle.setGravity(false);
            fallenSoulMiddle.setVisible(false);
            fallenSoulMiddle.setMarker(true);
            fallenSoulMiddle.setHeadPose(new EulerAngle(directionMiddle.getY() * -fallenSoulSpeed, 0, 0));
            this.fallenSoulRight = fallenSoulRight;
            fallenSoulRight.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulRight.setGravity(false);
            fallenSoulRight.setVisible(false);
            fallenSoulRight.setMarker(true);
            fallenSoulRight.setHeadPose(new EulerAngle(directionRight.getY() * -fallenSoulSpeed, 0, 0));
            //fallenSoulRight.setHeadPose(new EulerAngle(directionRight.getY() * -fallenSoulSpeed, .25, 0));
            this.locationLeft = locationLeft;
            this.locationMiddle = locationMiddle;
            this.locationRight = locationRight;
            this.directionLeft = directionLeft;
            this.directionMiddle = directionMiddle;
            this.directionRight = directionRight;
            this.fallenSouls = fallenSouls;
            leftRemoved = false;
            middleRemoved = false;
            rightRemoved = false;
            playersHit = new ArrayList<>();
            playersHit.add(shooter);
        }

        public WarlordsPlayer getShooter() {
            return shooter;
        }

        public void setShooter(WarlordsPlayer shooter) {
            this.shooter = shooter;
        }

        public ArmorStand getFallenSoulLeft() {
            return fallenSoulLeft;
        }

        public void setFallenSoulLeft(ArmorStand fallenSoulLeft) {
            this.fallenSoulLeft = fallenSoulLeft;
        }

        public ArmorStand getFallenSoulMiddle() {
            return fallenSoulMiddle;
        }

        public void setFallenSoulMiddle(ArmorStand fallenSoulMiddle) {
            this.fallenSoulMiddle = fallenSoulMiddle;
        }

        public ArmorStand getFallenSoulRight() {
            return fallenSoulRight;
        }

        public void setFallenSoulRight(ArmorStand fallenSoulRight) {
            this.fallenSoulRight = fallenSoulRight;
        }

        public Location getLocationLeft() {
            return locationLeft;
        }

        public void setLocationLeft(Location locationLeft) {
            this.locationLeft = locationLeft;
        }

        public Location getLocationMiddle() {
            return locationMiddle;
        }

        public void setLocationMiddle(Location locationMiddle) {
            this.locationMiddle = locationMiddle;
        }

        public Location getLocationRight() {
            return locationRight;
        }

        public void setLocationRight(Location locationRight) {
            this.locationRight = locationRight;
        }

        public Vector getDirectionLeft() {
            return directionLeft;
        }

        public void setDirectionLeft(Vector directionLeft) {
            this.directionLeft = directionLeft;
        }

        public Vector getDirectionMiddle() {
            return directionMiddle;
        }

        public void setDirectionMiddle(Vector directionMiddle) {
            this.directionMiddle = directionMiddle;
        }

        public Vector getDirectionRight() {
            return directionRight;
        }

        public void setDirectionRight(Vector directionRight) {
            this.directionRight = directionRight;
        }

        public FallenSouls getFallenSouls() {
            return fallenSouls;
        }

        public boolean isLeftRemoved() {
            return leftRemoved;
        }

        public void setLeftRemoved(boolean leftRemoved) {
            this.leftRemoved = leftRemoved;
        }

        public boolean isMiddleRemoved() {
            return middleRemoved;
        }

        public void setMiddleRemoved(boolean middleRemoved) {
            this.middleRemoved = middleRemoved;
        }

        public boolean isRightRemoved() {
            return rightRemoved;
        }

        public void setRightRemoved(boolean rightRemoved) {
            this.rightRemoved = rightRemoved;
        }

        public void setFallenSouls(FallenSouls fallenSouls) {
            this.fallenSouls = fallenSouls;
        }

        public List<WarlordsPlayer> getPlayersHit() {
            return playersHit;
        }

        public void setPlayersHit(List<WarlordsPlayer> playersHit) {
            this.playersHit = playersHit;
        }

    }
}
