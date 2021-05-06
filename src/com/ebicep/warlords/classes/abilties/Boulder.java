package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class Boulder extends AbstractAbility {

    public Boulder() {
        super("Boulder", -588, -877, 8, 80, 15, 175,
                "§7Launch a giant boulder that shatters\n" +
                "§7and deals §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                "§7to all enemies near the impact point\n" +
                "§7and knocks them back slightly.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(1.5));
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 2, 0), EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
        stand.setCustomName("Boulder");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        //stand.setVelocity(location.getDirection().multiply(2.5));

        SnowballBoulder snowballBoulder = new SnowballBoulder(snowball, stand);
        Warlords.getSnowballBoulders().add(snowballBoulder);

        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "shaman.boulder.activation", 1, 1);
        }
    }

    public class SnowballBoulder {
        private Snowball snowball;
        private ArmorStand boulder;

        public SnowballBoulder(Snowball snowball, ArmorStand boulder) {
            this.snowball = snowball;
            this.boulder = boulder;
        }

        public Snowball getSnowball() {
            return snowball;
        }

        public void setSnowball(Snowball snowball) {
            this.snowball = snowball;
        }

        public ArmorStand getBoulder() {
            return boulder;
        }

        public void setBoulder(ArmorStand boulder) {
            this.boulder = boulder;
        }
    }
}
