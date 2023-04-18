package com.ebicep.warlords.database.repositories.illusionvendor;

import com.ebicep.warlords.database.repositories.illusionvendor.pojos.IllusionVendorWeeklyShop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IllusionVendorRepository extends MongoRepository<IllusionVendorWeeklyShop, String> {

}
