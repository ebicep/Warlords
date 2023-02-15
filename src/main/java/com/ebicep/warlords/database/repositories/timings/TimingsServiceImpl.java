package com.ebicep.warlords.database.repositories.timings;


import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.stereotype.Service;

@Service("timingsService")
public class TimingsServiceImpl implements TimingsService {

    final
    TimingsRepository timingsRepository;

    public TimingsServiceImpl(TimingsRepository timingsRepository) {
        this.timingsRepository = timingsRepository;
    }


    @Override
    public void create(DatabaseTiming databaseTiming) {
        DatabaseTiming timing = timingsRepository.insert(databaseTiming);
        ChatUtils.MessageTypes.TIMINGS.sendMessage("Created: - " + timing);
    }

    @Override
    public void update(DatabaseTiming databaseTiming) {
        DatabaseTiming timing = timingsRepository.save(databaseTiming);
        ChatUtils.MessageTypes.TIMINGS.sendMessage("Updated: - " + timing);
    }

    @Override
    public void delete(DatabaseTiming databaseTiming) {
        timingsRepository.delete(databaseTiming);
        ChatUtils.MessageTypes.TIMINGS.sendMessage("Deleted: - " + databaseTiming);
    }

    @Override
    public DatabaseTiming findByTitle(String title) {
        return timingsRepository.findByTitle(title);
    }
}
