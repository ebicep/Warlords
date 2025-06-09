package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class ConfigManager {

    public static final List<String> DEFAULT_NAMESPACES = List.of("pvp", "pve", "weapon", "td");
    public static final AbilitiesConfig ABILITIES_CONFIG = new AbilitiesConfig();
    public static final SpecBoostConfig SPEC_BOOST_CONFIG = new SpecBoostConfig();
    public static final SpecializationsConfig SPECIALIZATIONS_CONFIG = new SpecializationsConfig();
    public static final GameConfig GAME_CONFIG = new GameConfig();
    public static final Config[] CONFIGS = {ABILITIES_CONFIG, SPEC_BOOST_CONFIG, SPECIALIZATIONS_CONFIG, GAME_CONFIG};
    private static final String COLLECTION_NAME = "Config";

    public static void loadConfigs(MongoDatabase warlordsDatabase) {
        ChatUtils.MessageType.CONFIG.sendMessage("Loading config from database...");
        MongoCollection<Document> collection = warlordsDatabase.getCollection(COLLECTION_NAME);

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String type = doc.getString("type");
                for (Config value : CONFIGS) {
                    if (value.getName().equalsIgnoreCase(type)) {
                        value.load(doc.get("data", Document.class));
                        ChatUtils.MessageType.CONFIG.sendMessage("Loaded config: " + type);
                        break;
                    }
                }
            }
        }
        ChatUtils.MessageType.CONFIG.sendMessage("Finished loading config from database.");
    }

    public static <T> T getAbilityConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ABILITIES_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> T getSpecBoostConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return SPEC_BOOST_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> T getSpecsConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return SPECIALIZATIONS_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> List<T> getSpecsConfigListValue(List<String> namespaces, String key, Class<T> fieldType) {
        return SPECIALIZATIONS_CONFIG.getListValue(namespaces, key, fieldType);
    }

    public static <T> T getGameConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return GAME_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> T getGameConfigValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return GAME_CONFIG.getValue(namespaces, key, fieldType, defaultValue);
    }

    public interface Config {

        String getName();

        void load(Document doc);

    }

}
