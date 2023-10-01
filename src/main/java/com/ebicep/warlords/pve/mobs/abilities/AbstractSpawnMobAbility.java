package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import javax.annotation.Nonnull;

public abstract class AbstractSpawnMobAbility extends AbstractPveAbility {

    public AbstractSpawnMobAbility(String mobName, float cooldown, boolean startNoCooldown) {
        super("Spawn " + mobName, cooldown, 50, startNoCooldown);
    }

    public AbstractSpawnMobAbility(String mobName, float cooldown, float energyCost, boolean startNoCooldown) {
        super("Spawn " + mobName, cooldown, energyCost, startNoCooldown);
    }

    public AbstractSpawnMobAbility(String mobName, float cooldown, float energyCost, float startCooldown) {
        super("Spawn " + mobName, cooldown, energyCost, startCooldown);
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        wp.subtractEnergy(name, energyCost, false);

        for (int i = 0; i < getSpawnAmount(); i++) {
            spawnMob(wp);
        }
        return true;
    }

    public abstract AbstractMob<?> createMob(@Nonnull WarlordsEntity wp);

    public void spawnMob(@Nonnull WarlordsEntity wp) {
        AbstractMob<?> mob = createMob(wp);
        onMobCreate(mob);
        pveOption.spawnNewMob(mob, wp.getTeam());
        onMobSpawn(mob.getWarlordsNPC());
    }

    public int getSpawnAmount() {
        return 1;
    }

    public void onMobCreate(AbstractMob<?> mobSpawned) {

    }

    public void onMobSpawn(WarlordsNPC warlordsNPC) {

    }
}
