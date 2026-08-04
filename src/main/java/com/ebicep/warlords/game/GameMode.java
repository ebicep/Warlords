package com.ebicep.warlords.game;

import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.gamemodes.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

public enum GameMode implements Mode {

    LOBBY(new Lobby()),
    CAPTURE_THE_FLAG(new CaptureTheFlag()),
    INTERCEPTION(new Interception()),
    TEAM_DEATHMATCH(new TeamDeathmatch()),
    DUEL(new Duel()),
    WAVE_DEFENSE(new WaveDefense()),
    ONSLAUGHT(new Onslaught()),
    TREASURE_HUNT(new TreasureHunt()),
    ANOMALY(new Anomaly()),
    RAID(new Raid()),
    DEBUG(new Debug()),
    PVE_DEBUG(new PvEDebug()),
    TUTORIAL(new Tutorial()),
    EVENT_WAVE_DEFENSE(new EventWaveDefense()),
    SIEGE(new Siege()),
    TOWER_DEFENSE(new TowerDefense()),
    WHACK_A_MOLE(new WhackAMole()),
    EFFIGY_TRIALS(new EffigyTrials()),

    ;

    public static final GameMode[] VALUES = values();

    public static boolean isWaveDefense(GameMode mode) {
        return mode == WAVE_DEFENSE || mode == EVENT_WAVE_DEFENSE;
    }

    public static boolean isPvE(GameMode mode) {
        return mode == WAVE_DEFENSE
                || mode == EVENT_WAVE_DEFENSE
                || mode == ONSLAUGHT
                || mode == TREASURE_HUNT
                || mode == ANOMALY
                || mode == TOWER_DEFENSE
                || mode == WHACK_A_MOLE
                || mode == PVE_DEBUG
                || mode == EFFIGY_TRIALS
                || mode == RAID;
    }

    private final Mode mode;

    GameMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public List<Option> initMap(
            GameMap map,
            LocationFactory loc,
            EnumSet<GameAddon> addons
    ) {
        return mode.initMap(map, loc, addons);
    }

    @Override
    public List<String> getNamespaces() {
        return mode.getNamespaces();
    }

    @Override
    public List<Option> postMapModifyOptions(
            GameMap map,
            LocationFactory loc,
            EnumSet<GameAddon> addons,
            List<Option> options
    ) {
        return mode.postMapModifyOptions(map, loc, addons, options);
    }

    @Override
    public String getName() {
        return mode.getName();
    }

    @Override
    public String getAbbreviation() {
        return mode.getAbbreviation();
    }

    @Override
    public ItemStack getItemStack() {
        return mode.getItemStack();
    }

    @Override
    public boolean isHiddenInMenu() {
        return mode.isHiddenInMenu();
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return mode.getCreateDatabaseGame();
    }

    @Override
    public GamesCollections getGamesCollections() {
        return mode.getGamesCollections();
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return mode.getMinPlayersToAddToDatabase();
    }

    @Override
    public float getDropModifier() {
        return mode.getDropModifier();
    }
}