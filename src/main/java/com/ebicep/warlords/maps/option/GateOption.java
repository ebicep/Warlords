package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Cuboid;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.option.marker.TimerSkipAbleMarker;
import static com.ebicep.warlords.util.ChatUtils.sendMessage;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.Utils;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

public class GateOption implements Option, TimerSkipAbleMarker {
    public static final int DEFAULT_GATE_DELAY = 10;

    private final Location min;
    private final Location max;
    private final Material closed;
    private final Material open;
    
    private int delay;
    @Nonnull
    private Game game;

    public GateOption(Location a, Location b, Material closed, Material open) {
        this(a, b, closed, open, DEFAULT_GATE_DELAY);
    }

    public GateOption(Location a, Location b, Material closed, Material open, int delay) {
        if (a.getWorld() != b.getWorld()) {
            throw new IllegalArgumentException("The worlds provided have different worlds");
        }
        this.min = new Location(a.getWorld(), Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getYaw(), a.getPitch());
        this.max = new Location(a.getWorld(), Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), b.getYaw(), b.getPitch());
        this.closed = closed;
        this.open = open;
        this.delay = delay;
    }
    
    
    private int changeGate(Material search, Material replace) {
        int changed = 0;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                    Block block = min.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == search) {
                        block.setType(replace);
                        changed++;
                    }
                }
            }
        }
        return changed;
    }
    
    @Override
    public void register(Game game) {
        changeGate(open, closed);
        this.game = game;
        game.registerGameMarker(TimerSkipAbleMarker.class, this);
    }
    
    public void openGates() {
        delay = -1;
        changeGate(closed, open);
        game.forEachOnlinePlayer((player, team) -> {
            sendMessage(player, false, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!");
            PacketUtils.sendTitle(player, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);

            Utils.resetPlayerMovementStatistics(player);
        });
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void skipTimer(int delay) {
        if (this.delay > 0) {
            this.delay -= delay;
        }
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                if(delay < 0) {
                    cancel();
                    return;
                }
                game.forEachOnlinePlayer((player, team) -> {
                    player.playSound(player.getLocation(), delay == 0 ? Sound.WITHER_SPAWN : Sound.NOTE_STICKS, 1, 1);
                    String number = (
                            delay >= 8 ? ChatColor.GREEN :
                            delay >= 4 ? ChatColor.YELLOW :
                            ChatColor.RED
                    ).toString() + delay;
                    PacketUtils.sendTitle(player, number, "", 0, 40, 0);
                });
                switch (delay) {
                    case 0:
                        openGates();
                        cancel();
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                        game.forEachOnlinePlayer((player, team) -> {
                            String s = delay == 1 ? "" : "s";
                            sendMessage(player, false, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + delay + ChatColor.YELLOW + " second" + s + "!");
                        });
                        break;
                }
                delay--;
            }
            
        }.runTaskTimer(0, 20);
    }

}
