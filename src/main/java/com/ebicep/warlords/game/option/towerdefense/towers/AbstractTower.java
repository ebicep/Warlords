package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.towerdefense.TowerPlayerClass;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
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

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    protected Location cornerLocation; // bottom left corner of tower
    protected Location centerLocation; // top center of tower
    protected WarlordsTower warlordsTower;
    protected NPC npc;
    protected Block[][][] blocks;
    private Team team = Team.BLUE; //TODO


    protected AbstractTower(Game game, Location cornerLocation) {
        this.game = game;
        this.cornerLocation = cornerLocation;
        double xzOffset = getSize() / 2.0;
        this.centerLocation = cornerLocation.clone().add(xzOffset, getHeight() + .25, xzOffset);
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

        npc.spawn(centerLocation);

        return npc;
    }

    /**
     * Facing from track to frontLeftCorner
     */
    public void build() {
        Utils.playGlobalSound(centerLocation, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2, 1);
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

    public List<WarlordsNPC> getNearbyMobs(float range, int limit) {
        return PlayerFilterGeneric.entitiesAround(centerLocation, range, range, range)
                                  .warlordsNPCs()
                                  .filter(warlordsNPC -> warlordsNPC.getTeam() != team)
                                  .stream()
                                  .limit(limit == -1 ? Long.MAX_VALUE : limit)
                                  .collect(Collectors.toList());
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

    public Location getCornerLocation() {
        return cornerLocation;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }
}
