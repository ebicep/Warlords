package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.FrostSpikesAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Orbyz extends AbstractMob implements BossMob {

    private Location mapCenter;
    private FrostSpikesAbility frostSpikesAbility;

    public Orbyz(Location spawnLocation) {
        super(
                spawnLocation,
                "Orbyz",
                300000,
                0.15f,
                20,
                5000,
                7000
        );
    }

    public Orbyz(
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
        super.onSpawn(option);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5);

        frostSpikesAbility = new FrostSpikesAbility(
                warlordsNPC,
                warlordsNPC,
                () -> mapCenter,
                FrostSpikesAbility.Pattern.RING,
                FrostSpikesAbility.EruptMode.SEQUENTIAL,
                20,
                8,
                40,
                20,
                2.2,
                480,
                0.5,
                3.5,
                0.35,
                false,
                true,
                0.35
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.SNOWFLAKE);
        }

        if (ticksElapsed % 40 == 0) {
            EffectUtils.playCircularShieldAnimation(warlordsNPC.getLocation(), Particle.END_ROD, 6, 2, 3);
        }

        if (ticksElapsed % 200 == 0) {
            frostSpikesAbility.start(warlordsNPC.getGame());
        }
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ORBYZ;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public Component getDescription() {
        return Component.text("Frozen in Time", NamedTextColor.WHITE);
    }
}
