package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

public class DatabasePaladin extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec avenger = new DatabaseBaseSpec();
    protected DatabaseBaseSpec crusader = new DatabaseBaseSpec();
    protected DatabaseBaseSpec protector = new DatabaseBaseSpec();

    public DatabasePaladin() {
    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
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