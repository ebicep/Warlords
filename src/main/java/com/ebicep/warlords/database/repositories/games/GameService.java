package com.ebicep.warlords.database.repositories.games;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import org.springframework.data.mongodb.core.BulkOperations;

import java.util.List;

public interface GameService {

    boolean exists(DatabaseGame game);

    void create(DatabaseGame game);

    void update(DatabaseGame game);

    void delete(DatabaseGame game);

    void deleteAll();

    List<DatabaseGame> findAll();

    DatabaseGame findByDate(String date);

    List<DatabaseGame> getLastGames(int amount);

    BulkOperations bulkOps();

}
