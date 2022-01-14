package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends MongoRepository<DatabasePlayer, String>, CustomPlayerRepository {

    @Query("{uuid:'?0'}")
    DatabasePlayer findByUUID(UUID uuid);

}
