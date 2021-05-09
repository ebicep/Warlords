/*package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.ThunderLord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;


// what the fuck wip
public class RemoveEntities {

    // test comment
    public void onRemove(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Warlords.world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        if (args.length > 2) {
            Location location = player.getLocation();
            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
            stand.setGravity(true);
            stand.setVisible(true);
            stand.setHeadPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        }

        Location blueFlagLocation = new Location(player.getWorld(), 0.5, 4, 0.5);
        Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
        block.setType(Material.STANDING_BANNER);


        ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(blueFlagLocation, ArmorStand.class);
        blueFlag.setGravity(false);
        blueFlag.setCanPickupItems(false);
        blueFlag.setCustomName("BLU FLAG");
        blueFlag.setCustomNameVisible(true);
        blueFlag.setVisible(false);
    }
}*/

