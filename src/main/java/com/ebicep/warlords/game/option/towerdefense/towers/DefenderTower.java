
package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.towerdefense.attributes.Spawner;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefenderTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SpawnTroops spawnTroops;


    public DefenderTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(spawnTroops = new SpawnTroops(this));

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
        upgrades.add(new TowerUpgrade("Increased Defense", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("+10% Damage Resistance").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                spawnTroops.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Increased Health", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("+10% More Health").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                spawnTroops.setPveMasterUpgrade2(true);
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.DEFENDER_TOWER;
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


    private static class SpawnTroops extends AbstractAbility implements Spawner, HitBox {

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
                AbstractMob mob = Mob.TD_TOWER_DEFENDER.createMob(getSpawnLocation(tower));
                spawnedMobs.add((TowerDefenseTowerMob) mob);
                if (pveMasterUpgrade) {
                    ((TDTowerDefender) mob).increasedResistance = true;
                } else if (pveMasterUpgrade2) {
                    ((TDTowerDefender) mob).increasedHealth = true;
                }
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

    public static class TDTowerDefender extends TowerDefenseTowerMob {

        private boolean increasedResistance;
        private boolean increasedHealth;

        public TDTowerDefender(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Defender",
                    2000,
                    .3f,
                    0,
                    25,
                    25
            );
        }

        public TDTowerDefender(
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
                    maxMeleeDamage,
                    new AvengersStrike()
            );
        }

        @Override
        public Mob getMobRegistry() {
            return Mob.TD_TOWER_DEFENDER;
        }

        @Override
        public void onSpawn(PveOption option) {
            super.onSpawn(option);
            if (increasedResistance) {
                warlordsNPC.setDamageResistance(warlordsNPC.getSpec().getDamageResistance() + 10);
            }
            if (increasedHealth) {
                warlordsNPC.getHealth().addMultiplicativeModifierAdd("Increased Health", .1f);
            }
        }

    }

}
