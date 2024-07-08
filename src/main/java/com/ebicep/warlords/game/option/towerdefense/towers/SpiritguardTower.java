package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.SpiritLink;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.Spawner;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpiritguardTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SpiritAttack spiritAttack;
    private SpawnTroops spawnTroops;


    public SpiritguardTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(spiritAttack = new SpiritAttack());

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);

        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Reduce Enemy Damage", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("????").build();
            }
        }) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Summons Mobs", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("????").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                warlordsTower.getAbilities().add(spawnTroops = new SpawnTroops(SpiritguardTower.this));
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.SPIRITGUARD_TOWER;
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

    private static class SpiritAttack extends AbstractAbility implements TDAbility, HitBox, Damages<SpiritAttack.DamageValues> {

        private final FloatModifiable range = new FloatModifiable(30);

        public SpiritAttack() {
            super("Spirit Attack", 2, 0);
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
            EffectUtils.playChainAnimation(warlordsTower, target, SpiritLink.CHAIN_ITEM, 3);
            target.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(warlordsTower)
                    .value(damageValues.spiritAttackDamage)
                    .flags(InstanceFlags.TD_MAGIC)
            );
            PlayerFilter.entitiesAround(target, 3, 3, 3)
                        .aliveTeammatesOf(target)
                        .excluding(target)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(warlordsTower)
                                    .value(damageValues.spiritAttackDamage)
                                    .flags(InstanceFlags.TD_MAGIC)
                            );
                        });
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue spiritAttackDamage = new Value.SetValue(200);
            private final List<Value> values = List.of(spiritAttackDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }


    private static class SpawnTroops extends AbstractAbility implements TDAbility, Spawner, HitBox {

        private final List<LocationUtils.LocationXYZ> mobSpawnLocations;
        private final List<TowerDefenseTowerMob> spawnedMobs = new ArrayList<>();
        private final FloatModifiable range = new FloatModifiable(30);

        public SpawnTroops(AbstractTower tower) {
            super("Spawn Troops", 5, 0);
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
                AbstractMob mob = Mob.TD_TOWER_SPIRITGUARD.createMob(getSpawnLocation(tower));
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

    public static class TDTowerSpiritguard extends TowerDefenseTowerMob {


        public TDTowerSpiritguard(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Spritguard",
                    1000,
                    .3f,
                    0,
                    100,
                    100
            );
        }

        public TDTowerSpiritguard(
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
            return Mob.TD_TOWER_SPIRITGUARD;
        }

    }

}
