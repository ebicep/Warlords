package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class Merciful extends BaseSet {

    private int healingToShieldPercent;
    private int maxShieldPercentOfMaxHealth;
    private int shieldDurationSeconds;
    private int cooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.healingToShieldPercent = getValue("healingToShieldPercent", int.class);
        this.maxShieldPercentOfMaxHealth = getValue("maxShieldPercentOfMaxHealth", int.class);
        this.shieldDurationSeconds = getValue("shieldDurationSeconds", int.class);
        this.cooldownSeconds = getValue("cooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "merciful";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingToShieldPercent, maxShieldPercentOfMaxHealth, (float) shieldDurationSeconds, (float) cooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private int cooldownTicks;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Merciful.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (cooldownTicks > 0) {
                            cooldownTicks--;
                        }
                    }
            ).addModifier(
                    Modifier.ON_OUTGOING_HEALING,
                    (event, currentHealValue, isCrit) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE) || cooldownTicks > 0 || currentHealValue <= 0) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (!(healed instanceof WarlordsPlayer) || healed.equals(warlordsPlayer) || !healed.isTeammate(warlordsPlayer)) {
                            return;
                        }
                        float shieldHealth = Math.min(
                                currentHealValue * healingToShieldPercent / 100f,
                                healed.getMaxHealth() * maxShieldPercentOfMaxHealth / 100f
                        );
                        if (shieldHealth <= 0) {
                            return;
                        }
                        giveShield(warlordsPlayer, healed, shieldHealth);
                        cooldownTicks = cooldownSeconds * 20;
                    }
            ));
        }

        private void giveShield(WarlordsPlayer from, WarlordsEntity to, float shieldHealth) {
            Shield shield = new Shield(getName(), shieldHealth);
            to.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getName() + " Shield",
                    "SHIELD",
                    Shield.class,
                    shield,
                    from,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                    },
                    shieldDurationSeconds * 20
            ) {

                @Override
                public PlayerNameData addPrefixFromOther() {
                    return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(from), NamedTextColor.YELLOW);
                }
            });
        }
    }

}
