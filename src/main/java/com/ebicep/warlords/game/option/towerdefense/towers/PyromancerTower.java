package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PyromancerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final FlameAttack flameAttack = new FlameAttack();

    public PyromancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(flameAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage3 = new TowerUpgradeInstance.Damage(50);

        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
            @Override
            public void onUpgrade() {
//                flameDamage.addAdditiveModifier(name, upgradeDamage1.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
//                flameDamage.addAdditiveModifier(name, upgradeDamage2.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Future Damage + Minor AOE", upgradeDamage3) {
            @Override
            public void onUpgrade() {
//                flameDamage.addAdditiveModifier(name, upgradeDamage3.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Burn", upgradeDamage3) {});
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.PYROMANCER_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, topCenterLocation, 5, .5, .1, .5, 2);
        }
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class FlameAttack extends AbstractAbility implements TDAbility, HitBox {

        private static final int TELEPORT_DURATION = 5; // arrow teleport duration
        private final FloatModifiable range = new FloatModifiable(30);

        public FlameAttack() {
            super("Flame Attack", 500, 500, 10, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                // TODO spread priority
                warlordsTower.getTower().getEnemyMobs(EnemyTargetPriority.FIRST, range, 1).forEach(target -> attack(warlordsTower, target));
            }
            return true;
        }

        private void attack(WarlordsTower warlordsTower, WarlordsNPC target) {
            Location targetLocation = new LocationBuilder(target.getLocation())
                    .addY(1);
            LocationBuilder startLocation = new LocationBuilder(warlordsTower.getLocation())
                    .addY(-.5)
                    .faceTowards(targetLocation);
            playSpiralFacingEffect(startLocation, targetLocation, Particle.SMALL_FLAME);
            playSpiralFacingEffect(startLocation.clone().forward(1.1f), targetLocation, Particle.DRAGON_BREATH);

            ItemDisplay arrow = fireArrowTowards(startLocation, targetLocation);
            new BukkitRunnable() {
                @Override
                public void run() {
                    target.addInstance(InstanceBuilder
                            .damage()
                            .ability(FlameAttack.this)
                            .source(warlordsTower)
                            .value(damageValues.flameDamage)
                    );
                    if (pveMasterUpgrade) {
                        PlayerFilter.entitiesAround(target, 2, 2, 2)
                                    .aliveEnemiesOf(warlordsTower)
                                    .excluding(target)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(FlameAttack.this)
                                                .source(warlordsTower)
                                                .value(damageValues.flameDamage)
                                                .flags(InstanceFlags.TD_MAGIC)
                                        );
                                    });
                    } else if (pveMasterUpgrade2) {
                        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Pyromancer Tower Burn",
                                "BRN",
                                PyromancerTower.class,
                                null,
                                warlordsTower,
                                CooldownTypes.DEBUFF,
                                cooldownManager -> {
                                },
                                60,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksLeft % 20 == 0) {
                                        float healthDamage = target.getMaxHealth() * 0.005f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        target.addInstance(InstanceBuilder
                                                .damage()
                                                .cause("Burn")
                                                .source(warlordsTower)
                                                .value(healthDamage)
                                                .flags(InstanceFlags.RECURSIVE)
                                        );
                                    }
                                })
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * 1.2f;
                            }
                        });
                    }
                    EffectUtils.displayParticle(Particle.LAVA, target.getLocation().clone().add(0, 1, 0), 15, 0.5F, 0, 0.5F, 500);
                    arrow.remove();
                }
            }.runTaskLater(Warlords.getInstance(), TELEPORT_DURATION);
        }

        private static void playSpiralFacingEffect(Location startLocation, Location targetLocation, Particle particle) {
            double width = .6;

            LocationBuilder builder = new LocationBuilder(startLocation);
            for (int ticksLived = 0; ticksLived < 200; ticksLived++) {
                Matrix4d center = new Matrix4d(builder);
                for (float i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
                    EffectUtils.displayParticle(
                            particle,
                            center.translateVector(builder.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                            3
                    );
                }
                builder.forward(.4);
                if (builder.distanceSquared(targetLocation) < 2) {
                    break;
                }
            }
        }

        private static ItemDisplay fireArrowTowards(Location startLocation, Location endLocation) {
            LocationBuilder start = new LocationBuilder(startLocation);
            float pitchTowards = start.getPitch();
            start.pitch(0); // make arrow straight
            start.yaw(start.getYaw() - 90); // rotate arrow to face the right direction
            LocationBuilder end = new LocationBuilder(endLocation).direction(start.getDirection());
            ItemStack item = new ItemStack(Material.TIPPED_ARROW);
            PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
            itemMeta.setBasePotionType(PotionType.INSTANT_HEAL);
            item.setItemMeta(itemMeta);
            ItemDisplay arrow = startLocation.getWorld().spawn(
                    start,
                    ItemDisplay.class,
                    itemDisplay -> {
                        itemDisplay.setItemStack(item);
                        itemDisplay.setTransformation(new Transformation(
                                        new Vector3f(),
                                        new AxisAngle4f((float) Math.toRadians(45 + pitchTowards), 0, 0, 1),
                                        new Vector3f(2f),
                                        new AxisAngle4f()
                                )
                        );
                        itemDisplay.setTeleportDuration(FlameAttack.TELEPORT_DURATION);
                    }
            );
            arrow.teleport(end);
            return arrow;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue flameDamage = new Value.SetValue(500);
            private final List<Value> values = List.of(flameDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
