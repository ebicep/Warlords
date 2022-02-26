package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

public class DatabaseWarrior extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec berserker = new DatabaseBaseSpec();
    protected DatabaseBaseSpec defender = new DatabaseBaseSpec();
    protected DatabaseBaseSpec revenant = new DatabaseBaseSpec();

    public DatabaseWarrior() {
    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
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