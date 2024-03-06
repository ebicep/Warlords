package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.game.option.towerdefense.TowerPlayerClass;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.Gravity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTower {

    /**
     * @param frontRightCorner ALWAYS BUILD TOWER FACING SOUTH AT FRONT RIGHT CORNER or else directions of some blocks will be wrong
     * @param data             3d array of block data
     * @return
     */
    public static Block[][][] build(Location frontRightCorner, BlockData[][][] data) {
        LocationBuilder builder = new LocationBuilder(frontRightCorner)
                .pitch(0)
                .yaw((float) (Math.round(frontRightCorner.getYaw() / 90) * 90));
        // build one strip going up at a time
        int maxX = data.length;
        int maxZ = data[0].length;
        int maxY = data[0][0].length;
        Block[][][] builtBlocks = new Block[maxX][maxZ][maxY];
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                for (int z = 0; z < maxZ; z++) {
                    BlockData blockData = data[x][z][y];
                    Block block = builder.clone()
                                         .forward(z)
                                         .addY(y)
                                         .left(x)
                                         .getBlock();
                    block.setBlockData(blockData);
                    builtBlocks[x][z][y] = block;

                    // move entities up if in the way TODO maybe change to just tp to center
                    if (y != maxY - 1) {
                        block.getLocation()
                             .toCenterLocation()
                             .getNearbyEntities(.5, .5, .5)
                             .forEach(entity -> entity.teleport(entity.getLocation().add(0, 1, 0)));
                    }
                }
            }
        }
        return builtBlocks;
    }

    protected UUID owner; // person who built the tower
    protected Game game;
    protected TowerDefenseOption towerDefenseOption;
    protected Location cornerLocation; // bottom left corner of tower
    protected Location topCenterLocation; // top center of tower
    protected Location bottomCenterLocation; // top center of tower
    protected WarlordsTower warlordsTower;
    protected NPC npc;
    protected Block[][][] blocks;
    protected Team team; //TODO


    protected AbstractTower(Game game, UUID owner, Location cornerLocation) {
        this.game = game;
        this.owner = owner;
        this.team = Objects.requireNonNull(Warlords.getPlayer(owner)).getTeam();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption defenseOption) {
                this.towerDefenseOption = defenseOption;
                break;
            }
        }
        this.cornerLocation = cornerLocation;
        double xzOffset = getSize() / 2.0;
        this.topCenterLocation = cornerLocation.clone().add(xzOffset, getHeight() + .25, xzOffset);
        this.bottomCenterLocation = cornerLocation.clone().add(xzOffset, 0, xzOffset);
        this.npc = createNPC();
        this.warlordsTower = new WarlordsTower(UUID.randomUUID(), getName(), npc.getEntity(), game, team, new TowerPlayerClass());

        build();
    }

    public int getSize() {
        return getTowerRegistry().getSize();
    }

    public int getHeight() {
        return getTowerRegistry().getHeight();
    }

    protected NPC createNPC() {
        NPC npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.ARMOR_STAND, getName());
        npc.data().set(NPC.Metadata.COLLIDABLE, false);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, true);

        npc.getDefaultGoalController().clear();
        npc.getNavigator().setPaused(true);

        npc.getOrAddTrait(Gravity.class).gravitate(true);
        ArmorStandTrait armorStandTrait = npc.getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setMarker(true);

        npc.spawn(topCenterLocation);

        return npc;
    }

    /**
     * Facing from track to frontLeftCorner
     */
    public void build() {
        Utils.playGlobalSound(topCenterLocation, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2, 1);
        blocks = build(cornerLocation, getTowerRegistry().baseTowerData);
        forEachBlock(block -> {
            block.setMetadata("TOWER", new FixedMetadataValue(Warlords.getInstance(), this));
            return false;
        });
    }

    public abstract TowerRegistry getTowerRegistry();


    public void whileActive(int ticksElapsed) {

    }

    public void updateAttributes() {
        if (this instanceof AttackSpeed attackSpeed) {
            updateFloatModifiables(attackSpeed.getAttackSpeeds());
        }
        if (this instanceof Damage damage) {
            updateFloatModifiables(damage.getDamages());
        }
        if (this instanceof Range range) {
            updateFloatModifiables(range.getRanges());
        }
    }

    private void updateFloatModifiables(List<FloatModifiable> floatModifiables) {
        for (FloatModifiable floatModifiable : floatModifiables) {
            floatModifiable.tick();
        }
    }

    public List<WarlordsNPC> getAllyMob(float range, int limit) {
        return getAllyMob(null, range, limit);
    }

    public List<WarlordsNPC> getAllyMob(float range) {
        return getAllyMob(null, range, -1);
    }

    public List<WarlordsNPC> getAllyMob(@Nullable AllyTargetPriority targetPriority, float range, int limit) {
        ConcurrentHashMap<AbstractMob, TowerDefenseOption.TowerDefenseMobData> mobData = towerDefenseOption.getMobsMap();
        Stream<WarlordsNPC> stream = PlayerFilterGeneric
                .entitiesAround(bottomCenterLocation, range, range, range)
                .warlordsNPCs()
                .filter(warlordsNPC -> mobData.get(warlordsNPC.getMob()) instanceof TowerDefenseOption.TowerDefenseDefendingMobData data && warlordsNPC.getTeam() == team)
                .stream();
        if (targetPriority != null) {
            stream = stream.sorted((o1, o2) -> targetPriority.compare(this,
                    new AllyTargetPriority.AllyTargetPriorityMob(o1, mobData.get(o1.getMob())),
                    new AllyTargetPriority.AllyTargetPriorityMob(o2, mobData.get(o2.getMob()))
            ));
        }
        if (limit != -1) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    public List<WarlordsNPC> getEnemyMobs(float range, int limit) {
        return getEnemyMobs(null, range, limit);
    }

    public List<WarlordsNPC> getEnemyMobs(float range) {
        return getEnemyMobs(null, range, -1);
    }

    public List<WarlordsNPC> getEnemyMobs(@Nullable EnemyTargetPriority targetPriority, float range, int limit) {
        ConcurrentHashMap<AbstractMob, TowerDefenseOption.TowerDefenseMobData> mobData = towerDefenseOption.getMobsMap();
        Stream<WarlordsNPC> stream = PlayerFilterGeneric
                .entitiesAround(bottomCenterLocation, range, range, range)
                .warlordsNPCs()
                .filter(warlordsNPC -> mobData.get(warlordsNPC.getMob()) instanceof TowerDefenseOption.TowerDefenseAttackingMobData data && data.getAttackingTeam() == team)
                .stream();
        if (targetPriority != null) {
            stream = stream.sorted((o1, o2) -> targetPriority.compare(this,
                    new EnemyTargetPriority.EnemyTargetPriorityMob(o1, (TowerDefenseOption.TowerDefenseAttackingMobData) mobData.get(o1.getMob())),
                    new EnemyTargetPriority.EnemyTargetPriorityMob(o2, (TowerDefenseOption.TowerDefenseAttackingMobData) mobData.get(o2.getMob()))
            ));
        }
        if (limit != -1) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    public List<AbstractTower> getTowers(float range) {
        return getTowers(range, -1);
    }

    public List<AbstractTower> getTowers(float range, int limit) {
        Stream<AbstractTower> stream = towerDefenseOption
                .getTowerBuildOption()
                .getBuiltTowers()
                .keySet()
                .stream()
                .filter(tower -> tower.getTeam() == team)
                .filter(tower -> tower.bottomCenterLocation.distanceSquared(bottomCenterLocation) <= range * range);
        if (limit != -1) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    public void remove() {
        forEachBlock(block -> {
            block.setType(Material.AIR);
            return false;
        });
        npc.destroy();
        game.getPlayers().remove(warlordsTower.getUuid());
        Warlords.removePlayer(warlordsTower.getUuid());
    }

    /**
     * @param consumer return true to break loop
     * @return true if loop was broken
     */
    public boolean forEachBlock(Function<Block, Boolean> consumer) {
        Block[][][] builtBlocks = getBlocks();
        for (Block[][] builtBlock : builtBlocks) {
            for (Block[] blocks : builtBlock) {
                for (Block block : blocks) {
                    if (consumer.apply(block)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public String getName() {
        return getTowerRegistry().name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Game getGame() {
        return game;
    }

    public Team getTeam() {
        return team;
    }

    public Location getCornerLocation() {
        return cornerLocation;
    }

    public Location getTopCenterLocation() {
        return topCenterLocation;
    }

    public Location getBottomCenterLocation() {
        return bottomCenterLocation;
    }

    public enum EnemyTargetPriority {
        FIRST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                return Integer.compare(o1.mobData().getPosition(), o2.mobData().getPosition());
            }
        },
        LAST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                return Integer.compare(o2.mobData().getPosition(), o1.mobData().getPosition());
            }
        },
        STRONGEST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                return o1.warlordsNPC().getCurrentHealth() > o2.warlordsNPC().getCurrentHealth() ? -1 : 1;
            }
        },
        WEAKEST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                return o1.warlordsNPC().getCurrentHealth() < o2.warlordsNPC().getCurrentHealth() ? -1 : 1;
            }
        },
        CLOSEST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                Location location = tower.getTopCenterLocation();
                return Double.compare(o1.warlordsNPC().getLocation().distanceSquared(location), o2.warlordsNPC().getLocation().distanceSquared(location));
            }
        },
        FURTHEST {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                Location location = tower.getTopCenterLocation();
                return Double.compare(o2.warlordsNPC().getLocation().distanceSquared(location), o1.warlordsNPC().getLocation().distanceSquared(location));
            }
        },
        RANDOM {
            @Override
            public int compare(AbstractTower tower, EnemyTargetPriorityMob o1, EnemyTargetPriorityMob o2) {
                return ThreadLocalRandom.current().nextDouble() < .5 ? -1 : 1;
            }
        },


        ;

        public static final EnemyTargetPriority[] VALUES = values();


        public abstract int compare(
                AbstractTower tower,
                EnemyTargetPriorityMob o1,
                EnemyTargetPriorityMob o2
        );

        public record EnemyTargetPriorityMob(WarlordsNPC warlordsNPC, TowerDefenseOption.TowerDefenseAttackingMobData mobData) {
        }
    }

    public enum AllyTargetPriority {
        STRONGEST {
            @Override
            public int compare(AbstractTower tower, AllyTargetPriorityMob o1, AllyTargetPriorityMob o2) {
                return o1.warlordsNPC().getCurrentHealth() > o2.warlordsNPC().getCurrentHealth() ? -1 : 1;
            }
        },
        WEAKEST {
            @Override
            public int compare(AbstractTower tower, AllyTargetPriorityMob o1, AllyTargetPriorityMob o2) {
                return o1.warlordsNPC().getCurrentHealth() < o2.warlordsNPC().getCurrentHealth() ? -1 : 1;
            }
        },
        CLOSEST {
            @Override
            public int compare(AbstractTower tower, AllyTargetPriorityMob o1, AllyTargetPriorityMob o2) {
                Location location = tower.getTopCenterLocation();
                return Double.compare(o1.warlordsNPC().getLocation().distanceSquared(location), o2.warlordsNPC().getLocation().distanceSquared(location));
            }
        },
        FURTHEST {
            @Override
            public int compare(AbstractTower tower, AllyTargetPriorityMob o1, AllyTargetPriorityMob o2) {
                Location location = tower.getTopCenterLocation();
                return Double.compare(o2.warlordsNPC().getLocation().distanceSquared(location), o1.warlordsNPC().getLocation().distanceSquared(location));
            }
        },
        RANDOM {
            @Override
            public int compare(AbstractTower tower, AllyTargetPriorityMob o1, AllyTargetPriorityMob o2) {
                return ThreadLocalRandom.current().nextDouble() < .5 ? -1 : 1;
            }
        },


        ;

        public static final AllyTargetPriority[] VALUES = values();


        public abstract int compare(
                AbstractTower tower,
                AllyTargetPriorityMob o1,
                AllyTargetPriorityMob o2
        );

        public record AllyTargetPriorityMob(WarlordsNPC warlordsNPC, TowerDefenseOption.TowerDefenseMobData mobData) {
        }
    }
}
