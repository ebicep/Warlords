package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.OrbitingSwordsManager;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class ZombieEnd extends AbstractMob implements ChampionMob {

    private OrbitingSwordsManager orbitingSwordsManager;

    public ZombieEnd(Location spawnLocation) {
        super(
                spawnLocation,
                "End",
                10000,
                0.3f,
                20,
                500,
                700
        );
    }

    public ZombieEnd(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZOMBIE_END;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        orbitingSwordsManager = new OrbitingSwordsManager(() -> warlordsNPC.getLocation(), 3, 2, 1, 1.5f, option, warlordsNPC, Material.END_CRYSTAL);

        orbitingSwordsManager.spawnSwords(3);
        orbitingSwordsManager.start();

        EffectUtils.playFirework(
                warlordsNPC.getLocation(),
                FireworkEffect.builder()
                        .withColor(Color.PURPLE)
                        .with(FireworkEffect.Type.BALL)
                        .build()
        );
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 0.7f);
        receiver.addSpeedModifier(attacker, "End Slowness", -20, 2 * 20);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            for (WarlordsEntity we : PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, 3.5, 4, 3.5)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addSpeedModifier(warlordsNPC, "End Slowness", -80, 30);
                we.addInstance(InstanceBuilder
                        .damage()
                        .cause("End Distortion")
                        .source(warlordsNPC)
                        .value(100)
                );
            }
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_SCREAM, 2, 0.4f);
        orbitingSwordsManager.stop();
    }
}
