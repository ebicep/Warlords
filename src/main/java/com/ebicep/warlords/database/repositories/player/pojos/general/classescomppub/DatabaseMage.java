package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseMage extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec pyromancer = new DatabaseBaseSpec();
    protected DatabaseBaseSpec cryomancer = new DatabaseBaseSpec();
    protected DatabaseBaseSpec aquamancer = new DatabaseBaseSpec();

    public DatabaseMage() {
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseBaseSpec getPyromancer() {
        return pyromancer;
    }

    public void setPyromancer(DatabaseBaseSpec pyromancer) {
        this.pyromancer = pyromancer;
    }

    public DatabaseBaseSpec getCryomancer() {
        return cryomancer;
    }

    public void setCryomancer(DatabaseBaseSpec cryomancer) {
        this.cryomancer = cryomancer;
    }

    public DatabaseBaseSpec getAquamancer() {
        return aquamancer;
    }

    public void setAquamancer(DatabaseBaseSpec aquamancer) {
        this.aquamancer = aquamancer;
    }
}
