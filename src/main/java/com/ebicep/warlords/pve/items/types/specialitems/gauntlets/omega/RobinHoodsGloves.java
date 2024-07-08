package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RobinHoodsGloves extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public RobinHoodsGloves() {

    }

    public RobinHoodsGloves(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Robin Hood's Gloves";
    }

    @Override
    public String getBonus() {
        return "Non-ranged right-clicks have a 1% chance to insta-kill their target (Excluding Bosses).";
    }

    @Override
    public String getDescription() {
        return "Finally, all this thieving is paying off.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        String weaponRightClick = warlordsPlayer.getSpec().getWeapon().getName();
        if (Utils.isProjectile(weaponRightClick)) {
            return;
        }
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getSource(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (!Objects.equals(event.getCause(), weaponRightClick)) {
                    return;
                }
                WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                if (warlordsEntity instanceof WarlordsNPC warlordsNPC) {
                    if (warlordsNPC.getMob() instanceof BossLike) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() > 0.01) {
                        return;
                    }
                    event.setMin(warlordsEntity.getCurrentHealth() + 1);
                    event.setMax(warlordsEntity.getCurrentHealth() + 1);
                    event.getFlags().add(InstanceFlags.IGNORE_SELF_RES);
                    event.getFlags().add(InstanceFlags.TRUE_DAMAGE);
                }
            }
        });
    }

}
