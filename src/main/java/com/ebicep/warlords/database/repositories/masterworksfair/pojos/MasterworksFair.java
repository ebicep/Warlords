package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.util.java.DateUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "Masterworks_Fair")
public class MasterworksFair {

    @Id
    protected String id;

    @Field("start_date")
    private Instant startDate = DateUtil.getResetDateLatestMonday();
    @Field("common_entries")
    private List<MasterworksFairPlayerEntry> commonPlayerEntries = new ArrayList<>();
    @Field("rare_entries")
    private List<MasterworksFairPlayerEntry> rarePlayerEntries = new ArrayList<>();
    @Field("epic_entries")
    private List<MasterworksFairPlayerEntry> epicPlayerEntries = new ArrayList<>();

    public MasterworksFair() {
    }

    public String getId() {
        return id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public List<MasterworksFairPlayerEntry> getCommonPlayerEntries() {
        return commonPlayerEntries;
    }

    public List<MasterworksFairPlayerEntry> getRarePlayerEntries() {
        return rarePlayerEntries;
    }

    public List<MasterworksFairPlayerEntry> getEpicPlayerEntries() {
        return epicPlayerEntries;
    }

    @Override
    public String toString() {
        return "MasterworksFair{startDate=" + startDate + ", commonPlayerEntries=" + commonPlayerEntries.size() + ", rarePlayerEntries=" + rarePlayerEntries.size() + ", epicPlayerEntries=" + epicPlayerEntries.size() + '}';
    }
}
