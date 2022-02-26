package com.ebicep.warlords.database.repositories.games;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import org.bson.Document;
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
    MongoTemplate mongoTemplate;

    @Override
    public boolean exists(DatabaseGameBase game, GamesCollections collections) {
        return mongoTemplate.exists(new Query().addCriteria(Criteria.where("exact_date").is(game.getExactDate())), collections.collectionName);
    }

    @Override
    public void create(DatabaseGameBase game) {
        switch (game.getGameMode()) {
            case CAPTURE_THE_FLAG:
                create(game, GamesCollections.CTF);
                break;
            case TEAM_DEATHMATCH:
                create(game, GamesCollections.TDM);
                break;
        }
    }

    @Override
    public void create(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.insert(game, collection.collectionName);
        System.out.println("[GameService]: " + game.getDate() + " - was created in " + collection.collectionName);
        mongoTemplate.insert(game, GamesCollections.ALL.collectionName);
        System.out.println("[GameService]: " + game.getDate() + " - was created in " + GamesCollections.ALL.collectionName);
    }

    @Override
    public void save(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.save(game, collection.collectionName);
        System.out.println("[GameService]: Updated " + game.getDate() + " in " + collection.collectionName);
    }

    @Override
    public void delete(DatabaseGameBase game, GamesCollections collection) {
        mongoTemplate.remove(game, collection.collectionName);
        System.out.println("[GameService]: Deleted " + game.getDate() + " in " + collection.collectionName);
    }

    @Override
    public DatabaseGameBase findOne(Query query, GamesCollections collection) {
        return mongoTemplate.findOne(query, DatabaseGameBase.class, collection.collectionName);
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
        List<DatabaseGameBase> games = findAll(GamesCollections.ALL);
        if (games.size() <= amount) {
            return games;
        }
        return games.subList(games.size() - amount, games.size());
    }

}
