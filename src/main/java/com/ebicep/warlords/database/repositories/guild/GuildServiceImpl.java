package com.ebicep.warlords.database.repositories.guild;


import com.ebicep.warlords.guilds.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("guildService")
public class GuildServiceImpl implements GuildService {

    @Autowired
    GuildRepository guildRepository;


    @Override
    public void create(Guild guild) {
        guildRepository.insert(guild);
        System.out.println("[GuildService] Created: - " + guild);
    }

    @Override
    public void update(Guild guild) {
        guildRepository.save(guild);
        System.out.println("[GuildService] Updated: - " + guild);
    }

    @Override
    public void delete(Guild guild) {
        guildRepository.delete(guild);
        System.out.println("[GuildService] Deleted: - " + guild);
    }

    @Override
    public Guild findByName(String title) {
        return guildRepository.findByName(title);
    }
}
