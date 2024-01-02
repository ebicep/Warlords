package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MonaLisasPalms extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public MonaLisasPalms() {

    }

    public MonaLisasPalms(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "I was only 9 years old...";
    }

    @Override
    public String getBonus() {
        return "Ranged right-clicks have a 1% chance to insta-kill their target (Excluding Bosses).";
    }

    @Override
    public String getName() {
        return "Mona Lisa's Palms";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        String weaponRightClick = warlordsPlayer.getSpec().getWeapon().getName();
        if (!Utils.isProjectile(weaponRightClick)) {
            return;
        }
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (!Objects.equals(event.getAbility(), weaponRightClick)) {
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
