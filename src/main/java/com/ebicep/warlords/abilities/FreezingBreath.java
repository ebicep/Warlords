package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FreezingBreath extends AbstractAbility {

    public int playersHit = 0;

    private final int slowDuration = 4;
    private int slowness = 35;
    private float hitbox = 10;
    private int maxAnimationTime = 12;

    public FreezingBreath() {
        super("Freezing Breath", 422, 585, 6.3f, 60, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Breathe cold air in a cone in front of you, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies hit and slowing them by "))
                               .append(Component.text(slowness + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(slowDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));

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

                wp.getWorld().spawnParticle(
                        Particle.CLOUD,
                        center.translateVector(wp.getWorld(), animationTimer / 2D, 0, 0),
                        5,
                        0,
                        0,
                        0,
                        0.6f,
                        null,
                        true
                );

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    wp.getWorld().spawnParticle(
                            Particle.FIREWORKS_SPARK,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width),
                            1,
                            0,
                            0,
                            0,
                            0,
                            null,
                            true
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
                        minDamageHeal * (pveMasterUpgrade ? 1.5f : 1),
                        maxDamageHeal * (pveMasterUpgrade ? 1.5f : 1),
                        critChance,
                        critMultiplier
                );
                breathTarget.addSpeedModifier(wp, "Freezing Breath", -slowness, slowDuration * 20);
            }
        }

        if (pveMasterUpgrade) {
            if (counter > 6) {
                counter = 6;
            }
            damageReductionOnHit(wp, counter);
        }

        return true;
    }

    private void damageReductionOnHit(WarlordsEntity we, int counter) {
        we.getCooldownManager().removeCooldown(FreezingBreath.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
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
                return currentDamageValue * (1 - (0.05f * counter));
            }
        });
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
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
