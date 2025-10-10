package com.ebicep.warlords.pve.mobs.creaking;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.pve.mobs.vindicator.AncientDynasty;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SovereignGuardian extends AbstractMob implements ChampionMob {

    private Listener listener;
    private List<WarlordsEntity> summons = new ArrayList<>();

    public SovereignGuardian(Location spawnLocation) {
        super(
                spawnLocation,
                "Sovereign Guardian",
                13000,
                0.15f,
                20,
                500,
                1000
        );
    }

    public SovereignGuardian(
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

        listener = new Listener() {
            @EventHandler(ignoreCancelled = true)
            private void onAllyDeath(WarlordsDeathEvent event) {
                if (summons.isEmpty()) return;
                summons.removeIf(p -> p.equals(event.getWarlordsEntity()));
            }
        };

        warlordsNPC.getGame().registerEvents(listener);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 300 == 0) {
            if (summons.size() < 4) {
                AbstractMob mob = new AncientDynasty(warlordsNPC.getLocation());
                option.spawnNewMob(mob);
                summons.add(mob.getWarlordsNPC());
            }
        }

        if (ticksElapsed % 20 == 0) {
            summons.forEach(entity -> EffectUtils.playParticleLinkAnimation(
                    warlordsNPC.getLocation(),
                    entity.getLocation(),
                    Particle.FIREWORK
            ));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        summons.clear();
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SOVEREIGN_GUARDIAN;
    }
}
