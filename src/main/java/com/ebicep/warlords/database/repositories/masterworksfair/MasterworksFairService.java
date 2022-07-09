package com.ebicep.warlords.database.repositories.masterworksfair;

import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import org.springframework.stereotype.Service;

@Service
public interface MasterworksFairService {

    void create(MasterworksFair databaseTiming);

    void update(MasterworksFair databaseTiming);

    void delete(MasterworksFair databaseTiming);

    MasterworksFair findFirstByOrderByStartDateDesc();


}
