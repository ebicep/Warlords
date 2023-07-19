package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class SpawnMobAbility extends AbstractPveAbility {

    private final Mobs mobToSpawn;
    private final Function<PveOption, Integer> spawnAmount;

    public SpawnMobAbility(
            String mobName,
            float cooldown,
            Mobs mobToSpawn,
            Function<PveOption, Integer> spawnAmount
    ) {
        super("Spawn " + mobName, cooldown, 50);
        this.mobToSpawn = mobToSpawn;
        this.spawnAmount = spawnAmount;
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
        for (int i = 0; i < spawnAmount.apply(pveOption); i++) {
            pveOption.spawnNewMob(mobToSpawn.createMob.apply(wp.getLocation()), wp.getTeam());
        }
        return true;
    }
}
