package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.player.Weapons;

public class DatabaseSpecialization {

    private int kills = 0;
    private int assists = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int flagsCaptured = 0;
    private int flagsReturned = 0;
    private long damage = 0;
    private long healing = 0;
    private long absorbed = 0;
    private Weapons weapon = Weapons.FELFLAME_BLADE;
    private long experience = 0;

    public DatabaseSpecialization() {

    }
}
