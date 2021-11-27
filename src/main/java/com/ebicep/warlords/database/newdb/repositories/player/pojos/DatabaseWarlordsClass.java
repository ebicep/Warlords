package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.player.ArmorManager;

public class DatabaseWarlordsClass {

    protected int kills;
    protected int assists;
    protected int deaths;
    protected int wins;
    protected int losses;
    protected int flagsCaptured;
    protected int flagsReturned;
    protected long damage;
    protected long healing;
    protected long absorbed;
    protected ArmorManager.Helmets helmet;
    protected ArmorManager.ArmorSets armor;

    public DatabaseWarlordsClass() {
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.flagsCaptured = 0;
        this.flagsReturned = 0;
        this.damage = 0L;
        this.healing = 0L;
        this.absorbed = 0L;
        this.helmet = ArmorManager.Helmets.SIMPLE_MAGE_HELMET;
        this.armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_MAGE;
    }


//    private static BasicDBObject getBaseSpecStats() {
//        return new BasicDBObject("kills", 0)
//                .append("assists", 0)
//                .append("deaths", 0)
//                .append("wins", 0)
//                .append("losses", 0)
//                .append("flags_captured", 0)
//                .append("flags_returned", 0)
//                .append("damage", 0L)
//                .append("healing", 0L)
//                .append("absorbed", 0L);
//    }
}
