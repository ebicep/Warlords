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
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ShawlOfMithra extends AbstractFixedItem implements FixedItemAppliesToPlayer {

    public static final HashMap<BasicStatPool, Float> STAT_POOL = new HashMap<>() {{
        put(BasicStatPool.DAMAGE, 70f);
        put(BasicStatPool.HEALING, 70f);
        put(BasicStatPool.MAX_ENERGY, 15f);
        put(BasicStatPool.REGEN_TIMER, 200f);

    }};

    public ShawlOfMithra() {
        super(ItemTier.DELTA);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ShawlOfMithra.class,
                null,
                warlordsPlayer,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                WarlordsEntity attacker = event.getSource();
                if (attacker instanceof WarlordsNPC warlordsNPC) {
                    if (warlordsNPC.getMob().getLevel() < 2) {
                        return currentDamageValue * 0.9f;
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
    public HashMap<BasicStatPool, Float> getStatPool() {
        return STAT_POOL;
    }

    @Override
    public int getWeight() {
        return 45;
    }

    @Override
    public ItemType getType() {
        return ItemType.TOME;
    }

    @Override
    protected ItemStack getItemStack() {
        return Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200);
    }

    @Override
    public String getEffect() {
        return "Queenly Majesty";
    }

    @Override
    public String getEffectDescription() {
        return "Take 10% less damage from all mobs below 2*.";
    }
}
