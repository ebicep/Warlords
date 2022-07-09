package com.ebicep.warlords.database.repositories.masterworksfair;

import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterworksFairRepository extends MongoRepository<MasterworksFair, String> {

    MasterworksFair findFirstByOrderByStartDateDesc();

}
