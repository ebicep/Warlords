package com.ebicep.warlords.database.repositories.masterworksfair;


import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("masterworksFairService")
public class MasterworksFairServiceImpl implements MasterworksFairService {

    @Autowired
    MasterworksFairRepository masterworksFairRepository;


    @Override
    public void create(MasterworksFair databaseTiming) {
        MasterworksFair timing = masterworksFairRepository.insert(databaseTiming);
        System.out.println("[MasterworksFairService] Created: - " + timing);
    }

    @Override
    public void update(MasterworksFair databaseTiming) {
        MasterworksFair timing = masterworksFairRepository.save(databaseTiming);
        System.out.println("[MasterworksFairService] Updated: - " + timing);
    }

    @Override
    public void delete(MasterworksFair databaseTiming) {
        masterworksFairRepository.delete(databaseTiming);
        System.out.println("[MasterworksFairService] Deleted: - " + databaseTiming);
    }

    @Override
    public MasterworksFair findFirstByOrderByStartDateDesc() {
        return masterworksFairRepository.findFirstByOrderByStartDateDesc();
    }

}
