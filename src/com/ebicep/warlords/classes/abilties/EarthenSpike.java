package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.util.Utils;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    private List<ArrayList<EarthenSpikeBlock>> spikeArrays = new ArrayList<>();
    public List<ArrayList<EarthenSpikeBlock>> getSpikeArrays() {
        return spikeArrays;
    }

    public EarthenSpike() {
        super("Earthen Spike", -476, -662, 0, 120, 15, 175,
                "§7Send forth an underground earth spike\n" +
                "§7that locks onto a targeted enemy player.\n" +
                "§7When the spike reaches its target it\n" +
                "§7emerges from the ground, dealing §c%dynamic.value% §7-\n" +
                "§c%dynamic.value §7damage to any nearby enemies and\n" +
                "§7launches them up into the air.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();
        List<Entity> near = player.getNearbyEntities(7.0D, 6.0D, 7.0D);
        near = Utils.filterOutTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer)) {
                    FallingBlock block = player.getWorld().spawnFallingBlock(location.clone(), location.getWorld().getBlockAt((int) location.getX(), (int) location.getY(), (int) location.getZ()).getType(), (byte) 0);
                    block.setVelocity(new Vector(0, .2, 0));
                    ArrayList<EarthenSpikeBlock> spikeList = new ArrayList<>();
                    spikeList.add(new EarthenSpikeBlock(block, nearPlayer, Warlords.getPlayer(player)));
                    EarthenSpike spike = new EarthenSpike();
                    spike.getSpikeArrays().add(spikeList);

                    Warlords.getPlayer(player).subtractEnergy(energyCost);
                    Warlords.spikes.add(spike);
                    WarlordsEvents.addEntityUUID(block.getUniqueId());
                    break;
                }
            }
        }
    }

    public static class EarthenSpikeBlock {

        FallingBlock block;
        Player player;
        WarlordsPlayer user;

        public EarthenSpikeBlock(FallingBlock block, Player player, WarlordsPlayer user) {
            this.block = block;
            this.player = player;
            this.user = user;
        }

        public FallingBlock getBlock() {
            return block;
        }

        public void setBlock(FallingBlock block) {
            this.block = block;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public WarlordsPlayer getUser() {
            return user;
        }

        public void setUser(WarlordsPlayer user) {
            this.user = user;
        }
    }
}
