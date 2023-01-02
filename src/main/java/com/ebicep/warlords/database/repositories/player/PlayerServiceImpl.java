package com.ebicep.warlords.database.repositories.player;


import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public DatabasePlayer create(DatabasePlayer player, PlayersCollections collection) {
        DatabasePlayer p = playerRepository.create(player, collection);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Created: - " + p + " in " + collection);
        return p;
    }

    @Override
    public DatabasePlayer update(DatabasePlayer player) {
        DatabasePlayer p = playerRepository.save(player);
        ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Updated: - " + p);
        return p;
    }

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

    @Override
    public DatabasePlayer findByUUID(UUID uuid) {
        return findByUUID(uuid, PlayersCollections.LIFETIME);
    }

    @Override
    public DatabasePlayer findByUUID(UUID uuid, PlayersCollections collection) {
        ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(collection);
        if (concurrentHashMap.containsKey(uuid)) {
            return concurrentHashMap.get(uuid);
        }
        DatabasePlayer databasePlayer = playerRepository.findByUUID(uuid, collection);
        if (databasePlayer != null) {
            concurrentHashMap.put(uuid, databasePlayer);
        }
        return databasePlayer;
    }

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
