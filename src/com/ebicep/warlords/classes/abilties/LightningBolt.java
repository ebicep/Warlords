package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class LightningBolt extends AbstractAbility {

    public LightningBolt() {
        super("Lightning Bolt", -249, -462, 0, 1, 20, 200,
                "§7Hurl a fast, piercing bolt of lightning that\n" +
                "§7deals §c%dynamic.value% §7- §c%dynamic.value% §7damage to all enemies it\n" +
                "§7passes through. Each target hit reduces the\n" +
                "§7cooldown of Chain Lightning by §62 §7seconds.\n" +
                "''\n" +
                "§7Has a maximum range of §e60 §7blocks.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();
        Vector direction = location.getDirection();

        Bolt bolt = new Bolt(Warlords.getPlayer(player), (ArmorStand) location.getWorld().spawnEntity(location.subtract(direction.getX() * -.5, .3, direction.getZ() * -.5), EntityType.ARMOR_STAND), location, direction, this);
        Warlords.getBolts().add(bolt);
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningbolt.activation", 1, 1);
        }
    }

    public static class Bolt {

        private WarlordsPlayer shooter;
        private ArmorStand armorStand;
        private Location location;
        private Vector direction;
        private LightningBolt lightningBolt;

        public Bolt(WarlordsPlayer shooter, ArmorStand armorStand, Location location, Vector direction, LightningBolt lightningBolt) {
            this.shooter = shooter;
            this.armorStand = armorStand;
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setHelmet(new ItemStack(Material.SAPLING, 1, (short) 3));
            armorStand.setHeadPose(new EulerAngle(direction.getY() * -1, 0, 0));
            this.location = location;
            this.direction = direction;
            this.lightningBolt = lightningBolt;
        }

        public WarlordsPlayer getShooter() {
            return shooter;
        }

        public void setShooter(WarlordsPlayer shooter) {
            this.shooter = shooter;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public void setArmorStand(ArmorStand armorStand) {
            this.armorStand = armorStand;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Vector getTeleportDirection() {
            return direction;
        }

        public void setDirection(Vector direction) {
            this.direction = direction;
        }

        public LightningBolt getLightningBolt() {
            return lightningBolt;
        }

        public void setLightningBolt(LightningBolt lightningBolt) {
            this.lightningBolt = lightningBolt;
        }

    }
}
