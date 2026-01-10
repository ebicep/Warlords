package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OrbPassenger extends ExperienceOrb {

    protected final ArmorStand armorStand;
    protected int ticksLived = 0;

    public OrbPassenger(Location location, WarlordsEntity owner) {
        this(location, owner, null);
    }

    public OrbPassenger(Location location, WarlordsEntity owner, @Nullable Consumer<ArmorStand> standConsumer) {
        super(((CraftWorld) location.getWorld()).getHandle(),
                location.getX(),
                location.getY() + 2,
                location.getZ(),
                2500,
                org.bukkit.entity.ExperienceOrb.SpawnReason.CUSTOM,
                null,
                null
        );
        ArmorStand orbStand = Utils.spawnArmorStand(LocationUtils.getGroundLocation(location).clone().add(0, .5, 0), armorStand -> {
            armorStand.setMarker(true);
                    armorStand.setGravity(true);
                    armorStand.addPassenger(spawn(location).getBukkitEntity());
                    if (standConsumer != null) {
                        standConsumer.accept(armorStand);
                    }
                }
        );
        for (WarlordsEntity warlordsEntity : PlayerFilter.playingGame(owner.getGame()).enemiesOf(owner)) {
            if (warlordsEntity.getEntity() instanceof Player player) {
                PacketUtils.removeEntityForPlayer(player, getId());
            }
        }
        this.armorStand = orbStand;
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

}
