package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;
import org.bukkit.Sound;

import java.util.Comparator;
import java.util.List;

public class Reservoir extends BaseSet {

    private int storedHealingCap;
    private int allyHealthThresholdPercent;
    private int dumpCooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.storedHealingCap = getValue("storedHealingCap", int.class);
        this.allyHealthThresholdPercent = getValue("allyHealthThresholdPercent", int.class);
        this.dumpCooldownSeconds = getValue("dumpCooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "reservoir";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(storedHealingCap, allyHealthThresholdPercent, (float) dumpCooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private float storedHealing;
        private int dumpCooldownTicks;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Reservoir.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (dumpCooldownTicks > 0) {
                            dumpCooldownTicks--;
                        }
                        if (ticksElapsed % 10 != 0 || dumpCooldownTicks > 0 || storedHealing <= 0 || warlordsPlayer.isDead()) {
                            return;
                        }
                        WarlordsEntity target = PlayerFilter.playingGame(warlordsPlayer.getGame())
                                .aliveTeammatesOf(warlordsPlayer)
                                .filter(ally -> ally.getCurrentHealth() / ally.getMaxHealth() * 100f < allyHealthThresholdPercent)
                                .stream()
                                .min(Comparator.comparingDouble(ally -> ally.getCurrentHealth() / ally.getMaxHealth()))
                                .orElse(null);
                        if (target == null) {
                            return;
                        }
                        float dumpAmount = storedHealing;
                        storedHealing = 0;
                        dumpCooldownTicks = dumpCooldownSeconds * 20;
                        Utils.playGlobalSound(target.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 1.6f);
                        target.addInstance(InstanceBuilder
                                .healing()
                                .cause(getName())
                                .source(warlordsPlayer)
                                .value(dumpAmount)
                                .flags(
                                        InstanceFlags.RECURSIVE,
                                        InstanceFlags.TRUE_HEALING,
                                        InstanceFlags.NO_HEALING_ORBS,
                                        InstanceFlags.NO_HEALING_LEECH
                                )
                        );
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealingValue) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (healed.equals(warlordsPlayer) || !healed.isTeammate(warlordsPlayer)) {
                            return;
                        }
                        float excess = getExcessHealing(currentHealingValue, healed.getCurrentHealth(), healed.getMaxHealth());
                        if (excess > 0) {
                            storedHealing = Math.min(storedHealingCap, storedHealing + excess);
                        }
                    }
            ));
        }

        private float getExcessHealing(MultiFloatModifiable currentHealingValue, float targetCurrentHealth, float targetMaxHealth) {
            float healingAmount = currentHealingValue.getCalculatedValue();
            float healthAfterHealing = targetCurrentHealth + healingAmount;
            if (healthAfterHealing <= targetMaxHealth) {
                return 0;
            }
            return healthAfterHealing - targetMaxHealth;
        }
    }

}
