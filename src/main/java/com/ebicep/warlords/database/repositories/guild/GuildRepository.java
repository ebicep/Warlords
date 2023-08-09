package com.ebicep.warlords.database.repositories.guild;

import com.ebicep.warlords.guilds.Guild;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends MongoRepository<Guild, String> {

    @Query("{'name':'?0'}")
    Guild findByName(String name);

}
