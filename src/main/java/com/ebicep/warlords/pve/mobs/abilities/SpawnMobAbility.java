package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nonnull;

public class SpawnMobAbility extends AbstractSpawnMobAbility {

    protected final Mob mobToSpawn;

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
        super(AbstractAbilityBuilder.create(mobToSpawn.name).pve().cooldown(cooldown).energyCost(energyCost).startNoCooldown(startNoCooldown));
        this.mobToSpawn = mobToSpawn;
    }

    public SpawnMobAbility(
            float cooldown,
            float energyCost,
            Mob mobToSpawn,
            float startCooldown
    ) {
        super(AbstractAbilityBuilder.create(mobToSpawn.name).pve().cooldown(cooldown).energyCost(energyCost).startCooldown(startCooldown));
        this.mobToSpawn = mobToSpawn;
    }

    @Override
    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
        return mobToSpawn.createMob(wp.getLocation());
    }

}
