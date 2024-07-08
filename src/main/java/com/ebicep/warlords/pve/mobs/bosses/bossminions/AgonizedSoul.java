package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

public class AgonizedSoul extends AbstractMob implements BossMinionMob {

    private int cdReduction = 2;

    public AgonizedSoul(Location spawnLocation) {
        this(spawnLocation, "Agonized Soul", 2000, 0.38f, 5, 200, 300);
    }

    public AgonizedSoul(
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
                new RemoveTarget(15)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.AGONIZED_SOUL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        cdReduction = difficulty == DifficultyIndex.EXTREME ? 4 : difficulty == DifficultyIndex.HARD ? 3 : 2;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 54, 54, 54, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_HIT, 0.35f, 2);
        if (!event.getCause().isEmpty()) {
            attacker.getCooldownManager().subtractTicksOnRegularCooldowns(cdReduction, CooldownTypes.ABILITY, CooldownTypes.BUFF);
        }
    }

}
