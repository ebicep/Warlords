package com.ebicep.warlords.game.option.cuboid;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.java.JavaUtils.iterable;

public class GateOption extends AbstractCuboidOption implements TimerSkipAbleMarker {

    public static final int DEFAULT_GATE_DELAY = 10;
    public static final Material DEFAULT_OPEN_MATERIAL = Material.AIR;
    public static final Material DEFAULT_CLOSED_MATERIAL = Material.OAK_FENCE;
    public static final Boolean DEFAULT_SHOULD_BROADCAST = null;

    private final Material closed;
    private final Material open;
    private final BlockFace blockFace;
    private boolean autoDetectShouldBroadcast = true;
    private boolean shouldBroadcast;

    private int delay;
    @Nonnull
    private Game game;

    public GateOption(Location a, Location b) {
        this(a, b, DEFAULT_CLOSED_MATERIAL, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(Location a, Location b, Material closed, Material open, int delay, Boolean shouldBroadcast) {
        super(a, b);
        if (closed == open) {
            throw new IllegalArgumentException("Cannot have the closed and open material of a gate be the same material");
        }
        this.closed = closed;
        this.open = open;
        this.delay = delay;
        if (a.getX() == b.getX()) {
            blockFace = BlockFace.NORTH;
        } else {
            blockFace = BlockFace.EAST;
        }
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

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2) {
        this(loc, x1, y1, z1, x2, y2, z2, DEFAULT_CLOSED_MATERIAL, DEFAULT_OPEN_MATERIAL, DEFAULT_GATE_DELAY, DEFAULT_SHOULD_BROADCAST);
    }

    public GateOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2, Material closed, Material open, int delay, Boolean shouldBroadcast) {
        super(loc, x1, y1, z1, x2, y2, z2);
        if (closed == open) {
            throw new IllegalArgumentException("Cannot have the closed and open material of a gate be the same material");
        }
        this.closed = closed;
        this.open = open;
        this.delay = delay;
        if (x1 == x2) {
            blockFace = BlockFace.NORTH;
        } else {
            blockFace = BlockFace.EAST;
        }
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

    @Override
    public void register(@Nonnull Game game) {
        changeGate(open, closed);
        this.game = game;
        game.registerGameMarker(TimerSkipAbleMarker.class, this);
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.OAK_FENCE_GATE, 0, this.getClass(),
                Component.text("Gates"),
                new Location(
                        getMin().getWorld(),
                        (getMin().getX() + getMax().getX()) / 2,
                        (getMin().getY() + getMax().getY()) / 2,
                        (getMin().getZ() + getMax().getZ()) / 2
                ),
                () -> Arrays.asList(
                        Component.text("MIN: " + getMin().getX() + ", " + getMin().getY() + ", " + getMin().getZ()),
                        Component.text("MAX: " + getMax().getX() + ", " + getMax().getY() + ", " + getMax().getZ())
                )
        ));
    }

    protected int changeGate(Material search, Material replace) {
        int changed = 0;
        for (int x = getMin().getBlockX(); x <= getMax().getBlockX(); x++) {
            for (int z = getMin().getBlockZ(); z <= getMax().getBlockZ(); z++) {
                for (int y = getMin().getBlockY(); y <= getMax().getBlockY(); y++) {
                    if (
                            x == getMin().getBlockX() || x == getMax().getBlockX() ||
                                    y == getMin().getBlockY() || y == getMax().getBlockY() ||
                                    z == getMin().getBlockZ() || z == getMax().getBlockZ()
                    ) {
                        Block block = getMin().getWorld().getBlockAt(x, y, z);
                        if (block.getType() == search) {
                            block.setType(replace);
                            if (replace == closed && block.getBlockData() instanceof MultipleFacing multipleFacing) {
                                multipleFacing.setFace(blockFace, true);
                                multipleFacing.setFace(blockFace.getOppositeFace(), true);
                                block.setBlockData(multipleFacing);
                            }
                            changed++;
                        }
                    }
                }
            }
        }
        return changed;
    }

    @Override
    public void start(@Nonnull Game game) {
        if (autoDetectShouldBroadcast) {
            for (Option option : game.getOptions()) {
                if (option instanceof GateOption gateOption) {
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
                        player.playSound(player.getLocation(), delay == 0 ? Sound.ENTITY_WITHER_SPAWN : Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                        if (delay > 0) {
                            player.showTitle(Title.title(
                                    Component.text(delay, delay >= 8 ? NamedTextColor.GREEN : delay >= 4 ? NamedTextColor.YELLOW : NamedTextColor.RED),
                                    Component.empty(),
                                    Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                            ));
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
                                sendMessage(player, false, Component.text("The gates will fall in ", NamedTextColor.YELLOW)
                                                                    .append(Component.text(delay, NamedTextColor.RED))
                                                                    .append(Component.text(" second" + (delay == 1 ? "!" : "s!"))));
                            }
                            break;
                    }
                }
                delay--;
            }

        }.runTaskTimer(0, 20);
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

    public void openGates() {
        delay = -1;
        changeGate(closed, open);
        if (this.shouldBroadcast) {
            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayersWithoutSpectators())) {
                Player player = entry.getKey();
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 5, 1);
                sendMessage(player, false, Component.text("Gates opened! ", NamedTextColor.YELLOW).append(Component.text("FIGHT!", NamedTextColor.RED)));
            }
        }
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
