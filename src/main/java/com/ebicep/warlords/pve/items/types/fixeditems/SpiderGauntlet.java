package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class SpiderGauntlet extends AbstractFixedItem implements FixedItemAppliesToPlayer {

    public static final HashMap<BasicStatPool, Integer> STAT_POOL = new HashMap<>() {{
        put(BasicStatPool.DAMAGE, 50);
        put(BasicStatPool.HEALING, 50);
        put(BasicStatPool.CRIT_CHANCE, 20);
        put(BasicStatPool.CRIT_MULTI, 40);

    }};

    public SpiderGauntlet() {
        super(ItemTier.GAMMA);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
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
                WarlordsEntity victim = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getAttacker();
                if (victim instanceof WarlordsNPC warlordsNPC && Objects.equals(attacker, warlordsPlayer)) {
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
        return "Spider Gauntlet";
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
    public ItemType getType() {
        return ItemType.GAUNTLET;
    }

    @Override
    protected ItemStack getItemStack() {
        return SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER);
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
