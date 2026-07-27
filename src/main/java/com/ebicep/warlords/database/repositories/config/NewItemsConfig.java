package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NewItemsConfig implements ConfigManager.Config {

    private static final String DROWNED_REALMS_OVERRIDES = "/new-items-drowned-realms-overrides.json";

    public Document newItemsConfig;

    @Override
    public String getName() {
        return "NEW_ITEMS";
    }

    @Override
    public void load(Document doc) {
        this.newItemsConfig = applyDrownedRealmsOverrides(doc);
        NewItemsUtils.reloadConfig();
    }

    private Document applyDrownedRealmsOverrides(Document source) {
        Document merged = Document.parse(source.toJson());
        try (InputStream inputStream = NewItemsConfig.class.getResourceAsStream(DROWNED_REALMS_OVERRIDES)) {
            if (inputStream == null) {
                throw new IOException("Missing resource " + DROWNED_REALMS_OVERRIDES);
            }
            Document overrides = Document.parse(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            Document mergedPve = merged.get("pve", Document.class);
            if (mergedPve == null) {
                mergedPve = new Document();
                merged.put("pve", mergedPve);
            }
            Document mergedSets = mergedPve.get("sets", Document.class);
            if (mergedSets == null) {
                mergedSets = new Document();
                mergedPve.put("sets", mergedSets);
            }
            Document overridePve = overrides.get("pve", Document.class);
            Document overrideSets = overridePve == null ? null : overridePve.get("sets", Document.class);
            if (overrideSets != null) {
                overrideSets.forEach(mergedSets::put);
            }
            mergedSets.remove("exergis");
        } catch (Exception e) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
        return merged;
    }

    @Override
    public Document getConfigDocument() {
        return this.newItemsConfig;
    }
}
