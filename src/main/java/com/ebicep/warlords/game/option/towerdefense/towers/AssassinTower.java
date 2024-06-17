package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.towerdefense.attributes.Spawner;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AssassinTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SpawnTroops spawnTroops;


    public AssassinTower(Game game, UUID owner, Location location) {
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
        upgrades.add(new TowerUpgrade("Immunity to Range Attacks", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("???").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                spawnTroops.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Gain a Range Attack", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("????").build();
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
        return TowerRegistry.ASSASSIN_TOWER;
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
                AbstractMob mob = Mob.TD_TOWER_ASSASSIN.createMob(getSpawnLocation(tower));
                spawnedMobs.add((TowerDefenseTowerMob) mob);
                if (pveMasterUpgrade) {
                    ((TDTowerAssassin) mob).immuneToRangeAttacks = true;
                } else if (pveMasterUpgrade2) {
                    ((TDTowerAssassin) mob).rangeAttack = true;
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

    public static class TDTowerAssassin extends TowerDefenseTowerMob {

        private boolean immuneToRangeAttacks;
        private boolean rangeAttack;

        public TDTowerAssassin(Location spawnLocation) {
            this(
                    spawnLocation,
                    "Assassin",
                    1000,
                    .3f,
                    0,
                    200,
                    200
            );
        }

        public TDTowerAssassin(
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
            return Mob.TD_TOWER_ASSASSIN;
        }

        @Override
        public void onSpawn(PveOption option) {
            super.onSpawn(option);
            if (rangeAttack) {
                warlordsNPC.getAbilities().add(new AssassinRangeAttack());
            }
        }

        @Override
        public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
            if (immuneToRangeAttacks && Utils.isProjectile(event.getCause())) {
                event.setCancelled(true);
            } else if (ThreadLocalRandom.current().nextDouble() < .3) {
                event.setCancelled(true);
                MobHologram.CustomHologramLine line = new MobHologram.CustomHologramLine(Component.text("Dodged!", NamedTextColor.GRAY));
                warlordsNPC.getMobHologram().getCustomHologramLines().add(line);
                new GameRunnable(self.getGame()) {
                    @Override
                    public void run() {
                        line.setDelete(true);
                    }
                }.runTaskLater(20);
            }
        }

        private static class AssassinRangeAttack extends AbstractAbility implements TDAbility {

            private static final double SPEED = 0.30;
            private static final double GRAVITY = -0.007;

            public AssassinRangeAttack() {
                super("Range Attack", 150, 150, 3, 0);
            }

            @Override
            public boolean onActivate(@Nonnull WarlordsEntity wp) {
                Utils.playGlobalSound(wp.getLocation(), "mage.frostbolt.activation", 2, 0.7f);

                Utils.spawnThrowableProjectile(
                        wp.getGame(),
                        Utils.spawnArmorStand(wp.getLocation(), armorStand -> armorStand.getEquipment().setHelmet(new ItemStack(Material.FIRE_CHARGE))),
                        wp.getLocation().getDirection().multiply(SPEED),
                        GRAVITY,
                        SPEED,
                        (newLoc, integer) -> {},
                        newLoc -> PlayerFilter
                                .entitiesAroundRectangle(newLoc, 1, 2, 1)
                                .aliveEnemiesOf(wp)
                                .findFirstOrNull(),
                        (newLoc, directHit) -> {
                            List<WarlordsEntity> enemies = PlayerFilter
                                    .entitiesAround(newLoc, 2, 2, 2)
                                    .aliveEnemiesOf(wp)
                                    .toList();
                            for (WarlordsEntity nearEntity : enemies) {
                                nearEntity.addDamageInstance(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        InstanceFlags.TD_PHYSICAL
                                );
                            }
                        }
                );
                return true;
            }
        }
    }

}
