package com.ebicep.warlords.database.newdb.repositories.games.pojos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collation = "Games_Information")
public class DatabaseGame {

    @Id
    protected String id;

    protected String date;
    protected String map;
    @Field("time_left")
    protected int timeLeft;
    protected String winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected DatabaseGamePlayers players;
    protected String statInfo;
    protected boolean counted;

    public DatabaseGame(String date, String map, int timeLeft, String winner, int bluePoints, int redPoints, DatabaseGamePlayers players, String statInfo, boolean counted) {
        this.date = date;
        this.map = map;
        this.timeLeft = timeLeft;
        this.winner = winner;
        this.bluePoints = bluePoints;
        this.redPoints = redPoints;
        this.players = players;
        this.statInfo = statInfo;
        this.counted = counted;
    }

    @Override
    public String toString() {
        return "Game{" +
                "date='" + date + '\'' +
                ", map='" + map + '\'' +
                '}';
    }
}
