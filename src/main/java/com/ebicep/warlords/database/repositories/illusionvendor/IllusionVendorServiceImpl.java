package com.ebicep.warlords.database.repositories.illusionvendor;


import com.ebicep.warlords.database.repositories.illusionvendor.pojos.IllusionVendorWeeklyShop;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("illusionVendorService")
public class IllusionVendorServiceImpl implements IllusionVendorService {

    final
    IllusionVendorRepository illusionVendorRepository;

    public IllusionVendorServiceImpl(IllusionVendorRepository illusionVendorRepository) {
        this.illusionVendorRepository = illusionVendorRepository;
    }


    @Override
    public void create(IllusionVendorWeeklyShop illusionVendorWeeklyShop) {
        illusionVendorRepository.insert(illusionVendorWeeklyShop);
        ChatUtils.MessageTypes.ILLUSION_VENDOR.sendMessage("Created: - " + illusionVendorWeeklyShop);
    }

    @Override
    public void update(IllusionVendorWeeklyShop illusionVendorWeeklyShop) {
        illusionVendorRepository.save(illusionVendorWeeklyShop);
        ChatUtils.MessageTypes.ILLUSION_VENDOR.sendMessage("Updated: - " + illusionVendorWeeklyShop);
    }

    @Override
    public void delete(IllusionVendorWeeklyShop illusionVendorWeeklyShop) {
        illusionVendorRepository.delete(illusionVendorWeeklyShop);
        ChatUtils.MessageTypes.ILLUSION_VENDOR.sendMessage("Deleted: - " + illusionVendorWeeklyShop);
    }

    @Override
    public List<IllusionVendorWeeklyShop> findAll() {
        return illusionVendorRepository.findAll();
    }

}
