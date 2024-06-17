package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.effects.ChasingBlockEffect;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.Spawner;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EarthwardenTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SpikeAttack spikeAttack = new SpikeAttack();
    private SpawnTroops spawnTroops;

    public EarthwardenTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(spikeAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);

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
        upgrades.add(new TowerUpgrade("Attacks Slow Enemies", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("+10% More Health").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                spikeAttack.setPveMasterUpgrade2(true);
            }
        });
        upgrades.add(new TowerUpgrade("Powerful Mob", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("?!?!!?").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                warlordsTower.getAbilities().add(spawnTroops = new SpawnTroops(EarthwardenTower.this));
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.EARTHWARDEN_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
        }
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class SpikeAttack extends AbstractAbility implements TDAbility, HitBox {

        private static final int SLOW_TICKS = 20;
        private final FloatModifiable range = new FloatModifiable(30);

        public SpikeAttack() {
            super("Spike Attack", 350, 350, 6, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(EnemyTargetPriority.FIRST, range, 1)
                             .forEach(target -> attack(warlordsTower, target));
            }
            return true;
        }

        private void attack(WarlordsTower warlordsTower, WarlordsEntity target) {
            new ChasingBlockEffect.Builder()
                    .setGame(target.getGame())
                    .setSpeed(1.5f)
                    .setDestination(() -> target.isDead() ? null : target.getLocation())
                    .setOnTick(ticksElapsed -> {

                    })
                    .setOnDestinationReached(() -> {
                        Display display = target.getWorld().spawn(
                                new LocationBuilder(target.getLocation())
                                        .pitch(0)
                                ,
                                ItemDisplay.class,
                                d -> {
                                    d.setTransformation(new Transformation(
                                            new Vector3f(0, 2, 0),
                                            new AxisAngle4f(),
                                            new Vector3f(1.5f),
                                            new AxisAngle4f()
                                    ));
                                    d.setItemStack(new ItemStack(Material.BROWN_MUSHROOM));
                                }
                        );
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                display.remove();
                            }
                        }.runTaskLater(Warlords.getInstance(), 8);
                        target.addDamageInstance(
                                warlordsTower,
                                name,
                                minDamageHeal,
                                maxDamageHeal,
                                critChance,
                                critMultiplier,
                                InstanceFlags.TD_PHYSICAL
                        );
                        if (pveMasterUpgrade) {
                            target.addSpeedModifier(warlordsTower, name, -20, SLOW_TICKS);
                        }
                        PlayerFilter.entitiesAround(target, 3, 3, 3)
                                    .aliveTeammatesOf(target)
                                    .excluding(target)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addDamageInstance(
                                                warlordsTower,
                                                name,
                                                minDamageHeal,
                                                maxDamageHeal,
                                                critChance,
                                                critMultiplier,
                                                InstanceFlags.TD_PHYSICAL
                                        );
                                        if (pveMasterUpgrade) {
                                            warlordsEntity.addSpeedModifier(warlordsTower, name, -20, SLOW_TICKS);
                                        }
                                    });
                    })
                    .setMaxTicks(30)
                    .create()
                    .start(warlordsTower.getTower().getBottomCenterLocation());
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

    }

    private static class SpawnTroops extends AbstractAbility implements TDAbility, Spawner, HitBox {

        private final List<LocationUtils.LocationXYZ> mobSpawnLocations;
        private final List<TowerDefenseTowerMob> spawnedMobs = new ArrayList<>();
        private final FloatModifiable range = new FloatModifiable(30);

        public SpawnTroops(AbstractTower tower) {
            super("Spawn Troops", 0, 0, 5, 0);
            this.mobSpawnLocations = Spawner.getBlockSpawnLocations(tower, range);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                spawnedMobs.removeIf(mob -> mob.getWarlordsNPC() != null && mob.getWarlordsNPC().isDead());
                if (spawnedMobs.size() > 0) {
                    return true;
                }
                AbstractTower tower = warlordsTower.getTower();
                AbstractMob mob = Mob.TD_TOWER_EARTHWARDEN.createMob(getSpawnLocation(tower));
                spawnedMobs.add((TowerDefenseTowerMob) mob);
                tower.getTowerDefenseOption().spawnNewMob(mob, warlordsTower);
            }
            return true;
        }

        @Override
        public List<LocationUtils.LocationXYZ> getBlockSpawnLocations() {
            return mobSpawnLocations;
        }

        @Override
        public List<TowerDefenseTowerMob> getSpawnedMobs() {
            return spawnedMobs;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

    }

    public static class TDTowerEarthwarden extends TowerDefenseTowerMob {

        public TDTowerEarthwarden(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Earthwarden",
                    2000,
                    .3f,
                    0,
                    250,
                    250
            );
        }

        public TDTowerEarthwarden(
                Location spawnLocation,
                String name,
                int maxHealth,
                float walkSpeed,
                float damageResistance,
                float minMeleeDamage,
                float maxMeleeDamage
        ) {
            super(
                    spawnLocation,
                    name,
                    maxHealth,
                    walkSpeed,
                    damageResistance,
                    minMeleeDamage,
                    maxMeleeDamage
            );
        }

        @Override
        public Mob getMobRegistry() {
            return Mob.TD_TOWER_EARTHWARDEN;
        }
    }
}
