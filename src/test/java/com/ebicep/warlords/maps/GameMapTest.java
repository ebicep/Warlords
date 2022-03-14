package com.ebicep.warlords.maps;

import com.avaje.ebean.config.ServerConfig;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemFactory;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class GameMapTest {

    private final GameMap map;
    private final GameMode category;

    public GameMapTest(GameMap map, GameMode category) {
        this.map = map;
        this.category = category;
    }
    
    @Before
    public void before() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, new MockServer());
    }
    
    @Before
    public void after() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Parameterized.Parameters(name = "{index} [{0}:{1}]")
    public static Collection<Object[]> allMaps() {
        return Arrays.stream(GameMap.values()).flatMap(m -> m.getCategories().stream().map(c -> new Object[]{m, c})).collect(Collectors.toList());
    }

    @Test
    public void testMapConfiguration() {
        List<Option> options = map.initMap(category, new LocationFactory(new WorldMock()), EnumSet.noneOf(GameAddon.class));
        for (Option option : options) {
            option.checkConflicts(options);
        }
    }

    static class WorldMock implements World {

        @Override
        public Block getBlockAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Block getBlockAt(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getBlockTypeIdAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getBlockTypeIdAt(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getHighestBlockYAt(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getHighestBlockYAt(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Block getHighestBlockAt(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Block getHighestBlockAt(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Chunk getChunkAt(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Chunk getChunkAt(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Chunk getChunkAt(Block block) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isChunkLoaded(Chunk chunk) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Chunk[] getLoadedChunks() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void loadChunk(Chunk chunk) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isChunkLoaded(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isChunkInUse(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void loadChunk(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean loadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunk(Chunk chunk) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunk(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunkRequest(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadChunkRequest(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean regenerateChunk(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean refreshChunk(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Item dropItem(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Item dropItemNaturally(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean generateTree(Location lctn, TreeType tt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Entity spawnEntity(Location lctn, EntityType et) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LivingEntity spawnCreature(Location lctn, EntityType et) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LivingEntity spawnCreature(Location lctn, CreatureType ct) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LightningStrike strikeLightning(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LightningStrike strikeLightningEffect(Location lctn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Entity> getEntities() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<LivingEntity> getLivingEntities() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Entity> getEntitiesByClasses(Class<?>... types) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Player> getPlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<Entity> getNearbyEntities(Location lctn, double d, double d1, double d2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public UUID getUID() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Location getSpawnLocation() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean setSpawnLocation(int i, int i1, int i2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getTime() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setTime(long l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getFullTime() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setFullTime(long l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasStorm() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setStorm(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getWeatherDuration() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setWeatherDuration(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isThundering() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setThundering(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getThunderDuration() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setThunderDuration(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createExplosion(Location lctn, float f) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean createExplosion(Location lctn, float f, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public World.Environment getEnvironment() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getSeed() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getPVP() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPVP(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ChunkGenerator getGenerator() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void save() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<BlockPopulator> getPopulators() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public FallingBlock spawnFallingBlock(Location lctn, Material mtrl, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public FallingBlock spawnFallingBlock(Location lctn, int i, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void playEffect(Location lctn, Effect effect, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void playEffect(Location lctn, Effect effect, int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> void playEffect(Location lctn, Effect effect, T t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> void playEffect(Location lctn, Effect effect, T t, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setSpawnFlags(boolean bln, boolean bln1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getAllowAnimals() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getAllowMonsters() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Biome getBiome(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBiome(int i, int i1, Biome biome) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getTemperature(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getHumidity(int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getMaxHeight() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getSeaLevel() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getKeepSpawnInMemory() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setKeepSpawnInMemory(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isAutoSave() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setAutoSave(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setDifficulty(Difficulty dfclt) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Difficulty getDifficulty() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public File getWorldFolder() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WorldType getWorldType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canGenerateStructures() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getTicksPerAnimalSpawns() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setTicksPerAnimalSpawns(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getTicksPerMonsterSpawns() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setTicksPerMonsterSpawns(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getMonsterSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setMonsterSpawnLimit(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getAnimalSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getWaterAnimalSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setWaterAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getAmbientSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setAmbientSpawnLimit(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void playSound(Location lctn, Sound sound, float f, float f1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String[] getGameRules() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getGameRuleValue(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean setGameRuleValue(String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isGameRule(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public World.Spigot spigot() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WorldBorder getWorldBorder() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<String> getListeningPluginChannels() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setMetadata(String string, MetadataValue mv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<MetadataValue> getMetadata(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasMetadata(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeMetadata(String string, Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class MockServer implements Server {

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getBukkitVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        //@Override
        public Player[] _INVALID_getOnlinePlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Collection<? extends Player> getOnlinePlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getMaxPlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getPort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getViewDistance() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getIp() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getServerName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getServerId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getWorldType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getGenerateStructures() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getAllowEnd() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getAllowNether() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasWhitelist() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setWhitelist(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<OfflinePlayer> getWhitelistedPlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void reloadWhitelist() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int broadcastMessage(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getUpdateFolder() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public File getUpdateFolderFile() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getConnectionThrottle() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getTicksPerAnimalSpawns() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getTicksPerMonsterSpawns() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Player getPlayer(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Player getPlayerExact(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Player> matchPlayer(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Player getPlayer(UUID uuid) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PluginManager getPluginManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public BukkitScheduler getScheduler() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ServicesManager getServicesManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<World> getWorlds() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public World createWorld(WorldCreator wc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadWorld(String string, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean unloadWorld(World world, boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public World getWorld(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public World getWorld(UUID uuid) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MapView getMap(short s) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MapView createMap(World world) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void reload() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Logger getLogger() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PluginCommand getPluginCommand(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void savePlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean dispatchCommand(CommandSender cs, String string) throws CommandException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void configureDbConfig(ServerConfig sc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addRecipe(Recipe recipe) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Recipe> getRecipesFor(ItemStack is) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator<Recipe> recipeIterator() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clearRecipes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void resetRecipes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<String, String[]> getCommandAliases() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getSpawnRadius() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setSpawnRadius(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getOnlineMode() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getAllowFlight() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isHardcore() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean useExactLoginLocation() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int broadcast(String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public OfflinePlayer getOfflinePlayer(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public OfflinePlayer getOfflinePlayer(UUID uuid) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<String> getIPBans() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void banIP(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unbanIP(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<OfflinePlayer> getBannedPlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public BanList getBanList(BanList.Type type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<OfflinePlayer> getOperators() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public org.bukkit.GameMode getDefaultGameMode() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setDefaultGameMode(org.bukkit.GameMode gm) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ConsoleCommandSender getConsoleSender() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public File getWorldContainer() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public OfflinePlayer[] getOfflinePlayers() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Messenger getMessenger() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public HelpMap getHelpMap() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Inventory createInventory(InventoryHolder ih, InventoryType it) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Inventory createInventory(InventoryHolder ih, InventoryType it, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Inventory createInventory(InventoryHolder ih, int i) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Inventory createInventory(InventoryHolder ih, int i, String string) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getMonsterSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getAnimalSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getWaterAnimalSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getAmbientSpawnLimit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPrimaryThread() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getMotd() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getShutdownMessage() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Warning.WarningState getWarningState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ItemFactory getItemFactory() {
            return CraftItemFactory.instance();
        }

        @Override
        public ScoreboardManager getScoreboardManager() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CachedServerIcon getServerIcon() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CachedServerIcon loadServerIcon(BufferedImage bi) throws IllegalArgumentException, Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setIdleTimeout(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getIdleTimeout() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ChunkGenerator.ChunkData createChunkData(World world) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public UnsafeValues getUnsafe() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Spigot spigot() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<String> getListeningPluginChannels() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        
    }

}
