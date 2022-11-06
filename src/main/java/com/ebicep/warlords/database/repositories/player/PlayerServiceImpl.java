package com.ebicep.warlords.database.repositories.player;


import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;
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

    @Cacheable(cacheResolver = "cacheResolver", key = "#player.uuid")//, condition = "#player != null")
    @Override
    public DatabasePlayer create(DatabasePlayer player) {
        DatabasePlayer p = playerRepository.insert(player);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Created: - " + p);
        return p;
    }

    @Cacheable(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null")
    @Override
    public DatabasePlayer create(DatabasePlayer player, PlayersCollections collection) {
        DatabasePlayer p = playerRepository.create(player, collection);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Created: - " + p + " in " + collection);
        return p;
    }

    @CachePut(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null", condition = "#player != null")
    @Override
    public DatabasePlayer update(DatabasePlayer player) {
        DatabasePlayer p = playerRepository.save(player);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Updated: - " + p);
        return p;
    }

    @CachePut(cacheResolver = "cacheResolver", key = "#player.uuid", unless = "#player == null", condition = "#player != null")
    @Override
    public DatabasePlayer update(DatabasePlayer player, PlayersCollections collection) {
        DatabasePlayer p = playerRepository.save(player, collection);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Updated: - " + player + " in " + collection);
        return p;
    }

    @Override
    public void delete(DatabasePlayer player) {
        playerRepository.delete(player);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Deleted: - " + player);
    }

    @Override
    public void delete(DatabasePlayer player, PlayersCollections collection) {
        playerRepository.delete(player, collection);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Deleted: - " + player + " in " + collection);
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

    //@Cacheable(cacheResolver = "cacheResolver", key = "#result?.uuid", condition = "#result != null")
    @Override
    public DatabasePlayer findByNameIgnoreCase(String name) {
        return playerRepository.findByNameIgnoreCase(name);
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
        return playerRepository.bulkOps(); //WARNING DO NOT USE IN SCENARIO WITH PLAYERS ONLINE
    }


    @Override
    public List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections) {
        return playerRepository.getPlayersSorted(aggregation, collections);
    }

    @Override
    public <T> T convertDocumentToClass(Document document, Class<T> clazz) {
        return playerRepository.convertDocumentToClass(document, clazz);
    }

    @Override
    public void renameCollection(String collectionName, String newCollectionName, boolean dropTarget) {
        playerRepository.renameCollection(collectionName, newCollectionName, dropTarget);
    }


}
