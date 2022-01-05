package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabasePlayerPubStats {

    @Field("ctf_stats")
    private DatabasePlayerCTF ctfStats = new DatabasePlayerCTF();

    public DatabasePlayerPubStats() {
    }

    public DatabasePlayerCTF getCtfStats() {
        return ctfStats;
    }

    public void setCtfStats(DatabasePlayerCTF ctfStats) {
        this.ctfStats = ctfStats;
    }
}
