package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CryomancerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SlowAttack slowAttack = new SlowAttack();

    public CryomancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(slowAttack);

//        TowerUpgradeInstance.DamageUpgradeInstance upgradeDamage1 = new TowerUpgradeInstance.DamageUpgradeInstance(25);
//        TowerUpgradeInstance.DamageUpgradeInstance upgradeDamage2 = new TowerUpgradeInstance.DamageUpgradeInstance(25);
//
//        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("Single Target Attack", upgradeDamage3) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("AOE Attack", upgradeDamage3) {});
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.CRYOMANCER_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class SlowAttack extends AbstractAbility implements TDAbility, HitBox {

        private static final int SLOW_TICKS = 40;
        private final FloatModifiable range = new FloatModifiable(30);

        public SlowAttack() {
            super("Slow", 2, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range).forEach(warlordsNPC -> {
                    EffectUtils.displayParticle(Particle.SNOWFLAKE, warlordsNPC.getLocation(), 15, .15, 0, .15, 0);
                    playIceBlockEffect(wp, warlordsNPC);

                    warlordsNPC.addSpeedModifier(warlordsTower, "Cryomancer Tower Slow", -20, SLOW_TICKS, "BASE");
                });
            }
            return true;
        }

        private static void playIceBlockEffect(@Nonnull WarlordsEntity wp, WarlordsNPC warlordsNPC) {
            BoundingBox boundingBox = warlordsNPC.getEntity().getBoundingBox();
            double x = boundingBox.getMaxX() - boundingBox.getMinX();
            double y = boundingBox.getMaxY() - boundingBox.getMinY();
            double z = boundingBox.getMaxZ() - boundingBox.getMinZ();
            BlockDisplay display = wp.getWorld().spawn(warlordsNPC.getLocation(), BlockDisplay.class, d -> {
                d.setBlock(Material.ICE.createBlockData());
                d.setTransformation(new Transformation(
                                new Vector3f(),
                                new AxisAngle4f(),
                                new Vector3f((float) x * 1.2f, (float) y * 1.1f, (float) z * 1.2f),
                                new AxisAngle4f()
                        )
                );
                d.setTeleportDuration(3);
            });
            new GameRunnable(warlordsNPC.getGame()) {

                final float xOffset = (float) (x / 2);
                final float zOffset = (float) (z / 2);
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead() || counter++ > SLOW_TICKS) {
                        display.remove();
                        this.cancel();
                        return;
                    }
                    Location location = new LocationBuilder(warlordsNPC.getLocation())
                            .yaw(0)
                            .pitch(0)
                            .right(xOffset)
                            .backward(zOffset);
                    display.teleport(location);
                }
            }.runTaskTimer(0, 0);
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

    }

}
