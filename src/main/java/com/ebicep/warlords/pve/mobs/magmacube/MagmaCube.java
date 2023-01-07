package com.ebicep.warlords.pve.mobs.magmacube;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.LastStand;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

public class MagmaCube extends AbstractMagmaCube implements EliteMob {

    public MagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illumination",
                MobTier.ELITE,
                null,
                4000,
                0.5f,
                20,
                0,
                0
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 60 != 0) {
            return;
        }
        Location loc = getWarlordsNPC().getLocation();
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playSphereAnimation(loc, 9, ParticleEffect.SPELL, 1);
        Utils.playGlobalSound(loc, "warrior.laststand.activation", 2, 0.6f);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 9, 9, 9)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            if (!ally.getEntity().getCustomName().equals("Illusion Illumination")) {
                ally.getCooldownManager().removeCooldown(LastStand.class);
                ally.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                        name,
                        "",
                        LastStand.class,
                        new LastStand(),
                        we,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        3 * 20
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * .5f;
                    }
                });
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, 5, 5, 5)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addDamageInstance(we, "Blight", 900, 1200, 0, 100, false);
            }
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.playHelixAnimation(deathLocation, 6, 255, 40, 40);
        Utils.playGlobalSound(deathLocation, Sound.ENDERMAN_SCREAM, 1, 2);
    }
}
