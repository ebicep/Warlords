package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

public class EventScriptedGrimoire extends AbstractMob implements ChampionMob {

    public EventScriptedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Scripted Grimoire",
                6500,
                0.21f,
                10,
                150,
                250
        );
    }

    public EventScriptedGrimoire(
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
                "c2icpuCtW+tFbJcoT7TrqwAjmMuUlstBJebD2PxEE/ylLerisW2KwM3Fz/iOse0+BLe7nRWIGpKvEqf5bzDL6gXAQS0w5WJwWX/S68uJSe0LaLnnOIjW0szL3iZH6lN7zH68H1l34eDN+mRxwiQleenhZsJgQYPDejUmcmVjfVk+DHrpadF0v9/ctYzy1xhx3lheX/yvx3R250QZF/as7H8uwyqX485Sd6d+vDBaeDWlo4qaa2fvYki2WO5rBeM9COhe6gpB/QGGtZfNy7PwRbrIDBhokSjGQNrOjQMH5MV7iVmUotkM7iMSue3qJ1fVnO4mPOWuXynss4IFHZRAhUaMc1LEGRoIreGI2d6BF0FIEUBDNfrKc9VhVwxTB329eXOXGPcu7yUAnIy4LJ0yNsxBGCB3JZ15zTfP8ZJN/P2ESPyZwb8CJHM5feVWHIgbK4/Ve8iNmhsdPpvtWNCcjMMQjzG3pocwxY3yi4PyurP0aDZ+Qr2Ho5BkpTA3gELW6uC7bxutcogw/NIP0q0weTMIzvMmud5YwepSdwsKSJJ2M9IBNMkMMSfmXpx6Uq40TD7OdO6S9JXrvle4UuQkxVIzBhR/UQ8CkLiF4zPDmYd/U+PqN95e/ErwITLNgcEQ1AKiDg2zgW0anab8mF7zcdKMN0EMn3yM84eIGkz/K9Q=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5ODc0ODc1ODY1OCwKICAicHJvZmlsZUlkIiA6ICJlNTZkYjMyZWVkOGQ0NTY3YWI4YmZjOWMwYmM1YWFlMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJraXVnYW1lcjk1NiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jODg0Y2MxNzJmZGI2MDQ1ODExNzI3ZGE0ZWE1OTY0MTNiYTY5ZjNiZWM2NmQ5NDkzMjRhYTNhYWRmNDE4MGYiCiAgICB9CiAgfQp9"
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_SCRIPTED_GRIMOIRE;
    }
}
