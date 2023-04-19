package com.ebicep.warlords.database.repositories.items;


import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("itemsWeeklyBlessingsService")
public class WeeklyBlessingsServiceImpl implements WeeklyBlessingsService {

    final
    WeeklyBlessingsRepository weeklyBlessingsRepository;

    public WeeklyBlessingsServiceImpl(WeeklyBlessingsRepository weeklyBlessingsRepository) {
        this.weeklyBlessingsRepository = weeklyBlessingsRepository;
    }


    @Override
    public void create(WeeklyBlessings weeklyBlessings) {
        weeklyBlessingsRepository.insert(weeklyBlessings);
        ChatUtils.MessageTypes.WEEKLY_BLESSINGS.sendMessage("Created: - " + weeklyBlessings);
    }

    @Override
    public void update(WeeklyBlessings weeklyBlessings) {
        weeklyBlessingsRepository.save(weeklyBlessings);
        ChatUtils.MessageTypes.WEEKLY_BLESSINGS.sendMessage("Updated: - " + weeklyBlessings);
    }

    @Override
    public void delete(WeeklyBlessings weeklyBlessings) {
        weeklyBlessingsRepository.delete(weeklyBlessings);
        ChatUtils.MessageTypes.WEEKLY_BLESSINGS.sendMessage("Deleted: - " + weeklyBlessings);
    }

    @Override
    public List<WeeklyBlessings> findAll() {
        return weeklyBlessingsRepository.findAll();
    }

}
