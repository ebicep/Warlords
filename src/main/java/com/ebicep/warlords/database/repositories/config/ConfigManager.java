package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigManager {

    public static final List<String> DEFAULT_NAMESPACES = List.of("pvp", "pve", "weapon", "td");
    public static final List<String> PVE_NAMESPACES = List.of("pve", "pvp", "weapon", "td");
    public static final List<String> TD_NAMESPACES = List.of("td", "pve", "pvp", "weapon");
    public static final AbilitiesConfig ABILITIES_CONFIG = new AbilitiesConfig();
    public static final SpecBoostConfig SPEC_BOOST_CONFIG = new SpecBoostConfig();
    public static final SpecializationsConfig SPECIALIZATIONS_CONFIG = new SpecializationsConfig();
    public static final GameConfig GAME_CONFIG = new GameConfig();
    public static final MobsConfig MOBS_CONFIG = new MobsConfig();
    public static final NewItemsConfig NEW_ITEMS_CONFIG = new NewItemsConfig();
    public static final FeatureFlagsConfig FEATURE_FLAGS_CONFIG = new FeatureFlagsConfig();
    public static final Config[] CONFIGS = {ABILITIES_CONFIG, SPEC_BOOST_CONFIG, SPECIALIZATIONS_CONFIG, GAME_CONFIG, MOBS_CONFIG, NEW_ITEMS_CONFIG, FEATURE_FLAGS_CONFIG};
    public static final String COLLECTION_NAME = "Config";

    public static void loadConfigs(MongoDatabase warlordsDatabase) {
        ChatUtils.MessageType.CONFIG.sendMessage("Loading config from database...");
        MongoCollection<Document> collection = warlordsDatabase.getCollection(COLLECTION_NAME);

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                loadConfig(doc);
            }
        }
        ChatUtils.MessageType.CONFIG.sendMessage("Finished loading config from database.");
    }

    private static void loadConfig(Document doc) {
        String type = doc.getString("type");
        for (Config value : CONFIGS) {
            if (value.getName().equalsIgnoreCase(type)) {
                value.load(doc.get("data", Document.class));
                ChatUtils.MessageType.CONFIG.sendMessage("Loaded config: " + type);
                break;
            }
        }
    }

    public static void loadConfigsFromFolder() {
        Path dirPath = Paths.get(Warlords.getInstance().getDataFolder().toString(), "config");
        if (!Files.isDirectory(dirPath)) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config folder does not exist: " + dirPath);
            return;
        }
        try (Stream<Path> paths = Files.list(dirPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().toLowerCase().endsWith(".json"))
                 .forEach(path -> {
                     try (Stream<String> lines = Files.lines(path)) {
                         String jsonString = lines.collect(Collectors.joining(System.lineSeparator()));
                         Document bsonDocument = Document.parse(jsonString);
                         loadConfig(bsonDocument);
                     } catch (IOException e) {
                         ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
                     }
                 });
        } catch (IOException e) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
    }

    public static <T> T getAbilityConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ABILITIES_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> List<T> getAbilityConfigListValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ABILITIES_CONFIG.getListValue(namespaces, key, fieldType);
    }

    public static <T> T getSpecBoostConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return SPEC_BOOST_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> T getSpecBoostConfigValue(List<String> namespaces, String key, Class<T> fieldType, boolean optionalField) {
        return SPEC_BOOST_CONFIG.getValue(namespaces, key, fieldType, optionalField);
    }

    public static <T> T getSpecBoostConfigValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue, boolean optionalField) {
        return SPEC_BOOST_CONFIG.getValue(namespaces, key, fieldType, defaultValue, optionalField);
    }

    public static <T> List<T> getSpecBoostConfigListValue(List<String> namespaces, String key, Class<T> fieldType) {
        return SPEC_BOOST_CONFIG.getListValue(namespaces, key, fieldType);
    }

    public static <T> List<T> getSpecBoostConfigListValue(List<String> namespaces, String key, Class<T> fieldType, boolean optionalField) {
        return SPEC_BOOST_CONFIG.getListValue(namespaces, key, fieldType, optionalField);
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

    public static <T> T getNewItemsConfigValue(List<String> namespaces, String key, Class<T> fieldType) {
        return NEW_ITEMS_CONFIG.getValue(namespaces, key, fieldType);
    }

    public static <T> List<T> getNewItemsConfigListValue(List<String> namespaces, String key, Class<T> fieldType) {
        return NEW_ITEMS_CONFIG.getListValue(namespaces, key, fieldType);
    }

    public static <T> Map<String, T> getNewItemsConfigMapValue(List<String> namespaces, String key, Class<T> valueType) {
        return NEW_ITEMS_CONFIG.getMapValue(namespaces, key, valueType);
    }

    public interface Config {

        String getName();

        void load(Document doc);

        default <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
            return ConfigUtils.getValue(getConfigDocument(), namespaces, key, fieldType);
        }

        Document getConfigDocument();

        default <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
            return ConfigUtils.getValue(getConfigDocument(), namespaces, key, fieldType, defaultValue);
        }

        default <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, boolean optionalField) {
            return ConfigUtils.getValue(getConfigDocument(), namespaces, key, fieldType, optionalField);
        }

        default <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue, boolean optionalField) {
            return ConfigUtils.getValue(getConfigDocument(), namespaces, key, fieldType, defaultValue, optionalField);
        }

        default <T> List<T> getListValue(List<String> namespaces, String key, Class<T> itemType) {
            return ConfigUtils.getListValue(getConfigDocument(), namespaces, key, itemType);
        }

        default <T> List<T> getListValue(List<String> namespaces, String key, Class<T> itemType, boolean optionalField) {
            return ConfigUtils.getListValue(getConfigDocument(), namespaces, key, itemType, optionalField);
        }

        default <T> Map<String, T> getMapValue(
                List<String> namespaces,
                String key,
                Class<T> valueType
        ) {
            return ConfigUtils.getMapValue(getConfigDocument(), namespaces, key, valueType);
        }

        default <T> Map<String, T> getMapValue(
                List<String> namespaces,
                String key,
                Class<T> valueType,
                boolean optionalField
        ) {
            return ConfigUtils.getMapValue(getConfigDocument(), namespaces, key, valueType, optionalField);
        }

    }

}
