package com.ebicep.warlords.database.repositories.masterworksfair;


import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("masterworksFairService")
public class MasterworksFairServiceImpl implements MasterworksFairService {

    final
    MasterworksFairRepository masterworksFairRepository;

    public MasterworksFairServiceImpl(MasterworksFairRepository masterworksFairRepository) {
        this.masterworksFairRepository = masterworksFairRepository;
    }


    @Override
    public void create(MasterworksFair masterworksFair) {
        MasterworksFair fair = masterworksFairRepository.insert(masterworksFair);
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Created: - " + fair);
    }

    @Override
    public void update(MasterworksFair masterworksFair) {
        MasterworksFair fair = masterworksFairRepository.save(masterworksFair);
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Updated: - " + fair);
    }

    @Override
    public void delete(MasterworksFair masterworksFair) {
        masterworksFairRepository.delete(masterworksFair);
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Deleted: - " + masterworksFair);
    }

    @Override
    public MasterworksFair findFirstByOrderByStartDateDesc() {
        return masterworksFairRepository.findFirstByOrderByStartDateDesc();
    }

    @Override
    public List<MasterworksFair> findAll() {
        return masterworksFairRepository.findAll();
    }

    @Override
    public long count() {
        return masterworksFairRepository.count();
    }
}
