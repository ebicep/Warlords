package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

public class TormentedSoul extends AbstractMob implements BossMinionMob {

    private float reduceCooldown = 0.2f;

    public TormentedSoul(Location spawnLocation) {
        super(spawnLocation,
                "Tormented Soul",
                2000,
                0.38f,
                0,
                214,
                338,
                new RemoveTarget(20)
        );
    }

    public TormentedSoul(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new RemoveTarget(20)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TORMENTED_SOUL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        reduceCooldown = difficulty == DifficultyIndex.EXTREME ? 0.5f : difficulty == DifficultyIndex.HARD ? 0.4f : 0.2f;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 117, 24, 65, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.AMBIENT_CAVE, 0.35f, 2);
        if (!event.getCause().isEmpty()) {
            attacker.getSpec().increaseAllCooldownTimersBy(reduceCooldown);
        }
    }
}
