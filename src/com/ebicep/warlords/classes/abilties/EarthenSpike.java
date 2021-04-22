package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Utils;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    private List<ArrayList<EarthenSpikeBlock>> spikeArrays = new ArrayList<>();
    public List<ArrayList<EarthenSpikeBlock>> getSpikeArrays() {
        return spikeArrays;
    }

    public EarthenSpike() {
        super("Earthen Spike", 0, 120, 15, 175, "earthen spike description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        List<Entity> near = player.getNearbyEntities(50.0D, 50.0D, 50.0D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                double distance = player.getLocation().distance(nearPlayer.getLocation());
                if (Utils.getLookingAt(player, nearPlayer)) {
                    location.setY(player.getWorld().getHighestBlockYAt(location));


                    FallingBlock block = player.getWorld().spawnFallingBlock(location.add(0, 0, 0), location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getType(), (byte) 0);
                    block.setVelocity(new Vector(0, .2, 0));
                    ArrayList<EarthenSpikeBlock> spikeList = new ArrayList<>();
                    spikeList.add(new EarthenSpikeBlock(block, nearPlayer));
                    EarthenSpike spike = new EarthenSpike();
                    spike.getSpikeArrays().add(spikeList);
                    WarlordsEvents.getSpikes().add(spike);

                    WarlordsEvents.addEntityUUID(block.getUniqueId());
                }
            }
        }
    }
}
