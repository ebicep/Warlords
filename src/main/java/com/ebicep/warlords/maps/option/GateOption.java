package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.DebugLocationMarker;
import com.ebicep.warlords.maps.option.marker.TimerSkipAbleMarker;
import static com.ebicep.warlords.util.ChatUtils.sendMessage;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.Utils;
import static com.ebicep.warlords.util.Utils.iterable;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GateOption implements Option, TimerSkipAbleMarker {

    public static final int DEFAULT_GATE_DELAY = 10;
    public static final Material DEFAULT_OPEN_MATERIAL = Material.AIR;
    public static final Material DEFAULT_CLOSED_MATERIAL = Material.FENCE;
    public static final Boolean DEFAULT_SHOULD_BROADCAST = null;

    private final Location min;
    private final Location max;
    private final Material closed;
    private final Material open;
    private boolean autoDetectShouldBroadcast = true;
    private boolean shouldBroadcast;

    private int delay;
    @Nonnull
    private Game game;

    public GateOption(Location a, Location b) {
        this(a, b, DEFAULT_CLOSED_MATERIAL, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(Location a, Location b, Material closed) {
        this(a, b, closed, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(Location a, Location b, Material closed, Material open) {
        this(a, b, closed, open, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(Location a, Location b, Material closed, Material open, int delay) {
        this(a, b, closed, open, delay, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(Location a, Location b, Material closed, Material open, int delay, Boolean shouldBroadcast) {
        if (a.getWorld() != b.getWorld()) {
            throw new IllegalArgumentException("The worlds provided have different worlds");
        }
        if (closed == open) {
            throw new IllegalArgumentException("Cannot have the closed and open material of a gate be the same material");
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
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(null, 0, this.getClass(),
                "Gates",
                new Location(
                        min.getWorld(),
                        (min.getX() + max.getX()) / 2,
                        (min.getY() + max.getY()) / 2,
                        (min.getZ() + max.getZ()) / 2
                ),
                () -> Arrays.asList(
                        "MIN: " + min.getX() + ", " + min.getY() + ", " + min.getZ(),
                        "MAX: " + max.getX() + ", " + max.getY() + ", " + max.getZ()
                )
        ));
    }

    public void openGates() {
        delay = -1;
        changeGate(closed, open);
        if (this.shouldBroadcast) {
            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayersWithoutSpectators())) {
                Player player = entry.getKey();
                sendMessage(player, false, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!");
                PacketUtils.sendTitle(player, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);

                Utils.resetPlayerMovementStatistics(player);
            }
        }
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void skipTimer(int delay) {
        if (this.delay > 0) {
            this.delay -= delay;
            if (delay <= 0) {
                openGates();
            }
        }
    }

    @Override
    public void start(Game game) {
        if (autoDetectShouldBroadcast) {
            for (Option option : game.getOptions()) {
                if (option instanceof GateOption) {
                    GateOption gateOption = (GateOption) option;
                    if (gateOption.getDelay() == this.getDelay()) {
                        this.shouldBroadcast = option == this;
                        break;
                    }
                }
            }
        }
        new GameRunnable(game) {
            @Override
            public void run() {
                if (delay < 0) {
                    cancel();
                    return;
                }
                if (shouldBroadcast) {
                    for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayersWithoutSpectators())) {
                        Player player = entry.getKey();
                        player.playSound(player.getLocation(), delay == 0 ? Sound.WITHER_SPAWN : Sound.NOTE_STICKS, 1, 1);
                        String number = (delay >= 8 ? ChatColor.GREEN
                                : delay >= 4 ? ChatColor.YELLOW
                                        : ChatColor.RED).toString() + delay;
                        PacketUtils.sendTitle(player, number, "", 0, 40, 0);
                    }
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
                            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayersWithoutSpectators())) {
                                Player player = entry.getKey();
                                String s = delay == 1 ? "" : "s";
                                sendMessage(player, false, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + delay + ChatColor.YELLOW + " second" + s + "!");
                            }
                            break;
                    }
                }
                delay--;
            }

        }.runTaskTimer(0, 20);
    }

    @Nullable
    public Boolean shouldBroadcast() {
        return autoDetectShouldBroadcast == false ? null : shouldBroadcast;
    }

    public void setShouldBroadcast(@Nullable Boolean shouldBroadcast) {
        this.autoDetectShouldBroadcast = shouldBroadcast != null;
        this.shouldBroadcast = shouldBroadcast != null ? shouldBroadcast : false;
    }

}
