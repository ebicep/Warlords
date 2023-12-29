package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

public class EventEmbellishedGrimoire extends AbstractMob implements AdvancedMob {

    public EventEmbellishedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Embellished Grimoire",
                5000,
                0.38f,
                0,
                550,
                685
        );
    }

    public EventEmbellishedGrimoire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "N5c6VhqxitMsqPsrme6Qio5QmaF7w5oWM3Ck7Qhw4x6PYie34ocd3BQ0Bk/ymq8lrv+BvQDBJTSxtacQNVKB6I5E1ec0M1nPF2oQNREpRjDiQ1MGCkst6+BCt5y/1IILSgvGAxJsGrgSY1tiPh00LoCJ680T5pzRvVwxrkAlJGzZEOFm3VBVwv344RNpYm19/wIKmvC5wThSwPBxfEDXCT+VR6Ia6HeCPGrWjvep0TmNjsfWQtWnNqE3CiWJL8hYQNXaPq+RGxiWCDE0HJEMxavDLGU70PeWsZu6vPs+apVdJGNA0t6A4Oqmt7ZUDr89UCXDIsaIaFRdJ83GrI8lEno/oKZMH2tdANKTOKtVngUtHk1xbMdPE3Qj9CgL3vlrhEs15RfBUrj/AnDKemVmCWlzojmDhJ54IB73o284861pNGovgOtoHQhNrCHAzMUPh5A3qL7asdaq/t35JcKVNGHgquwDdz3srhTabjwlt02WtyL0gKG2nWi9Zodw4n52OGBWVkJrzHSx6fTEnVG3mZcvD/3am5CLvGGJl+h5kqPuwdjmN/F38qF2VDf8K8HyxCaaSW2hI3lJANGndlerBLhxS6heZn2H5kV5sc40Go1VIE1gVK5xZ6yM/yONMU06llsmn7WHb9n6l7Vp2myYTMjMxQ9I7IF/nYhpBN4VHpg=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY2MzUyNjkxMjkxNSwKICAicHJvZmlsZUlkIiA6ICI5ZWEyMTQ0NGFiNjI0MWZkYjg5YjE2NDFhNDg2MGZiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICI3QUJDSE9VTiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YjYwNGU0YjE5OThlNDM2NDJmZmU5ZjE0OWIxNTE3NzlkMTRjODJmNjJkMjcyYTFhMTQ5NTlkYTUyMDNlOTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_EMBELLISHED_GRIMOIRE;
    }
}
