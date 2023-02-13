package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsAddVelocityEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;

public class EventDjer extends AbstractZombie implements BossMob {

    private final int earthQuakeRadius = 12; //TODO
    private final HashSet<String> skillsImmuneTo = new HashSet<>() {{
        add("Seismic Wave");
        add("Ground Slam");
        add("Last Stand");
        add("Boulder");
        add("Earthen Spike");
        add("Lightning Rod");
        add("Water Breath");
    }};

    public EventDjer(Location spawnLocation) {
        super(spawnLocation,
                "Djer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
                        null,
                        null,
                        null,
                        null
                ),
                9000,
                0.32f,
                10,
                1100,
                1310
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
            warlordsNPC.heal();
        }
        option.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onVelocity(WarlordsAddVelocityEvent event) {
                if (!event.getPlayer().equals(warlordsNPC)) {
                    return;
                }
                if (aboveHealthThreshold()) {
                    return;
                }
                if (!skillsImmuneTo.contains(event.getFrom())) {
                    return;
                }
                event.setCancelled(true);
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getPlayer().equals(warlordsNPC)) {
                    return;
                }
                if (aboveHealthThreshold()) {
                    return;
                }
                if (!skillsImmuneTo.contains(event.getAbility())) {
                    return;
                }
                event.setMin(event.getMin() * .75f);
                event.setMax(event.getMax() * .75f);
            }

        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();

        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f); //TODO animations
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, ParticleEffect.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, ParticleEffect.FIREWORKS_SPARK, 2, 40);
            List<WarlordsPlayer> warlordsPlayers = PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(warlordsNPC)
                    .warlordsPlayers()
                    .stream()
                    .toList();
            for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                Utils.addKnockback(name, loc, warlordsPlayer, -2.5, 0.25);
                warlordsPlayer.addDamageInstance(
                        warlordsNPC,
                        "Ground Shred", //TODO
                        920,
                        1080,
                        0,
                        100,
                        false
                );
            }
            new GameRunnable(option.getGame()) {

                @Override
                public void run() {
                    for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                        warlordsPlayer.stun();
                    }
                }
            }.runTaskLater(30);
            new GameRunnable(option.getGame()) {

                @Override
                public void run() {
                    for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                        warlordsPlayer.unstun();
                    }
                }
            }.runTaskLater(50);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private boolean aboveHealthThreshold() {
        return !(warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .75);
    }

}
