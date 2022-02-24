package com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.MapCategory;
import org.bukkit.GameMode;

public class DatabaseRogue extends AbstractDatabaseStatInformation {

    protected DatabaseBaseSpec assassin = new DatabaseBaseSpec();
    protected DatabaseBaseSpec vindicator = new DatabaseBaseSpec();
    protected DatabaseBaseSpec apothecary = new DatabaseBaseSpec();

    public DatabaseRogue() {
    }

    @Override
    public void updateCustomStats(MapCategory mapCategory, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseBaseSpec getAssassin() {
        return assassin;
    }

    public void setAssassin(DatabaseBaseSpec assassin) {
        this.assassin = assassin;
    }

    public DatabaseBaseSpec getVindicator() {
        return vindicator;
    }

    public void setVindicator(DatabaseBaseSpec vindicator) {
        this.vindicator = vindicator;
    }

    public DatabaseBaseSpec getApothecary() {
        return apothecary;
    }

    public void setApothecary(DatabaseBaseSpec apothecary) {
        this.apothecary = apothecary;
    }
}
