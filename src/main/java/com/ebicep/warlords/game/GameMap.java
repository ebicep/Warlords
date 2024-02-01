package com.ebicep.warlords.game;

import com.ebicep.warlords.game.maps.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.chat.ChatUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameMap {

    public static final GameMap ACROPOLIS = new Acropolis();
    public static final GameMap APERTURE = new Aperture();
    public static final GameMap ARATHI = new Arathi();
    public static final GameMap BLACK_TEMPLE = new BlackTemple();
    public static final GameMap CROSSFIRE = new Crossfire();
    public static final GameMap DEATH_VALLEY = new DeathValley();
    public static final GameMap DEBUG = new Practice();
    public static final GameMap DORIVEN_BASIN = new DorivenBasin();
    public static final GameMap FALSTAD_GATE = new FalstadGate();
    public static final GameMap FORGOTTEN_CODEX = new ForgottenCodex();
    public static final GameMap GORGE_REMASTERED = new GorgeRemastered();
    public static final GameMap GORGE_REMASTERED_SIEGE = new GorgeRemasteredSiege();
    public static final GameMap GRIMOIRES_GRAVEYARD = new GrimoiresGraveyard();
    public static final GameMap HEAVEN_WILL = new HeavensWill();
    public static final GameMap ILLUSION_APERTURE = new IllusionAperture();
    public static final GameMap ILLUSION_CROSSFIRE = new IllusionCrossfire();
    public static final GameMap ILLUSION_PHANTOM = new IllusionPhantom();
    public static final GameMap ILLUSION_RIFT = new IllusionRift();
    public static final GameMap ILLUSION_RIFT_EVENT_1 = new CombatantsCavernMode1();
    public static final GameMap ILLUSION_RIFT_EVENT_2 = new CombatantsCavernMode2();
    public static final GameMap ILLUSION_RIFT_EVENT_3 = new AcolyteArchives();
    public static final GameMap ILLUSION_RIFT_EVENT_4 = new SpidersBurrow();
    public static final GameMap ILLUSION_RIFT_EVENT_5 = new TheBorderlineOfIllusion();
    public static final GameMap ILLUSION_VALLEY = new IllusionValleyHard();
    public static final GameMap ILLUSION_VALLEY2 = new IllusionValleyExtreme();
    public static final GameMap MAIN_LOBBY = new MainLobby();
    public static final GameMap NEOLITHIC = new Neolithic();
    public static final GameMap PAYLOAD = new Payload();
    public static final GameMap PAYLOAD2 = new Payload2();
    public static final GameMap PHANTOM = new Phantom();
    public static final GameMap RIFT = new TheRift();
    public static final GameMap RUINS = new Ruins();
    public static final GameMap SCORCHED = new Scorched();
    public static final GameMap SIEGE = new Siege();
    public static final GameMap STORM_WIND = new StormWind();
    public static final GameMap SUN_AND_MOON = new SunAndMoon();
    public static final GameMap TARTARUS = new Tartarus();
    public static final GameMap THE_OBSIDIAN_TRAIL_RAID = new TheObsidianTrailRaid();
    public static final GameMap TREASURE_HUNT = new TreasureHunt();
    public static final GameMap TUTORIAL_MAP = new Tutorial();
    public static final GameMap VALLEY = new Valley();
    public static final GameMap VOID_RIFT = new VoidRift();
    public static final GameMap WARSONG = new Warsong();

    public static final GameMap[] VALUES = {
            ACROPOLIS,
            APERTURE,
            ARATHI,
            BLACK_TEMPLE,
            CROSSFIRE,
            DEATH_VALLEY,
            DEBUG,
            DORIVEN_BASIN,
            FALSTAD_GATE,
            FORGOTTEN_CODEX,
            GORGE_REMASTERED,
            GORGE_REMASTERED_SIEGE,
            GRIMOIRES_GRAVEYARD,
            HEAVEN_WILL,
            ILLUSION_APERTURE,
            ILLUSION_CROSSFIRE,
            ILLUSION_PHANTOM,
            ILLUSION_RIFT,
            ILLUSION_RIFT_EVENT_1,
            ILLUSION_RIFT_EVENT_2,
            ILLUSION_RIFT_EVENT_3,
            ILLUSION_RIFT_EVENT_4,
            ILLUSION_RIFT_EVENT_5,
            ILLUSION_VALLEY,
            ILLUSION_VALLEY2,
            MAIN_LOBBY,
            NEOLITHIC,
            PAYLOAD,
            PAYLOAD2,
            PHANTOM,
            RIFT,
            RUINS,
            SCORCHED,
            SIEGE,
            STORM_WIND,
            SUN_AND_MOON,
            TARTARUS,
            THE_OBSIDIAN_TRAIL_RAID,
            TREASURE_HUNT,
            TUTORIAL_MAP,
            VALLEY,
            VOID_RIFT,
            WARSONG
    };

    public static GameMap getGameMap(String mapName) {
        for (GameMap value : GameMap.VALUES) {
            if (value.mapName.equalsIgnoreCase(mapName)) {
                return value;
            }
        }
        return null;
    }

    /**
     * <p>Each map instance presents a game server, every map can hold up to 3 games at once.</p>
     * <p>Adding a new map must start with -0 at the end and increment from there on out.</p>
     * <p>Adding additional game servers will require a config update in @see MultiWorld Plugin</p>
     *
     * @param gameManager The game manager to add gameholders to.
     */
    public static void addGameHolders(GameManager gameManager) {
        Set<String> addedMaps = new HashSet<>();
        for (GameMap map : VALUES) {
            if (map == MAIN_LOBBY) {
                gameManager.addGameHolder(map.fileName, map);
                continue;
            }
            for (int i = 0; i < map.numberOfMaps; i++) {
                String mapName = map.fileName + "-" + i;
                if (addedMaps.contains(mapName)) {
                    continue;
                }
                addedMaps.add(mapName);
                gameManager.addGameHolder(mapName, map);
            }
        }
    }

    private final String mapName;
    private final int maxPlayers;
    private final int minPlayers;
    private final int lobbyCountdown;
    private final String fileName;
    private final int numberOfMaps;
    private final List<GameMode> gameMode;

    public GameMap(
            @Nonnull String mapName,
            int maxPlayers,
            int minPlayers,
            int lobbyCountdown,
            String fileName,
            int numberOfMaps,
            @Nonnull GameMode... gameMode
    ) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyCountdown = lobbyCountdown;
        this.fileName = fileName;
        this.numberOfMaps = numberOfMaps;
        this.gameMode = List.of(gameMode);
    }

    /**
     * Constructs the game instance
     *
     * @param category The map category to construct (for maps with multiple
     *                 configurations)
     * @param loc      The base location to construct the map
     * @param addons   The used addons
     * @return The initial list of options
     */
    public abstract List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons);

    public State initialState(Game game) {
        return new PreLobbyState(game);
    }

    public String getMapName() {
        return mapName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public List<GameMode> getGameModes() {
        return gameMode;
    }

    public String getDatabaseName() {
        Field[] fields = GameMap.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.get(null) == this) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }
        return null;
    }
}
