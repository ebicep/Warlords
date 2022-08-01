package com.ebicep.warlords.game.option.wavedefense.mobs.magmacube;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.LastStand;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class MagmaCube extends AbstractMagmaCube implements EliteMob {

    public MagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illumination",
                null,
                4000,
                0.5f,
                20,
                0,
                0
        );
    }

    @Override
    public void onSpawn() {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive() {
        Location loc = getWarlordsNPC().getLocation();
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playCylinderAnimation(loc, 9, ParticleEffect.SPELL, 1);
        Utils.playGlobalSound(loc, "warrior.laststand.activation", 2, 0.6f);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 9, 9, 9)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            ally.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                    name,
                    "",
                    LastStand.class,
                    new LastStand(),
                    we,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    2 * 20
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * .6f;
                }
            });
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.2).setY(0.2);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, 6, 6, 6)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addDamageInstance(we, "Blight", 900, 1200, -1, 100, false);
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
