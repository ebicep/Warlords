package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
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

    public FreezingBreath() {
        super("Freezing Breath", 422, 585, 6.3f, 60, 20, 175);
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Breathe cold air in a cone in front\n" +
                "§7of you, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7to all enemies hit and slowing them by\n" +
                "§e35% §7for §6" + slowDuration + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "mage.freezingbreath.activation", 2, 1);

        Location playerLoc = new LocationBuilder(player.getLocation())
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
                if (animationTimer > 12) {
                    this.cancel();
                }

                ParticleEffect.CLOUD.display(0F, 0F, 0F, 0.6F, 5,
                        center.translateVector(player.getWorld(), animationTimer / 2D, 0, 0), 500);

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1,
                            center.translateVector(player.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                }

                animationTimer++;
            }
        }.runTaskTimer(0, 1);

        Location hitbox = new LocationBuilder(player.getLocation())
                .pitch(0)
                .backward(1);

        Vector viewDirection = playerLoc.getDirection();

        for (WarlordsEntity breathTarget : PlayerFilter
                .entitiesAroundRectangle(player, 7.5, 10, 7.5)
                .aliveEnemiesOf(wp)
        ) {
            playersHit++;
            Vector direction = breathTarget.getLocation().subtract(hitbox).toVector().normalize();
            if (viewDirection.dot(direction) > .68) {
                breathTarget.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier,
                        false
                );
                breathTarget.getSpeed().addSpeedModifier("Freezing Breath", -35, slowDuration * 20);
            }
        }

        return true;
    }
}
