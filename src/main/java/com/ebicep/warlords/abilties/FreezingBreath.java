package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FreezingBreath extends AbstractAbility {
    private final int slowDuration = 4;
    protected int playersHit = 0;
    private boolean pveUpgrade = false;
    private int slowness = 35;

    private float hitbox = 10;
    private int maxAnimationTime = 12;

    public FreezingBreath() {
        super("Freezing Breath", 422, 585, 6.3f, 60, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Breathe cold air in a cone in front of you, dealing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage to all enemies hit and slowing them by §e" + slowness + "% §7for §6" + slowDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "mage.freezingbreath.activation", 2, 1);

        Location playerLoc = new LocationBuilder(wp.getLocation())
                .pitch(0)
                .add(0, 1.7, 0);

        new GameRunnable(wp.getGame()) {

            final Matrix4d center = new Matrix4d(playerLoc);
            int animationTimer = 0;

            @Override
            public void run() {
                this.playEffect();
                this.playEffect();
            }

            public void playEffect() {
                if (animationTimer > maxAnimationTime) {
                    this.cancel();
                }

                ParticleEffect.CLOUD.display(0F, 0F, 0F, 0.6F, 5,
                        center.translateVector(wp.getWorld(), animationTimer / 2D, 0, 0), 500
                );

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500
                    );
                }

                animationTimer++;
            }
        }.runTaskTimer(0, 1);

        Location playerEyeLoc = new LocationBuilder(wp.getLocation())
                .pitch(0)
                .backward(1);

        Vector viewDirection = playerLoc.getDirection();

        int counter = 0;
        for (WarlordsEntity breathTarget : PlayerFilter
                .entitiesAroundRectangle(wp, hitbox - 2.5, hitbox, hitbox - 2.5)
                .aliveEnemiesOf(wp)
        ) {
            counter++;
            playersHit++;
            Vector direction = breathTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
            if (viewDirection.dot(direction) > .68) {
                breathTarget.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal * (pveUpgrade ? 1.8f : 1),
                        maxDamageHeal * (pveUpgrade ? 1.8f : 1),
                        critChance,
                        critMultiplier,
                        false
                );
                breathTarget.getSpeed().addSpeedModifier("Freezing Breath", -slowness, slowDuration * 20);
            }
        }

        if (pveUpgrade) {
            if (counter > 5) {
                counter = 5;
            }
            damageReductionOnHit(wp, counter);
        }

        return true;
    }

    private void damageReductionOnHit(WarlordsEntity we, int counter) {
        we.getCooldownManager().removeCooldown(FreezingBreath.class);
        we.getCooldownManager().addCooldown(new RegularCooldown<FreezingBreath>(
                name,
                "FRZ RES",
                FreezingBreath.class,
                new FreezingBreath(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                4 * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - (0.04f * counter));
            }
        });
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getMaxAnimationTime() {
        return maxAnimationTime;
    }

    public void setMaxAnimationTime(int maxAnimationTime) {
        this.maxAnimationTime = maxAnimationTime;
    }

    public int getSlowness() {
        return slowness;
    }

    public void setSlowness(int slowness) {
        this.slowness = slowness;
    }
}
