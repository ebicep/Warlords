package com.ebicep.warlords.database.repositories.timings;


import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("timingsService")
public class TimingsServiceImpl implements TimingsService {

    @Autowired
    TimingsRepository timingsRepository;


    @Override
    public void create(DatabaseTiming databaseTiming) {
        DatabaseTiming timing = timingsRepository.insert(databaseTiming);
        System.out.println("[TimingService] Created: - " + timing);
    }

    @Override
    public void update(DatabaseTiming databaseTiming) {
        DatabaseTiming timing = timingsRepository.save(databaseTiming);
        System.out.println("[TimingService] Updated: - " + timing);
    }

    @Override
    public void delete(DatabaseTiming databaseTiming) {
        timingsRepository.delete(databaseTiming);
        System.out.println("[TimingService] Deleted: - " + databaseTiming);
    }

    @Override
    public DatabaseTiming findByTitle(String title) {
        return timingsRepository.findByTitle(title);
    }
}
