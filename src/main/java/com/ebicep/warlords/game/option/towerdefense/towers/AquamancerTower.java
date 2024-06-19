package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AquamancerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final BoltAttack boltAttack = new BoltAttack();

    public AquamancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(boltAttack);

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
        return TowerRegistry.AQUAMANCER_TOWER;
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

    private static class BoltAttack extends AbstractAbility implements TDAbility, HitBox {

        private static final double WATER_EFFECT_RANDOMNESS = .1;
        private static final float WATER_EFFECT_BLOCK_SCALE = .3f;
        private final FloatModifiable range = new FloatModifiable(30);

        public BoltAttack() {
            super("Bolt Attack", 250, 250, 1, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range, 1).forEach(warlordsNPC -> {
                    Entity npcEntity = warlordsNPC.getEntity();
                    Location effectLocation = warlordsNPC.getLocation().add(0, npcEntity.getHeight(), 0);
                    Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
                    EffectUtils.playCircularEffectAround(
                            Particle.FALLING_WATER,
                            effectLocation,
                            npcEntity.getBoundingBox().getWidthX(),
                            15
                    );
                    playWaterEffect(wp, warlordsNPC);
                    warlordsNPC.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.boltDamage)
                    );
                });
//            getAllyMob(rangeValue, 1).forEach(warlordsNPC -> {
//                Entity npcEntity = warlordsNPC.getEntity();
//                Location effectLocation = warlordsNPC.getLocation().add(0, npcEntity.getHeight(), 0);
//                Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
//                EffectUtils.playCircularEffectAround(
//                        Particle.DRIP_WATER,
//                        effectLocation,
//                        npcEntity.getBoundingBox().getWidthX()
//                );
//                warlordsNPC.addHealingInstance(
//                        warlordsTower,
//                        "Water",
//                        healValue,
//                        healValue,
//                        0,
//                        100
//                );
//            });
            }
            return true;
        }

        private static void playWaterEffect(@Nonnull WarlordsEntity wp, WarlordsNPC warlordsNPC) {
            Location targetLocation = warlordsNPC.getLocation().add(0, warlordsNPC.getEntity().getHeight() / 2, 0);
            LocationBuilder locationBuilder = new LocationBuilder(wp.getEyeLocation())
                    .faceTowards(targetLocation)
                    .right(.1f);
            targetLocation.setDirection(locationBuilder.getDirection());
            for (int i = 0; i < 10; i++) {
                LocationBuilder current = locationBuilder
                        .clone()
                        .forward(MathUtils.generateRandomValueBetweenInclusive(-.5, .5))
                        .left(MathUtils.generateRandomValueBetweenInclusive(-WATER_EFFECT_RANDOMNESS, WATER_EFFECT_RANDOMNESS))
                        .addY(MathUtils.generateRandomValueBetweenInclusive(-WATER_EFFECT_RANDOMNESS, WATER_EFFECT_RANDOMNESS));
                int teleportDuration = 1 + i;
                BlockDisplay display = wp.getWorld().spawn(current, BlockDisplay.class, d -> {
                    Material material = ThreadLocalRandom.current().nextBoolean() ? Material.LIGHT_BLUE_WOOL : Material.BLUE_GLAZED_TERRACOTTA;
                    d.setBlock(material.createBlockData());
                    d.setTransformation(new Transformation(
                            new Vector3f(),
                            new AxisAngle4f(),
                            new Vector3f(WATER_EFFECT_BLOCK_SCALE, WATER_EFFECT_BLOCK_SCALE, WATER_EFFECT_BLOCK_SCALE),
                            new AxisAngle4f(
                                    1f,
                                    ThreadLocalRandom.current().nextFloat(),
                                    ThreadLocalRandom.current().nextFloat(),
                                    ThreadLocalRandom.current().nextFloat()
                            )
                    ));
                    d.setTeleportDuration(teleportDuration);
                });
                if (i >= 6) {
                    locationBuilder.backward((float) MathUtils.generateRandomValueBetweenInclusive(.8, 1));
                }

                display.teleport(targetLocation);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        display.remove();
                    }
                }.runTaskLater(Warlords.getInstance(), teleportDuration);
            }
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue boltDamage = new Value.SetValue(250);
            private final List<Value> values = List.of(boltDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

}
