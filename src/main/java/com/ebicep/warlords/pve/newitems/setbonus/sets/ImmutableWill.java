package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class ImmutableWill extends BaseSet {

    private int extraDamagePercent;
    private int missingHealthPercent;

    @Override
    public void init() {
        super.init();
        this.extraDamagePercent = getValue("extraDamagePercent", int.class);
        this.missingHealthPercent = getValue("missingHealthPercent", int.class);
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(extraDamagePercent, missingHealthPercent);
    }

    @Override
    public String getConfigFieldName() {
        return "immutableWill";
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    ImmutableWill.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        float currentHealth = warlordsPlayer.getCurrentHealth();
                        float maxHealth = warlordsPlayer.getMaxHealth();
                        float missingHealthPercent = ((maxHealth - currentHealth) / maxHealth) * 100;

                        float damageBonus = (missingHealthPercent / missingHealthPercent) * extraDamagePercent;

                        if (damageBonus > 0) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (damageBonus / 100f));
                        }
                    }
            ));

        }

    }

}
