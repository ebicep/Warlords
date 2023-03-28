package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.statpool.StatPoolAbility;
import com.ebicep.warlords.pve.items.statpool.StatPoolWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemLoadout {

    private String name;
    @Field("creation_date")
    private Instant creationDate = Instant.now();
    private List<UUID> items = new ArrayList<>();
    private DifficultyIndex difficulty;
    private Specializations spec;


    public ItemLoadout(String name) {
        this.name = name;
    }

    public int getWeight(ItemsManager itemsManager) {
        int weight = 0;
        for (AbstractItem<?, ?, ?> item : itemsManager.getItemInventory()) {
            if (items.contains(item.getUUID())) {
                weight += item.getWeight();
            }
        }
        return weight;
    }

    public List<AbstractItem<?, ?, ?>> getActualItems(ItemsManager itemsManager) {
        List<AbstractItem<?, ?, ?>> items = new ArrayList<>();
        for (AbstractItem<?, ?, ?> item : itemsManager.getItemInventory()) {
            if (this.items.contains(item.getUUID())) {
                items.add(item);
            }
        }
        return items;
    }

    public void applyToWarlordsPlayer(ItemsManager itemsManager, WarlordsPlayer warlordsPlayer) {
        HashMap<StatPoolWarlordsPlayer, Integer> statPoolWarlordsPlayer = new HashMap<>();
        getActualItems(itemsManager)
                .stream()
                .filter(item -> item.getStatPoolClass().isAssignableFrom(StatPoolWarlordsPlayer.class))
                .forEach(item -> item.getStatPool().forEach((stat, tier) -> statPoolWarlordsPlayer.merge((StatPoolWarlordsPlayer) stat, tier, Integer::sum)));
        statPoolWarlordsPlayer.forEach((stat, tier) -> stat.applyToWarlordsPlayer(warlordsPlayer, tier));

        HashMap<StatPoolAbility, Integer> statPoolAbility = new HashMap<>();
        getActualItems(itemsManager)
                .stream()
                .filter(item -> item.getStatPoolClass().isAssignableFrom(StatPoolAbility.class))
                .forEach(item -> item.getStatPool().forEach((stat, tier) -> statPoolAbility.merge((StatPoolAbility) stat, tier, Integer::sum)));
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            statPoolAbility.forEach((stat, tier) -> stat.applyToAbility(ability, tier));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public List<UUID> getItems() {
        return items;
    }

    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyIndex difficulty) {
        this.difficulty = difficulty;
    }

    public Specializations getSpec() {
        return spec;
    }

    public void setSpec(Specializations spec) {
        this.spec = spec;
    }
}
