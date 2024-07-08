package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

public class EventUnpublishedGrimoire extends AbstractMob implements IntermediateMob {

    public EventUnpublishedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Unpublished Grimoire",
                3500,
                0.38f,
                0,
                375,
                575
        );
    }

    public EventUnpublishedGrimoire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
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
                "uwktFPJzXOuHVY1EUR0uw82p2lF3kJE/MPzPIt4VkFZyUtxR6hS6toseyapeK6iYSeB+qEbEdiYA7c918EuSm/CQMC/8MctEIcUK7vCCe8VFadsFqo0loXKd4TVqw/hId8gaUao1YQqL25MwEKkBlEVGf03zASXgR2ZFz013UszX+dYw+yPUl5air3Ee9XXY5gLlgUVlYLGypjNbZTkxGxNwhfQTSWbampXzRjOBSLM3GaYjvtbrmLSnaB2Qb4syGHOnqWzNfqYKn/33xZCWBC7XXnyQEva4TGO9VG2K/8fA2GN/f3xFzzTD5IIhfflOxZ5yBB/UKhMxhz8yFnCyOVyZHy3QlsjJItTgCCTAKCcoxdCVO6ddlq4MbDtdrjFGoSGZPYCJOJHJx9Ux+lkjngBKvq0HD64bYTZMpE03WbNQofzcmWX/Vy1U76vzhCcYuUM3Motc1kfylB2hPAHWbRU6P+opMGsZ/cLo0rMEjf8DQoMgeBcosZOoQfF9VFaSHp1B/+1CsBrrCbsCwaQCLf5pS2Lfl/KXJlas6u/jlklKJBNvwgmBqu39PENhBCZOIaYwUpOgdaPB6SAN2HH/NX8c3qmiSMft1QlfbnV5C0kTFM8KvRGJ7+qR/EgWDv206Kw8DdtvmDe0QuQLP7FzmVdXw7u+Qx12OOVYR1lb26o=",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwMzcyNDM0NjU2MSwKICAicHJvZmlsZUlkIiA6ICJiZDNhNWRmY2ZkZjg0NDczOTViZDJiZmUwNGY0YzAzMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJwcmVja3Jhc25vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IyOGIxOWQwNmUyNWQ5M2M5ZGViZTUwZWQ1ZTg4OGM1ZjBhZDY3ZmMwMjgxY2VkNTc0ODY3M2M3ZGMzNjgzYjIiCiAgICB9CiAgfQp9"
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_UNPUBLISHED_GRIMOIRE;
    }
}
