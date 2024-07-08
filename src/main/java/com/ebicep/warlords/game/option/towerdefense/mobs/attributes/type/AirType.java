package com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Gravity;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AirType implements TDMobType {

    public static final TDMobType DEFAULT = new AirType(5);

    private double groundOffset;

    public AirType(double groundOffset) {
        this.groundOffset = groundOffset;
    }

    @Override
    public void onSpawn(AbstractMob mob) {
        mob.getNpc().getOrAddTrait(Gravity.class).gravitate(true);
        mob.getNpc().data().set(NPC.Metadata.FLYABLE, true);
        mob.getNpc().teleport(mob.getNpc().getStoredLocation().add(0, groundOffset, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public void onNextLocationSet(LocationBuilder nextTarget) {
        nextTarget.addY(groundOffset);
    }

}