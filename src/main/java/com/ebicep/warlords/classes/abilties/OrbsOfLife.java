package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class OrbsOfLife extends AbstractAbility {

    public OrbsOfLife() {
        super("Orbs of Life", 252, 420, 19.57f, 20, 0, 0
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Striking and hitting enemies with\n" +
                "§7abilities causes them to drop an orb of\n" +
                "§7life that lasts §68 §7seconds, restoring\n" +
                "§a" + maxDamageHeal + " §7health to the ally that pick it up.\n" +
                "§7Other nearby allies recover §a" + minDamageHeal + " §7health.\n" +
                "§7Lasts §613.2 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        Warlords.getPlayer(player).setOrbsOfLifeDuration(13);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 1);
        }
    }

    public static class Orb extends EntityExperienceOrb {

        private ArmorStand armorStand;
        private final WarlordsPlayer owner;

        public Orb(World world, Location location, WarlordsPlayer owner) {
            super(world, location.getX(), location.getY(), location.getZ(), 1000);
            this.owner = owner;
        }

        @Override
        public void d(EntityHuman entityhuman) {

        }

        @Override
        public void t_() {

        }

        public Orb spawn(Location loc) {
            World w = ((CraftWorld) loc.getWorld()).getHandle();
            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
            w.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return this;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public void setArmorStand(ArmorStand armorStand) {
            this.armorStand = armorStand;
        }

        public WarlordsPlayer getOwner() {
            return owner;
        }
    }
}
