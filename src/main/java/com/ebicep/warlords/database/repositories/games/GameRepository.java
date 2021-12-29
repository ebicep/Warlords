package com.ebicep.warlords.database.repositories.games;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends MongoRepository<DatabaseGame, String> {

    @Query("{date:'?0'}")
    DatabaseGame findByDate(String date);

    @Aggregation({
            "{ $sort: { _id: -1 } }",
            "{ $limit: ?0 }",
            "{ $sort: { _id: 1 } }"
    })
    List<DatabaseGame> getLastGames(int amount);

}
