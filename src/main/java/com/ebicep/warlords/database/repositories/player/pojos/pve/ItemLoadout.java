package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
