package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseBaseCTF extends AbstractDatabaseStatInformation {

    @Field("flags_captured")
    private int flagsCaptured = 0;
    @Field("flags_returned")
    private int flagsReturned = 0;

    public DatabaseBaseCTF() {
    }

    public int getFlagsCaptured() {
        return flagsCaptured;
    }

    public void setFlagsCaptured(int flagsCaptured) {
        this.flagsCaptured = flagsCaptured;
    }

    public int getFlagsReturned() {
        return flagsReturned;
    }

    public void setFlagsReturned(int flagsReturned) {
        this.flagsReturned = flagsReturned;
    }
}
