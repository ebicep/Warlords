package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.customentities.nms.MountEntitySetup;
import com.ebicep.warlords.player.general.mounts.MountType;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@CommandAlias("mount")
@CommandPermission("group.administrator")
public class MountCommand extends BaseCommand {

    public static final Map<UUID, EntityType> PLAYER_MOUNT_TYPE = new HashMap<>();
    public static final Map<EntityType, Float> SPEEDS = new HashMap<>();
    private static final double HEIGHT_TOLERANCE = 0.01;

    static {
        SPEEDS.put(EntityType.HORSE, 1.6f);
    }

    public static float getSpeed(EntityType entityType) {
        return SPEEDS.getOrDefault(entityType, 3.2f);
    }

    @Subcommand("custom")
    @CommandCompletion("@players")
    @Description("Toggles custom mounts")
    public void custom(CommandIssuer issuer, @Flags("other") Player player, EntityType entityType) {
        PLAYER_MOUNT_TYPE.put(player.getUniqueId(), entityType);
        ChatChannels.sendDebugMessage(issuer, Component.text("Set mount type for " + player.getName() + " to " + entityType.name(), NamedTextColor.GREEN));
    }

    @Subcommand("set")
    @CommandCompletion("@players @enabledisable")
    @Description("Sets player mount type")
    public void set(CommandIssuer issuer, @Flags("other") Player player, @Values("@enabledisable") String option) {
        if (option.equals("enable")) {
            PLAYER_MOUNT_TYPE.put(player.getUniqueId(), EntityType.HORSE);
            ChatChannels.sendDebugMessage(issuer, Component.text("Set mount type for " + player.getName() + " to HORSE", NamedTextColor.GREEN));
        } else {
            PLAYER_MOUNT_TYPE.remove(player.getUniqueId());
            ChatChannels.sendDebugMessage(issuer, Component.text("Removed mount type for " + player.getName(), NamedTextColor.GREEN));
        }
    }

    @Subcommand("speedtest")
    @Description("Runs every mount forward on flat ground and compares how far they travel")
    public void speedTest(
            Player player,
            @Optional @Default("60") @Conditions("limits:min=20,max=200") Integer ticks
    ) {
        Location start = beside(player.getLocation());
        List<SpeedResult> results = new ArrayList<>();
        for (MountType type : MountType.VALUES) {
            results.add(measure(type, start, ticks));
        }
        report(player, ticks, results);
    }

    @Subcommand("height")
    @Description("Compares mount hitbox height and the riding player's seat height")
    public void height(Player player) {
        Location start = beside(player.getLocation());
        ServerPlayer rider = ((CraftPlayer) player).getHandle();
        List<HeightResult> results = new ArrayList<>();
        for (MountType type : MountType.VALUES) {
            results.add(measureHeight(type, start, rider));
        }
        reportHeight(player, rider.getBbHeight(), results);
    }

    private static HeightResult measureHeight(MountType type, Location start, ServerPlayer rider) {
        Mob mount = spawnMount(type, start);
        try {
            Vec3 riding = mount.getPassengerRidingPosition(rider);
            Vec3 attachment = rider.getVehicleAttachmentPoint(mount);
            double seat = riding.y - attachment.y - mount.getY();
            return new HeightResult(type, mount.getBbHeight(), seat);
        } finally {
            mount.discard();
        }
    }

    private static void reportHeight(Player player, float playerHeight, List<HeightResult> results) {
        ChatChannels.sendDebugMessage(player, Component.text(String.format(Locale.US,
                "Mount height check. Your hitbox is %.3f. Seat is your feet above the mount.",
                playerHeight
        ), NamedTextColor.GRAY));
        if (results.isEmpty()) {
            return;
        }
        HeightResult baseline = results.getFirst();
        double mountSpread = spread(results.stream().mapToDouble(HeightResult::mountHeight).toArray());
        double seatSpread = spread(results.stream().mapToDouble(HeightResult::seat).toArray());
        for (HeightResult result : results) {
            boolean matches = close(result.mountHeight, baseline.mountHeight) && close(result.seat, baseline.seat);
            ChatChannels.sendDebugMessage(player, Component.text(String.format(Locale.US,
                    "%s: mount %.3f, seat %.3f%s",
                    result.type.getDisplayName(),
                    result.mountHeight,
                    result.seat,
                    scaleNote(result.type)
            ), matches ? NamedTextColor.GREEN : NamedTextColor.RED));
        }
        ChatChannels.sendDebugMessage(player, Component.text(
                sameHeight("Mount heights", mountSpread) + " " + sameHeight("Seat heights", seatSpread),
                mountSpread <= HEIGHT_TOLERANCE && seatSpread <= HEIGHT_TOLERANCE ? NamedTextColor.GREEN : NamedTextColor.RED
        ));
    }

    private static String sameHeight(String label, double spread) {
        if (spread <= HEIGHT_TOLERANCE) {
            return label + " match.";
        }
        return String.format(Locale.US, "%s differ by %.3f.", label, spread);
    }

    private static String scaleNote(MountType type) {
        if (Math.abs(type.getScale() - 1.0) <= 0.001) {
            return "";
        }
        return String.format(Locale.US, ", scale %.2f", type.getScale());
    }

    private static double spread(double[] values) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        return max - min;
    }

    private static boolean close(double value, double baseline) {
        return Math.abs(value - baseline) <= HEIGHT_TOLERANCE;
    }

    private static SpeedResult measure(MountType type, Location start, int ticks) {
        Mob mount = spawnMount(type, start);
        try {
            for (int tick = 0; tick < 40 && !mount.onGround(); tick++) {
                mount.setSpeed(0.0F);
                mount.travel(Vec3.ZERO);
            }
            boolean landed = mount.onGround();
            mount.setDeltaMovement(Vec3.ZERO);
            double startX = mount.getX();
            double startZ = mount.getZ();
            for (int tick = 0; tick < ticks; tick++) {
                MountEntitySetup.simulateForwardTick(mount);
            }
            double distance = Math.hypot(mount.getX() - startX, mount.getZ() - startZ);
            return new SpeedResult(type, distance, MountEntitySetup.riddenSpeed(mount), landed && mount.onGround());
        } finally {
            mount.discard();
        }
    }

    private static void report(Player player, int ticks, List<SpeedResult> results) {
        ChatChannels.sendDebugMessage(player, Component.text(
                "Mount speed test, " + ticks + " ticks of full forward. Stand on open flat ground.",
                NamedTextColor.GRAY
        ));
        double fastest = results.stream().mapToDouble(SpeedResult::distance).max().orElse(0);
        double slowest = results.stream().mapToDouble(SpeedResult::distance).min().orElse(0);
        boolean same = fastest > 0 && fastest - slowest <= fastest * 0.02;
        for (SpeedResult result : results) {
            double blocksPerSecond = result.distance / ticks * 20.0;
            boolean outlier = fastest > 0 && fastest - result.distance > fastest * 0.02;
            ChatChannels.sendDebugMessage(player, Component.text(String.format(Locale.US,
                    "%s: %.2f blocks, %.2f blocks/s, ridden speed %.3f%s",
                    result.type.getDisplayName(),
                    result.distance,
                    blocksPerSecond,
                    result.riddenSpeed,
                    result.stayedOnGround ? "" : " (left the ground)"
            ), outlier ? NamedTextColor.RED : NamedTextColor.GREEN));
        }
        if (fastest <= 0) {
            ChatChannels.sendDebugMessage(player, Component.text("None of the mounts moved.", NamedTextColor.RED));
        } else if (same) {
            ChatChannels.sendDebugMessage(player, Component.text(String.format(Locale.US,
                    "Same speed. Spread is %.2f blocks.",
                    fastest - slowest
            ), NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(player, Component.text(String.format(Locale.US,
                    "Not the same speed. Spread is %.2f blocks (%.0f%%).",
                    fastest - slowest,
                    (fastest - slowest) / fastest * 100.0
            ), NamedTextColor.RED));
        }
    }

    private static Mob spawnMount(MountType type, Location start) {
        Mob mount = type.create(start);
        LivingEntity entity = (LivingEntity) mount.getBukkitEntity();
        setBase(entity, Attribute.MOVEMENT_SPEED, MountEntitySetup.MOVEMENT_SPEED);
        setBase(entity, Attribute.SCALE, type.getScale());
        setBase(entity, Attribute.STEP_HEIGHT, 1.0);
        ((CraftWorld) start.getWorld()).getHandle().addFreshEntity(mount, CreatureSpawnEvent.SpawnReason.CUSTOM);
        mount.refreshDimensions();
        return mount;
    }

    private static Location beside(Location location) {
        Location start = location.clone();
        double yaw = Math.toRadians(start.getYaw());
        start.add(Math.cos(yaw) * 3.0, 0, Math.sin(yaw) * 3.0);
        start.setPitch(0);
        return start;
    }

    private static void setBase(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    private record SpeedResult(MountType type, double distance, float riddenSpeed, boolean stayedOnGround) {
    }

    private record HeightResult(MountType type, float mountHeight, double seat) {
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
