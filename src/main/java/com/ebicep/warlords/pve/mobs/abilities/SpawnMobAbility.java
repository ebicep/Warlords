package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nonnull;

public class SpawnMobAbility extends AbstractSpawnMobAbility {

    private final Mob mobToSpawn;

    public SpawnMobAbility(
            float cooldown,
            Mob mobToSpawn
    ) {
        this(cooldown, mobToSpawn, false);
    }

    public SpawnMobAbility(
            float cooldown,
            Mob mobToSpawn,
            boolean startNoCooldown
    ) {
        this(cooldown, 50, mobToSpawn, startNoCooldown);
    }

    public SpawnMobAbility(
            float cooldown,
            Mob mobToSpawn,
            float startCooldown
    ) {
        this(cooldown, 50, mobToSpawn, startCooldown);
    }

    public SpawnMobAbility(
            float cooldown,
            float energyCost,
            Mob mobToSpawn,
            boolean startNoCooldown
    ) {
        super(mobToSpawn.name, cooldown, energyCost, startNoCooldown);
        this.mobToSpawn = mobToSpawn;
    }

    public SpawnMobAbility(
            float cooldown,
            float energyCost,
            Mob mobToSpawn,
            float startCooldown
    ) {
        super(mobToSpawn.name, cooldown, energyCost, startCooldown);
        this.mobToSpawn = mobToSpawn;
    }

    @Override
    public AbstractMob<?> createMob(@Nonnull WarlordsEntity wp) {
        return mobToSpawn.createMob(wp.getLocation());
    }

}
