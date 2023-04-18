package com.ebicep.warlords.database.repositories.illusionvendor;

import com.ebicep.warlords.database.repositories.illusionvendor.pojos.IllusionVendorWeeklyShop;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IllusionVendorService {

    void create(IllusionVendorWeeklyShop databaseTiming);

    void update(IllusionVendorWeeklyShop databaseTiming);

    void delete(IllusionVendorWeeklyShop databaseTiming);

    List<IllusionVendorWeeklyShop> findAll();
}
