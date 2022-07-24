package com.ebicep.warlords.database.repositories.guild;

import com.ebicep.warlords.guilds.Guild;
import org.springframework.stereotype.Service;

@Service
public interface GuildService {

    void create(Guild guild);

    void update(Guild guild);

    void delete(Guild guild);

    Guild findByName(String name);


}
