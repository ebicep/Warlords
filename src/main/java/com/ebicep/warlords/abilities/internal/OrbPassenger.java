package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OrbPassenger extends ExperienceOrb {

    protected final ArmorStand armorStand;
    protected final int tickMultiplier;
    protected int ticksLived = 0;

    public OrbPassenger(Location location, WarlordsEntity owner, int tickMultiplier) {
        this(location, owner, tickMultiplier, null);
    }

    public OrbPassenger(Location location, WarlordsEntity owner, int tickMultiplier, @Nullable Consumer<ArmorStand> standConsumer) {
        super(((CraftWorld) location.getWorld()).getHandle(),
                location.getX(),
                location.getY() + 2,
                location.getZ(),
                2500,
                org.bukkit.entity.ExperienceOrb.SpawnReason.CUSTOM,
                null
        );
        ArmorStand orbStand = Utils.spawnArmorStand(location.clone().add(0, 1.5, 0), armorStand -> {
            armorStand.setGravity(true);
            armorStand.addPassenger(spawn(location).getBukkitEntity());
            if (standConsumer != null) {
                standConsumer.accept(armorStand);
            }
        });
        for (WarlordsEntity warlordsEntity : PlayerFilter.playingGame(owner.getGame()).enemiesOf(owner)) {
            if (warlordsEntity.getEntity() instanceof Player player) {
                PacketUtils.removeEntityForPlayer(player, getId());
            }
        }
        this.armorStand = orbStand;
        this.tickMultiplier = tickMultiplier;
        new GameRunnable(owner.getGame()) {

            @Override
            public void run() {
                if (!armorStand.isValid()) {
                    this.cancel();
                } else {
                    ticksLived += tickMultiplier;
                }
            }
        }.runTaskTimer(30, 0);
    }

    public OrbPassenger spawn(Location loc) {
        ServerLevel w = ((CraftWorld) loc.getWorld()).getHandle();
        w.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this;
    }

    @Override
    public void tick() {
    }

    // Makes it so they cannot be picked up
    @Override
    public void playerTouch(@Nonnull net.minecraft.world.entity.player.Player player) {
    }

    public void remove() {
        armorStand.remove();
        getBukkitEntity().remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public int getTicksToLive() {
        return 160 * tickMultiplier;
    }

}
