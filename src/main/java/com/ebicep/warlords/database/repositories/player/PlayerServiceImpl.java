package com.ebicep.warlords.database.repositories.player;


import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Cacheable(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null")
    @Override
    public void create(DatabasePlayer player) {
        DatabasePlayer p = playerRepository.insert(player);
        System.out.println("[PlayerService]: Created: - " + p);
    }

    @Cacheable(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null")
    @Override
    public void create(DatabasePlayer player, PlayersCollections collection) {
        playerRepository.create(player, collection);
        System.out.println("[PlayerService]: Created: - " + player + " in " + collection);
    }

    @CachePut(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null")
    @Override
    public void update(DatabasePlayer player) {
        DatabasePlayer p = playerRepository.save(player);
        System.out.println("[PlayerService]: Updated: - " + p);
    }

    @CachePut(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null")
    @Override
    public void update(DatabasePlayer player, PlayersCollections collection) {
        playerRepository.save(player, collection);
        System.out.println("[PlayerService]: Updated: - " + player + " in " + collection);
    }

    @Override
    public void delete(DatabasePlayer player) {
        playerRepository.delete(player);
        System.out.println("[PlayerService]: Deleted: - " + player);
    }

    @Override
    public void delete(DatabasePlayer player, PlayersCollections collection) {
        playerRepository.delete(player, collection);
        System.out.println("[PlayerService] Deleted: - " + player + " in " + collection);
    }

    @Override
    public void deleteAll() {
        playerRepository.deleteAll();
    }

    @Override
    public void deleteAll(PlayersCollections collection) {
        playerRepository.deleteAll(collection);
    }

    @Cacheable(cacheResolver = "cacheResolver", key = "#criteria.criteriaObject", unless = "#result == null")
    @Override
    public DatabasePlayer findOne(Criteria criteria, PlayersCollections collection) {
        return playerRepository.findOne(new Query().addCriteria(criteria), collection);
    }

    @Cacheable(cacheResolver = "cacheResolver", key = "#uuid", unless = "#result == null")
    @Override
    public DatabasePlayer findByUUID(UUID uuid) {
        return playerRepository.findByUUID(uuid);
    }

    @Cacheable(cacheResolver = "cacheResolver", key = "#uuid", unless = "#result == null")
    @Override
    public DatabasePlayer findByUUID(UUID uuid, PlayersCollections collection) {
        return playerRepository.findByUUID(uuid, collection);
    }

    @Override
    public List<DatabasePlayer> findAll() {
        return playerRepository.findAll();
    }

    @Override
    public List<DatabasePlayer> findAll(PlayersCollections collections) {
        return playerRepository.findAll(collections);
    }

    @Override
    public BulkOperations bulkOps() {
        return playerRepository.bulkOps();
    }


    @Override
    public List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections) {
        return playerRepository.getPlayersSorted(aggregation, collections);
    }

    @Override
    public <T> T convertDocumentToClass(Document document, Class<T> clazz) {
        return playerRepository.convertDocumentToClass(document, clazz);
    }


}
