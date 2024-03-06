package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.game.option.towerdefense.attributes.Spawner;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AvengerTower extends AbstractTower implements Damage, Range, AttackSpeed, Spawner, Upgradeable.Path2 {

    private final List<FloatModifiable> damage = new ArrayList<>();
    private final List<FloatModifiable> range = new ArrayList<>();
    private final List<FloatModifiable> attackSpeed = new ArrayList<>();
    private final List<TowerUpgrade> upgrades = new ArrayList<>();

    private final FloatModifiable slowDamage = new FloatModifiable(500);
    private final FloatModifiable slowRange = new FloatModifiable(10);
    private final FloatModifiable slowAttackSpeed = new FloatModifiable(3 * 20); // 5 seconds

    private final List<LocationUtils.LocationXYZ> mobSpawnLocations;
    private final List<TowerDefenseTowerMob> spawnedMobs = new ArrayList<>();

    public AvengerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);
        range.add(slowRange);
        attackSpeed.add(slowAttackSpeed);
        mobSpawnLocations = Spawner.getBlockSpawnLocations(bottomCenterLocation.clone().add(0, -1, 0), slowRange.getCalculatedValue(), towerDefenseOption.getMobPathMaterial());
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
        return TowerRegistry.AVENGER_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
        int attackSpeed = (int) slowAttackSpeed.getCalculatedValue();
        if (ticksElapsed % attackSpeed == 0) {
            spawnedMobs.removeIf(mob -> mob.getWarlordsNPC() != null && mob.getWarlordsNPC().isDead());
            if (spawnedMobs.size() > 0) {
                return;
            }
            AbstractMob mob = Mob.TD_TOWER_AVENGER.createMob(getSpawnLocation(this));
            spawnedMobs.add((TowerDefenseTowerMob) mob);
            towerDefenseOption.spawnNewMob(mob, warlordsTower);
        }
    }


    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public List<FloatModifiable> getDamages() {
        return damage;
    }

    @Override
    public List<FloatModifiable> getRanges() {
        return range;
    }

    @Override
    public List<FloatModifiable> getAttackSpeeds() {
        return attackSpeed;
    }

    @Override
    public List<LocationUtils.LocationXYZ> getBlockSpawnLocations() {
        return mobSpawnLocations;
    }

    @Override
    public List<TowerDefenseTowerMob> getSpawnedMobs() {
        return spawnedMobs;
    }

    public static class TDTowerAvenger extends TowerDefenseTowerMob {


        public TDTowerAvenger(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Zombie",
                    1000,
                    .3f,
                    0,
                    100,
                    100
            );
        }

        public TDTowerAvenger(
                Location spawnLocation,
                String name,
                int maxHealth,
                float walkSpeed,
                int damageResistance,
                float minMeleeDamage,
                float maxMeleeDamage,
                AbstractAbility... abilities
        ) {
            super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
        }

        @Override
        public Mob getMobRegistry() {
            return Mob.TD_ZOMBIE;
        }

        @Override
        public void whileAlive(int ticksElapsed, PveOption option) {

        }

    }

}
