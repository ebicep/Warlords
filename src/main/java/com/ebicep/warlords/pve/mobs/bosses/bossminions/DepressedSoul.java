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

public class DepressedSoul extends AbstractMob implements BossMinionMob {

    private int reduceSpeed = -2;
    private int reduceSpeedTickDuration = 40;

    public DepressedSoul(Location spawnLocation) {
        this(spawnLocation, "Depressed Soul", 2500, 0.32f, 5, 300, 400);
    }

    public DepressedSoul(
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
                new RemoveTarget(30)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.DEPRESSED_SOUL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        reduceSpeed = difficulty == DifficultyIndex.EXTREME ? -6 : difficulty == DifficultyIndex.HARD ? -4 : -2;
        reduceSpeedTickDuration = difficulty == DifficultyIndex.EXTREME ? 60 : difficulty == DifficultyIndex.HARD ? 50 : 40;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 35, 25, 122, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.BLOCK_SNOW_BREAK, 0.35f, 2);
        if (!event.getCause().isEmpty()) {
            attacker.addSpeedModifier(self, name, reduceSpeed, reduceSpeedTickDuration, "BASE");
        }
    }
}
