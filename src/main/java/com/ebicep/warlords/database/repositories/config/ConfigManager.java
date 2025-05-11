package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ConfigManager {

    public static final AbilitiesConfig ABILITIES_CONFIG = new AbilitiesConfig();
    public static final Config[] CONFIGS = {
            ABILITIES_CONFIG
    };
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

    public static <T> T getAbilityConfigValue(String namespace, String key, Class<T> fieldType) {
        return ABILITIES_CONFIG.getValue(namespace, key, fieldType);
    }

    public interface Config {

        String getName();

        void load(Document doc);

    }

}
