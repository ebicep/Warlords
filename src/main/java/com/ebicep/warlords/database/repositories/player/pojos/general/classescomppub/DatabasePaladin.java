package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabasePaladin extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec avenger = new DatabaseBaseSpec();
    protected DatabaseBaseSpec crusader = new DatabaseBaseSpec();
    protected DatabaseBaseSpec protector = new DatabaseBaseSpec();

    public DatabasePaladin() {
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseBaseSpec getAvenger() {
        return avenger;
    }

    public void setAvenger(DatabaseBaseSpec avenger) {
        this.avenger = avenger;
    }

    public DatabaseBaseSpec getCrusader() {
        return crusader;
    }

    public void setCrusader(DatabaseBaseSpec crusader) {
        this.crusader = crusader;
    }

    public DatabaseBaseSpec getProtector() {
        return protector;
    }

    public void setProtector(DatabaseBaseSpec protector) {
        this.protector = protector;
    }
}