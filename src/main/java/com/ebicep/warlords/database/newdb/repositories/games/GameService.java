package com.ebicep.warlords.database.newdb.repositories.games;

import com.ebicep.warlords.database.newdb.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;

import java.util.List;

public interface GameService {

    void create(DatabaseGame game);

    void update(DatabaseGame game);

    void delete(DatabaseGame game);

    void deleteAll();

    List<DatabasePlayer> findAll();

    DatabaseGame findByDate();

}
