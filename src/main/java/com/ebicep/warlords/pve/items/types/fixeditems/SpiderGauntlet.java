package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.AppliesToPlayer;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;

import java.util.HashMap;

public class SpiderGauntlet extends AbstractFixedItem implements AppliesToPlayer {

    public static final HashMap<BasicStatPool, Integer> STAT_POOL = new HashMap<>() {{
        put(BasicStatPool.DAMAGE, 5);
        put(BasicStatPool.HEALING, 5);
        put(BasicStatPool.CRIT_CHANCE, 2);
        put(BasicStatPool.CRIT_MULTI, 4);

    }};

    public SpiderGauntlet() {
        super(ItemType.BUCKLER, ItemTier.GAMMA);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                SpiderGauntlet.class,
                null,
                warlordsPlayer,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                WarlordsEntity attacker = event.getAttacker();
                if (attacker instanceof WarlordsNPC) {
                    WarlordsNPC warlordsNPC = (WarlordsNPC) attacker;
                    AbstractMob<?> mob = warlordsNPC.getMob();
                    if (mob instanceof Spider || mob instanceof EventEggSac) {
                        return currentDamageValue * 1.3f;
                    }
                }
                return currentDamageValue;
            }

        });
    }

    @Override
    public String getName() {
        return "Shawl of Mithra";
    }

    @Override
    public HashMap<BasicStatPool, Integer> getStatPool() {
        return STAT_POOL;
    }

    @Override
    public int getWeight() {
        return 35;
    }

    @Override
    public String getEffect() {
        return "Exterminator";
    }

    @Override
    public String getEffectDescription() {
        return "Deal 30% more damage to Spiders and Egg Sacs.";
    }
}
