package com.ebicep.warlords.game.option.wavedefense.mobs.slime;

import com.ebicep.customentities.nms.pve.CustomSlime;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

public class BasicSlime extends AbstractSlime implements BasicMob {

    private final double hitRadius = 2.5;
    private final double shimmerRadius = 6;

    public BasicSlime(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Anomaly",
                MobTier.BASE,
                null,
                3000,
                0.5f,
                20,
                0,
                0
        );
    }

    @Override
    public void onSpawn() {
        WarlordsEntity we = Warlords.getPlayer(this.getWarlordsNPC().getEntity());
        if (we == null) return;
        new GameRunnable(we.getGame()) {
            int counter = 0;
            @Override
            public void run() {
                if (counter % 4 == 0) {
                    new CircleEffect(
                            we.getGame(),
                            we.getTeam(),
                            we.getLocation(),
                            hitRadius,
                            new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE).particlesPerCircumference(0.75),
                            new DoubleLineEffect(ParticleEffect.SPELL)
                    ).playEffects();
                }

                if (counter % 8 == 0) {
                    for (WarlordsEntity enemy : PlayerFilter
                            .entitiesAround(we, hitRadius, hitRadius, hitRadius)
                            .aliveEnemiesOf(we)
                    ) {
                        enemy.addDamageInstance(we, "Shimmer", 400, 400, -1, 100, false);
                    }
                }

                counter++;

                if (we.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void whileAlive() {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        super.onDeath(killer, deathLocation, waveDefenseOption);
        WarlordsEntity we = Warlords.getPlayer(this.getWarlordsNPC().getEntity());
        if (we == null) return;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(we, shimmerRadius, shimmerRadius, shimmerRadius)
                .aliveEnemiesOf(we)
        ) {
            enemy.getCooldownManager().addRegularCooldown(
                    "Shimmer",
                    "SHM",
                    CustomSlime.class,
                    new CustomSlime(spawnLocation.getWorld()),
                    we,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    4 * 20,
                    (cooldown, ticksLeft, counter) -> {
                        if (counter % 10 == 0) {
                            Location location = enemy.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                            ParticleEffect.SLIME.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
                        }

                        if (ticksLeft % 20 == 0) {
                            float healthDamage = enemy.getMaxHealth() * 0.04f;
                            enemy.addDamageInstance(
                                    we,
                                    "Shimmer",
                                    healthDamage,
                                    healthDamage,
                                    -1,
                                    100,
                                    false
                            );
                        }
                    }
            );
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.GREEN)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.playHelixAnimation(deathLocation, shimmerRadius, 0, 255, 0);
        Utils.playGlobalSound(deathLocation, Sound.SLIME_WALK, 2, 0.5f);
    }

}
