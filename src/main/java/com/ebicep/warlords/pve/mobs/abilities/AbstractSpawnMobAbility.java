package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractSpawnMobAbility extends AbstractPveAbility {

    public AbstractSpawnMobAbility(String mobName, float cooldown, boolean startNoCooldown) {
        super("Spawn " + mobName, cooldown, 50, startNoCooldown);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        wp.subtractEnergy(energyCost, false);

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
