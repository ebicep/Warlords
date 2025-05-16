package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nonnull;

public class SpawnMobAbility extends AbstractSpawnMobAbility {

    protected final Mob mobToSpawn;

    public SpawnMobAbility(AbstractAbilityBuilder builder, Mob mobToSpawn) {
        super(builder);
        this.mobToSpawn = mobToSpawn;
    }

    @Override
    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
        return mobToSpawn.createMob(wp.getLocation());
    }

}
