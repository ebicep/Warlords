package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.Sanctuary;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.EnumSet;
import java.util.List;

public class SanctuaryOfRetribution implements SpecBoostManager.SpecBoost<SanctuaryOfRetribution> {

    private int sanctuaryDamageReductionPerHexStack;

    @Override
    public void init() {
        this.sanctuaryDamageReductionPerHexStack = getValue("sanctuaryDamageReductionPerHexStack", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sanctuaryOfRetribution";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(sanctuaryDamageReductionPerHexStack);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SanctuaryOfRetribution get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;
        private int sanctuaryAdditionalDamageReduction;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(Sanctuary.class).forEach(sanctuary -> {
                sanctuary.setAdditionalDamageReduction(sanctuary.getAdditionalDamageReduction() + sanctuaryDamageReductionPerHexStack);
                sanctuaryAdditionalDamageReduction = sanctuary.getAdditionalDamageReduction();
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (event.isHealingInstance()) {
                return;
            }
            if (event.getSource().isTeammate(warlordsEntity)) {
                return;
            }
            EnumSet<InstanceFlags> flags = event.getWarlordsDamageHealingEvent().getFlags();
            if (flags.contains(InstanceFlags.RECURSIVE)) {
                return;
            }
            int hexStacks = (int) new CooldownFilter<>(event.getWarlordsEntity(), RegularCooldown.class)
                    .filterCooldownFrom(warlordsEntity)
                    .filterCooldownClass(FortifyingHex.FortifyingHexData.class)
                    .stream()
                    .count();
            FortifyingHex hex = FortifyingHex.getFromHex(warlordsEntity);
            int maxStacks = hex.getMaxStacks();
            if (hexStacks < maxStacks) {
                return;
            }
            float valueBeforeAllReduction = event.getValueBeforeAllReduction();
            double damageReduction = 1 - Math.pow(AbstractAbility.convertToDivisionDecimal(hex.getDamageReduction().getCalculatedValue() + sanctuaryAdditionalDamageReduction),
                    maxStacks
            );
            float damageToReflect = (float) (valueBeforeAllReduction * damageReduction);
            Utils.playGlobalSound(event.getWarlordsEntity().getLocation(), Sound.ENTITY_VEX_HURT, 1, 1.9f);
            event.getSource().addInstance(InstanceBuilder
                    .damage()
                    .cause(getStringName())
                    .source(warlordsEntity)
                    .value(damageToReflect)
                    .flags(InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE)
            );
        }

    }

}
