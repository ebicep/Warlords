package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nonnull;

public class SpawnMobAbility extends AbstractSpawnMobAbility {

    private final Mob mobToSpawn;

    public SpawnMobAbility(
            String mobName,
            float cooldown,
            Mob mobToSpawn
    ) {
        super(mobName, cooldown);
        this.mobToSpawn = mobToSpawn;
    }

    @Override
    public AbstractMob<?> createMob(@Nonnull WarlordsEntity wp) {
        return mobToSpawn.createMob.apply(wp.getLocation());
    }

}
