package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;


import javax.annotation.Nonnull;

public class PetalCrystal extends AbstractMob implements BossMinionMob {

    public PetalCrystal(Location spawnLocation) {
        super(
                spawnLocation,
                "Petal Legacy",
                9000,
                0,
                10,
                0,
                0
        );
    }

    public PetalCrystal(
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
    public void onSpawn(PveOption option) {
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.fromRGB(255, 90, 180))
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.PETAL_CRYSTAL;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 60 == 0) {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                    .filter(player -> player.getName().equals("Lilium"))
                    .forEach(lilium -> {
                        EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), lilium.getLocation(), Particle.HAPPY_VILLAGER, 1);
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_AXE_WAX_OFF, 500, 0.5f);
                        lilium.addInstance(InstanceBuilder
                                .healing()
                                .cause("Healing")
                                .source(warlordsNPC)
                                .value(5000)
                        );
                    });
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {

    }
}
