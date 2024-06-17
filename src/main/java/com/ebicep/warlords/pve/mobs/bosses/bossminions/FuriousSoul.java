package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.EnumSet;

public class FuriousSoul extends AbstractMob implements BossMinionMob {

    private float maxHPPercent = .01f;

    public FuriousSoul(Location spawnLocation) {
        this(spawnLocation, "Furious Soul", 1500, 0.45f, 3, 500, 600);
    }

    public FuriousSoul(
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
                new RemoveTarget(12)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.FURIOUS_SOUL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        maxHPPercent = difficulty == DifficultyIndex.EXTREME ? .03f : difficulty == DifficultyIndex.HARD ? .02f : .01f;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 81, 18, 59, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED, 0.35f, 2);
        if (!event.getCause().isEmpty() && !event.getFlags().contains(InstanceFlags.RECURSIVE)) {
            float damage = attacker.getMaxBaseHealth() * maxHPPercent;
            attacker.addDamageInstance(
                    self,
                    "Outrage",
                    damage,
                    damage,
                    0,
                    100,
                    EnumSet.of(InstanceFlags.TRUE_DAMAGE, InstanceFlags.RECURSIVE)
            );
        }
    }
}
