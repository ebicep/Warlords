package com.ebicep.warlords.pve.mobs.enderman;

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
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class EndermanAnomaly extends AbstractMob implements ChampionMob {

    public EndermanAnomaly(Location spawnLocation) {
        super(
                spawnLocation,
                "Taine",
                12000,
                0.36f,
                20,
                400,
                600
        );
    }

    public EndermanAnomaly(
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
        return Mob.ENDERMAN_ANOMALY;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        EffectUtils.playFirework(
                warlordsNPC.getLocation(),
                FireworkEffect.builder()
                        .withColor(Color.PURPLE)
                        .with(FireworkEffect.Type.BALL_LARGE)
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
        if (ticksElapsed % 120 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.5f);
            warlordsNPC.teleport(option.getRandomSpawnLocation(warlordsNPC));
        }

        if (ticksElapsed % 240 == 0 && ticksElapsed > 0) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(warlordsNPC, 8, 8, 8)
                    .aliveEnemiesOf(warlordsNPC)
                    .limit(1)
            ) {
                Utils.playGlobalSound(enemy.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.5f);
                enemy.sendMessage(Component.text("Taine has kidnapped you!", NamedTextColor.LIGHT_PURPLE));
                enemy.teleport(option.getRandomSpawnLocation(enemy));
            }
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 0.4f);
    }
}

