package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

public class CustomSlime extends EntitySlime implements CustomEntity<CustomSlime> {

    private final int shimmerHitbox = 6;

    public CustomSlime(World world) {
        super(world);
        setSize(5);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = 0.1; //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    public void onDeath(CustomSlime entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        WarlordsEntity we = Warlords.getPlayer(this.getBukkitEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, shimmerHitbox, shimmerHitbox, shimmerHitbox)
                    .aliveEnemiesOf(we)
            ) {
                enemy.getCooldownManager().addRegularCooldown(
                        "Shimmer",
                        "SHM",
                        CustomSlime.class,
                        new CustomSlime(world),
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
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.GREEN)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.playHelixAnimation(deathLocation, shimmerHitbox, 0, 255, 0);
        Utils.playGlobalSound(deathLocation, Sound.SLIME_WALK, 2, 0.5f);
    }

    @Override
    public CustomSlime get() {
        return this;
    }

}
