package com.ebicep.warlords.database.repositories.items;

import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WeeklyBlessingsService {

    void create(WeeklyBlessings databaseTiming);

    void update(WeeklyBlessings databaseTiming);

    void delete(WeeklyBlessings databaseTiming);

    List<WeeklyBlessings> findAll();
}
