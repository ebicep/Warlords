package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Sharpshooter extends BaseSet {

    private int instakillChance;

    @Override
    public void init() {
        super.init();
        this.instakillChance = getValue("instakillChance", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sharpshooter";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(instakillChance);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            String weaponRightClick = warlordsPlayer.getSpec().getWeapon().getName();
            if (!Utils.isProjectile(weaponRightClick)) {
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
                        event.applyToMinMax(floatModifiable ->
                                floatModifiable.addModifier(FloatModifiable.ModifierType.OVERRIDING, getName(), warlordsEntity.getCurrentHealth() + 1)
                        );
                        event.getFlags().add(InstanceFlags.IGNORE_SELF_RES);
                        event.getFlags().add(InstanceFlags.TRUE_DAMAGE);
                    }
                }
            });
        }

    }

}