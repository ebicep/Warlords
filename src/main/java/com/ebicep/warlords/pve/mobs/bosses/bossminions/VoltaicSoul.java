package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class VoltaicSoul extends AbstractMob implements BossMinionMob {

    private float energyRemoved = 5;
    private int hitBuildUp = 0;

    public VoltaicSoul(Location spawnLocation) {
        this(spawnLocation, "Voltaic Soul", 3000, 0.43f, 0, 350, 450);
    }

    public VoltaicSoul(
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
                new RemoveTarget(10)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VOLTAIC_SOUL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        energyRemoved = difficulty == DifficultyIndex.EXTREME ? 10f : difficulty == DifficultyIndex.HARD ? 7.5f : 5f;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 40 == 0 && hitBuildUp > 0) {
            hitBuildUp--;
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 171, 74, 228, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.BLOCK_GRASS_STEP, 0.35f, 2);
        if (!event.getCause().isEmpty()) {
            attacker.subtractEnergy(name, -energyRemoved, true);
            hitBuildUp++;
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);

        int damage = 100 * hitBuildUp;
        PlayerFilter.entitiesAround(deathLocation, 10, 10, 10)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(warlordsEntity -> {
                        warlordsEntity.addDamageInstance(warlordsNPC, "Static Shock", damage, damage, 0, 100);
                    });
        new FallingBlockWaveEffect(warlordsNPC.getLocation().add(0, .75, 0), 9, 1.4, Material.OAK_SAPLING).play();
    }
}
