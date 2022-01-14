package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseWarrior extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec berserker = new DatabaseBaseSpec();
    protected DatabaseBaseSpec defender = new DatabaseBaseSpec();
    protected DatabaseBaseSpec revenant = new DatabaseBaseSpec();

    public DatabaseWarrior() {
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseBaseSpec getBerserker() {
        return berserker;
    }

    public void setBerserker(DatabaseBaseSpec berserker) {
        this.berserker = berserker;
    }

    public DatabaseBaseSpec getDefender() {
        return defender;
    }

    public void setDefender(DatabaseBaseSpec defender) {
        this.defender = defender;
    }

    public DatabaseBaseSpec getRevenant() {
        return revenant;
    }

    public void setRevenant(DatabaseBaseSpec revenant) {
        this.revenant = revenant;
    }
}