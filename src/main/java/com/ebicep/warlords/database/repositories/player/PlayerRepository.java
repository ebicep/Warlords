package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerRepository extends MongoRepository<DatabasePlayer, String>, CustomPlayerRepository {

    @Query("{uuid:'?0'}")
    DatabasePlayer findByUUID(UUID uuid);

    @Aggregation({
            "{ $addFields: { plays : { $add:[ $wins, $losses ] } } }",
            "{ $sort:{ plays: -1 } } }"
    })
    List<DatabasePlayer> getPlayersSortedByPlays();

}
