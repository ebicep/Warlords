package com.ebicep.warlords.database.repositories.games;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service("gameService")
public class GameServiceImpl implements GameService {

    final
    MongoTemplate mongoTemplate;

    public GameServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean exists(DatabaseGameBase game, GamesCollections collections) {
        return mongoTemplate.exists(new Query().addCriteria(Criteria.where("exact_date").is(game.getExactDate())), collections.collectionName);
    }

    @Override
    public void create(DatabaseGameBase game) {
        switch (game.getGameMode()) {
            case CAPTURE_THE_FLAG -> create(game, GamesCollections.CTF);
            case TEAM_DEATHMATCH -> create(game, GamesCollections.TDM);
        }
    }

    @Override
    public void create(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.insert(game, collection.collectionName);
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("" + game.getDate() + " - was created in " + collection.collectionName);
        mongoTemplate.insert(game, GamesCollections.ALL.collectionName);
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("" + game.getDate() + " - was created in " + GamesCollections.ALL.collectionName);
    }

    @Override
    public void createBackup(DatabaseGameBase game) {
        mongoTemplate.insert(game, "Games_Backup");
        ChatUtils.MessageType.GAME_SERVICE.sendMessage(game.getDate() + " - was created in Games_Backup");
    }

    @Override
    public void save(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.save(game, collection.collectionName);
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("Updated " + game.getDate() + " in " + collection.collectionName);
    }

    @Override
    public void delete(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.remove(game, collection.collectionName);
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("Deleted " + game.getDate() + " in " + collection.collectionName);
    }

    @Override
    public void updateMany(Query query, UpdateDefinition update, Class<?> clazz, GamesCollections collection) {
        mongoTemplate.updateMulti(query, update, clazz, collection.collectionName);
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("UpdatedMany (" + query + ") - (" + update + ") in " + collection.collectionName);
    }

    @Override
    public DatabaseGameBase findByDate(String date) {
        return mongoTemplate.findOne(new Query().addCriteria(Criteria.where("date").is(date)), DatabaseGameBase.class, GamesCollections.ALL.collectionName);
    }

    @Override
    public DatabaseGameBase findOne(Query query, GamesCollections collection) {
        return mongoTemplate.findOne(query, DatabaseGameBase.class, collection.collectionName);
    }

    @Override
    public List<DatabaseGameBase> find(Query query, GamesCollections collection) {
        return mongoTemplate.find(query, DatabaseGameBase.class, collection.collectionName);
    }

    @Override
    public List<DatabaseGameBase> findAll(GamesCollections collection) {
        return mongoTemplate.findAll(DatabaseGameBase.class, collection.collectionName);
    }

    @Override
    public BulkOperations bulkOps() {
        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DatabaseGameBase.class);
    }

    @Override
    public <T> T convertDocumentToClass(Document document, Class<T> clazz) {
        return mongoTemplate.getConverter().read(clazz, document);
    }

    @Override
    public List<? extends DatabaseGameBase> getLastGames(int amount) {
        List<DatabaseGameBase> games = find(new Query(Criteria.where("exact_date").gt(Instant.now().minus(30, ChronoUnit.DAYS))), GamesCollections.ALL);
        if (games.size() <= amount) {
            return games;
        }
        return games.subList(games.size() - amount, games.size());
    }

}
