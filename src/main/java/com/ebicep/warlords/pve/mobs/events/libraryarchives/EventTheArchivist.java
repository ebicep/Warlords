package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventTheArchivist extends AbstractMob implements BossMob, Unsilencable {

    private int grimoireDeathCounter = 0;

    public EventTheArchivist(Location spawnLocation) {
        this(
                spawnLocation,
                "The Archivist",
                125000,
                0.21f,
                15,
                0,
                0
        );
    }

    public EventTheArchivist(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new CripplingStrike() {{
                    this.getCooldown().setBaseValue(5);
                    this.pveMasterUpgrade = true;
                }},
                new ChainLightning(5, 5) {{
                    this.pveMasterUpgrade2 = true;
                }},
                new GroundSlamBerserker(8, 8),
                new PrismGuard(18, 18),
                new LastStand(25, 25)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_THE_ARCHIVIST;
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "OqO5GozF3TsoHxk/OGwvN8w7NpGNMKNZqOkWGRGSKDXpqBllwn9JBW0UCUZCVJ1WGBjiAC5oBeGtP2XrUxvLBbpGHhyfX4snM6D6dOBw40lXPzPRE0w/XTpVKpKrbTf1QLeI/AFCyCF1SFwuuCZzBNmyrqChO9e5sjkl3m31h6cI9jwO8omuISybfqrxNpPY2wcpsXvr5iHk5tvnOzvn4G7nxAZnMC581fuCD5TxVFWLgThH8YzCZOTlXbyFnNGqQLwQNYJyD48SZT1rfTtNgVZ+iiAbvJvRFdhSV8Wla7ZIh4vW0z4m3I0o1YfyLs4TULgrhCEg69j/NaM1VU+n4+B9vRCm6ptjrDL5vG+ljUy3zNi3kck9XGbX7MTIxPRhXnrvbbEblNnQUOLLSw2cpr/AtWBtkWEUD36OdQ25NDGQtbrEzkZWpLy4kyubtFdRjDArseuFUoXpKtrV2oL3pOKVzi45xtQNL3t1IORyDbJU3T8RcTB78tgVDvHz+rfhBqQO+iiYmdn9zwyoPMsP+lZQityFrPU0l7iNGpL6bGJHOdxOlm+B/21/SdCs64blKhDKY6+Glk9tV1i6dMxlSlZD+I6xn9Y0xh/GSR0fToTroZB2tg31rUfFJ0PjvXfORKRIP95BvbkRffzvSgvrXE/tIU7LkSr3+DJs5zhFn/s=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY4MTA3MTM5ODI1OSwKICAicHJvZmlsZUlkIiA6ICI5ZWEyMTQ0NGFiNjI0MWZkYjg5YjE2NDFhNDg2MGZiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICI3QUJDSE9VTiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMzViYTliNTU3Zjk4ZGNlYTQzYzdkZjg0YWVmNThjM2IzOThhNzQ1MDFiMzQ5OWYyNTI4ZWVmN2E1NTZlOTFjIgogICAgfQogIH0KfQ=="
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        option.getGame().registerEvents(new Listener() {

            final FloatModifiable.FloatModifier modifier = warlordsNPC.getHealth().addAdditiveModifier(name + " (Base)", 0);


            @EventHandler
            public void onAbilityUse(WarlordsAbilityActivateEvent.Post event) {
                if (event.getWarlordsEntity().equals(warlordsNPC)) {
                    modifier.setModifier(modifier.getModifier() - 500);
                }
            }

            @EventHandler
            public void onMobDeath(WarlordsDeathEvent event) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC wNPC && wNPC.getMob() instanceof EventGrimoire) {
                    grimoireDeathCounter++;
                    if (grimoireDeathCounter % 4 == 0) {
                        warlordsNPC.getSpec().setDamageResistance(warlordsNPC.getSpec().getDamageResistance() + 5);
                    }
                }
            }
        });
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 1.5;
    }

}
