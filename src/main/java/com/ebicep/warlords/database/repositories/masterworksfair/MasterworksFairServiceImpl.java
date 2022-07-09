package com.ebicep.warlords.database.repositories.masterworksfair;


import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("masterworksFairService")
public class MasterworksFairServiceImpl implements MasterworksFairService {

    @Autowired
    MasterworksFairRepository masterworksFairRepository;


    @Override
    public void create(MasterworksFair masterworksFair) {
        MasterworksFair fair = masterworksFairRepository.insert(masterworksFair);
        System.out.println("[MasterworksFairService] Created: - " + fair);
    }

    @Override
    public void update(MasterworksFair masterworksFair) {
        MasterworksFair fair = masterworksFairRepository.save(masterworksFair);
        System.out.println("[MasterworksFairService] Updated: - " + fair);
    }

    @Override
    public void delete(MasterworksFair masterworksFair) {
        masterworksFairRepository.delete(masterworksFair);
        System.out.println("[MasterworksFairService] Deleted: - " + masterworksFair);
    }

    @Override
    public MasterworksFair findFirstByOrderByStartDateDesc() {
        return masterworksFairRepository.findFirstByOrderByStartDateDesc();
    }

}
