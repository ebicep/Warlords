package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.*;

public class RevenantTower extends AbstractTower implements Upgradeable.Path2, Listener {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SpawnTroops spawnTroops;
    private final Set<WarlordsEntity> spawnedNearby = new HashSet<>();


    public RevenantTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(spawnTroops = new SpawnTroops(this));

        game.registerEvents(this);

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
        upgrades.add(new TowerUpgrade("Increased EHP", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("+10% Health").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                spawnTroops.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Increased Spawn Range + Troop Cap", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("+5 Block Spawn Range, +2 Spawn Count").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                spawnTroops.getHitBoxRadius().addAdditiveModifier("Upgrade 4", 5);
                spawnTroops.setMaxSpawnCount(spawnTroops.getMaxSpawnCount() + 2);
                spawnTroops.setPveMasterUpgrade2(true);
            }
        });
    }

    @EventHandler
    public void onMobSpawn(WarlordsMobSpawnEvent event) {
        AbstractMob mob = event.getMob();
        if (mob instanceof TDTowerRevenant) {
            return;
        }
        TowerDefenseOption.TowerDefenseMobData data = towerDefenseOption.getMobsMap().get(mob);
        if (data instanceof TowerDefenseOption.TowerDefenseDefendingMobData && mob.getWarlordsNPC().getTeam() == team) {
            spawnedNearby.add(mob.getWarlordsNPC());
        }
    }

    @EventHandler
    public void onMobKill(WarlordsDeathEvent event) {
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        if (spawnedNearby.remove(warlordsEntity)) {
            spawnTroops.setCurrentCooldown(0);
        }
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.REVENANT_TOWER;
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
        private int maxSpawnCount = 2;

        public SpawnTroops(AbstractTower tower) {
            super("Spawn Troops", 0, 0, Float.MAX_VALUE, 0, false);
            this.mobSpawnLocations = Spawner.getBlockSpawnLocations(tower, range);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                spawnedMobs.removeIf(mob -> mob.getWarlordsNPC() != null && mob.getWarlordsNPC().isDead());
                if (spawnedMobs.size() > maxSpawnCount) {
                    return true;
                }
                AbstractTower tower = warlordsTower.getTower();
                AbstractMob mob = Mob.TD_TOWER_REVENANT.createMob(getSpawnLocation(tower));
                spawnedMobs.add((TowerDefenseTowerMob) mob);
                if (pveMasterUpgrade) {
                    ((TDTowerRevenant) mob).increasedEHP = true;
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

        public int getMaxSpawnCount() {
            return maxSpawnCount;
        }

        public void setMaxSpawnCount(int maxSpawnCount) {
            this.maxSpawnCount = maxSpawnCount;
        }
    }

    public static class TDTowerRevenant extends TowerDefenseTowerMob {

        private boolean increasedEHP;

        public TDTowerRevenant(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Revenant",
                    250,
                    .3f,
                    0,
                    25,
                    25
            );
        }

        public TDTowerRevenant(
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
            return Mob.TD_TOWER_REVENANT;
        }

        @Override
        public void onSpawn(PveOption option) {
            super.onSpawn(option);
            if (increasedEHP) {
                warlordsNPC.getHealth().addMultiplicativeModifierAdd("Increased EHP", .1f);
            }
        }

    }

}
