package com.ebicep.warlords.database.newdb.repositories.games.pojos;

import java.util.List;

public class DatabaseGamePlayers {

    protected List<GamePlayer> blue;
    protected List<GamePlayer> red;

    public DatabaseGamePlayers(List<GamePlayer> blue, List<GamePlayer> red) {
        this.blue = blue;
        this.red = red;
    }

    public List<GamePlayer> getBlue() {
        return blue;
    }

    public List<GamePlayer> getRed() {
        return red;
    }

    public static class GamePlayer {

        private String uuid;
        private String name;
        private String spec;
        private int blocksTravelled;
        private int secondsInCombat;
        private int secondsInRespawn;
        private String xLocations;
        private String zLocations;
        private int totalKills;
        private int totalAssists;
        private int totalDeaths;
        private long totalDamage;
        private long totalHealing;
        private long totalAbsorbed;
        private List<Integer> kills;
        private List<Integer> assists;
        private List<Integer> deaths;
        private List<Long> damage;
        private List<Long> healing;
        private List<Long> absorbed;
        private int flagCaptures;
        private int flagReturns;
        private long totalDamageOnCarrier;
        private long totalHealingOnCarrier;
        private List<Long> damageOnCarrier;
        private List<Long> healingOnCarrier;
        private long experienceEarned;

        public GamePlayer(String name) {
            this.name = name;
        }
    }
}
