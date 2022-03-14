package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.warlords.Utils.iterable;

public class GateOption extends AbstractCuboidOption implements  TimerSkipAbleMarker {

    public static final int DEFAULT_GATE_DELAY = 10;
    public static final Material DEFAULT_OPEN_MATERIAL = Material.AIR;
    public static final Material DEFAULT_CLOSED_MATERIAL = Material.FENCE;
    public static final Boolean DEFAULT_SHOULD_BROADCAST = null;

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
        super(a, b);
        if (closed == open) {
            throw new IllegalArgumentException("Cannot have the closed and open material of a gate be the same material");
        }
        this.closed = closed;
        this.open = open;
        this.delay = delay;
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2) {
        this(loc, x1, y1, z1, x2, y2, z2, DEFAULT_CLOSED_MATERIAL, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2, Material closed) {
        this(loc, x1, y1, z1, x2, y2, z2, closed, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2, Material closed, Material open) {
        this(loc, x1, y1, z1, x2, y2, z2, closed, open, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2, Material closed, Material open, int delay) {
        this(loc, x1, y1, z1, x2, y2, z2, closed, open, delay, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2, Material closed, Material open, int delay, Boolean shouldBroadcast) {
        super(loc, x1, y1, z1, x2, y2, z2);
        if (closed == open) {
            throw new IllegalArgumentException("Cannot have the closed and open material of a gate be the same material");
        }
        this.closed = closed;
        this.open = open;
        this.delay = delay;
    }

    protected int changeGate(Material search, Material replace) {
        int changed = 0;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                    if (
                            x == min.getBlockX() || x == max.getBlockX() ||
                            y == min.getBlockY() || y == max.getBlockY() ||
                            z == min.getBlockZ() || z == max.getBlockZ()
                    ) {
                        Block block = min.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == search) {
                            block.setType(replace);
                            changed++;
                        }
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
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.FENCE_GATE, 0, this.getClass(),
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
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 5, 1);
                sendMessage(player, false, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!");

                Utils.resetPlayerMovementStatistics(player);
            }
        }
    }

    @Override
    public int getDelay() {
        return delay * 20;
    }

    @Override
    public void skipTimer(int delay) {
        if (this.delay > 0) {
            this.delay -= delay / 20;
            if (delay <= 0) {
                openGates();
            }
        }
    }

    @Override
    public void start(@Nonnull Game game) {
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
                if (delay == 0) {
                    openGates();
                    cancel();
                }
                if (shouldBroadcast) {
                    for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayersWithoutSpectators())) {
                        Player player = entry.getKey();
                        player.playSound(player.getLocation(), delay == 0 ? Sound.WITHER_SPAWN : Sound.NOTE_STICKS, 1, 1);
                        String number = (delay >= 8 ? ChatColor.GREEN
                                : delay >= 4 ? ChatColor.YELLOW
                                        : ChatColor.RED).toString() + delay;
                        if(delay > 0) {
                            PacketUtils.sendTitle(player, number, "", 0, 40, 0);
                        }
                    }
                    switch (delay) {
                        case 0:
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
        return !autoDetectShouldBroadcast ? null : shouldBroadcast;
    }

    public void setShouldBroadcast(@Nullable Boolean shouldBroadcast) {
        this.autoDetectShouldBroadcast = shouldBroadcast != null;
        this.shouldBroadcast = shouldBroadcast != null ? shouldBroadcast : false;
    }

}
