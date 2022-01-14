package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseShaman extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec thunderlord = new DatabaseBaseSpec();
    protected DatabaseBaseSpec spiritguard = new DatabaseBaseSpec();
    protected DatabaseBaseSpec earthwarden = new DatabaseBaseSpec();

    public DatabaseShaman() {
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseBaseSpec getThunderlord() {
        return thunderlord;
    }

    public void setThunderlord(DatabaseBaseSpec thunderlord) {
        this.thunderlord = thunderlord;
    }

    public DatabaseBaseSpec getSpiritguard() {
        return spiritguard;
    }

    public void setSpiritguard(DatabaseBaseSpec spiritguard) {
        this.spiritguard = spiritguard;
    }

    public DatabaseBaseSpec getEarthwarden() {
        return earthwarden;
    }

    public void setEarthwarden(DatabaseBaseSpec earthwarden) {
        this.earthwarden = earthwarden;
    }
}