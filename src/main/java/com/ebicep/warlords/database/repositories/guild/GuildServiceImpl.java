package com.ebicep.warlords.database.repositories.guild;


import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("guildService")
public class GuildServiceImpl implements GuildService {

    final
    GuildRepository guildRepository;

    public GuildServiceImpl(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }


    @Override
    public void create(Guild guild) {
        guildRepository.insert(guild);
        ChatUtils.MessageTypes.GUILD_SERVICE.sendMessage("Created: - " + guild);
    }

    @Override
    public void update(Guild guild) {
        guildRepository.save(guild);
        ChatUtils.MessageTypes.GUILD_SERVICE.sendMessage("Updated: - " + guild);
    }

    @Override
    public void delete(Guild guild) {
        guildRepository.delete(guild);
        ChatUtils.MessageTypes.GUILD_SERVICE.sendMessage("Deleted: - " + guild);
    }

    @Override
    public Guild findByName(String title) {
        return guildRepository.findByName(title);
    }

    @Override
    public List<Guild> findAll() {
        return guildRepository.findAll();
    }
}
