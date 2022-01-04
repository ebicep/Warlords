package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;

public class DatabasePlayerCompStats {

    private DatabasePlayerCTF ctfStats = new DatabasePlayerCTF();

    public DatabasePlayerCompStats() {
    }

    public DatabasePlayerCTF getCtfStats() {
        return ctfStats;
    }

    public void setCtfStats(DatabasePlayerCTF ctfStats) {
        this.ctfStats = ctfStats;
    }
}
