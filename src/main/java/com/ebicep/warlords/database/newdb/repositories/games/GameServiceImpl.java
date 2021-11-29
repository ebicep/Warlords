package com.ebicep.warlords.database.newdb.repositories.games;

import com.ebicep.warlords.database.newdb.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean exists(DatabaseGame game) {
        return mongoTemplate.exists(new Query().addCriteria(Criteria.where("date").is(game.getDate()).and("time_left").is(game.getTimeLeft())), "Games_Information");
    }

    @Override
    public void create(DatabaseGame game) {
        DatabaseGame g = gameRepository.insert(game);
        System.out.println("Created: - " + g);
    }

    @Override
    public void update(DatabaseGame game) {
        gameRepository.save(game);
        System.out.println("Updated: - " + game);
    }

    @Override
    public void delete(DatabaseGame game) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<DatabaseGame> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public DatabaseGame findByDate(String date) {
        return gameRepository.findByDate(date);
    }

    @Override
    public List<DatabaseGame> getLastGames(int amount) {
        return gameRepository.getLastGames(amount);
    }

    @Override
    public BulkOperations bulkOps() {
        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DatabaseGame.class);
    }
}
