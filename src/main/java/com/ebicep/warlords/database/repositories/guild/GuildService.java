package com.ebicep.warlords.database.repositories.guild;

import com.ebicep.warlords.guilds.Guild;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GuildService {

    void create(Guild guild);

    void update(Guild guild);

    void delete(Guild guild);

    Guild findByName(String name);

    List<Guild> findAll();


}
