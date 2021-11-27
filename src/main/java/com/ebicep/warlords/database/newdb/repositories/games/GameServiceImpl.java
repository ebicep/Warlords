package com.ebicep.warlords.database.newdb.repositories.games;

import com.ebicep.warlords.database.newdb.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void create(DatabaseGame game) {
        DatabaseGame g = gameRepository.insert(game);
        System.out.println("Created: - " + g);
    }

    @Override
    public void update(DatabaseGame game) {

    }

    @Override
    public void delete(DatabaseGame game) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<DatabasePlayer> findAll() {
        return null;
    }

    @Override
    public DatabaseGame findByDate() {
        return null;
    }
}
