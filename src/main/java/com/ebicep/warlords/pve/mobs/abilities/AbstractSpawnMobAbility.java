package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.newitems.setbonus.sets.Summoner;

import javax.annotation.Nonnull;

public abstract class AbstractSpawnMobAbility extends AbstractPveAbility {

    public AbstractSpawnMobAbility(AbstractAbilityBuilder builder) {
        super(builder.name("Spawn " + builder.getName()));
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        for (int i = 0; i < getSpawnAmount(); i++) {
            spawnMob(wp);
        }
        return true;
    }

    public int getSpawnAmount() {
        return 1;
    }

    public void spawnMob(@Nonnull WarlordsEntity wp) {
        AbstractMob mob = createMob(wp);
        if (mob == null) {
            return;
        }
        onMobCreate(mob);
        WarlordsPlayer summoner = wp instanceof WarlordsPlayer warlordsPlayer ? warlordsPlayer : null;
        if (summoner != null) {
            Summoner.applyToSummon(mob, summoner);
        }
        pveOption.spawnNewMob(mob, wp.getTeam());
        if (summoner != null) {
            Summoner.applyToSummon(mob, summoner);
        }
        onMobSpawn(mob.getWarlordsNPC());
    }

    public abstract AbstractMob createMob(@Nonnull WarlordsEntity wp);

    public void onMobCreate(AbstractMob mobSpawned) {

    }

    public void onMobSpawn(WarlordsNPC warlordsNPC) {

    }
}
