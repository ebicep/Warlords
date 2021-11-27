package com.ebicep.warlords.database.newdb.repositories.player;

import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PlayerService {

    void create(DatabasePlayer player);

    void create(DatabasePlayer player, PlayersCollections collection);

    void update(DatabasePlayer player);

    void save(DatabasePlayer player, PlayersCollections collection);

    void delete(DatabasePlayer player);

    void deleteAll();

    DatabasePlayer findOne(Criteria criteria, PlayersCollections collection);

    DatabasePlayer findByUUID(UUID uuid);

    List<DatabasePlayer> findAll();

    List<DatabasePlayer> getPlayersSortedByPlays();
}
