package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class EchoOfRuin extends BaseSet {

    private float damagePerKillPercent;
    private boolean loseStacksOnDeath;

    @Override
    public void init() {
        super.init();
        this.damagePerKillPercent = getValue("damagePerKillPercent", float.class);
        this.loseStacksOnDeath = getValue("loseStacksOnDeath", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "echoOfRuin";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerKillPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        private int stacks = 0;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Listener listener = new Listener() {

                @EventHandler
                private void onEnemyDeath(WarlordsDeathEvent event) {
                    if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                        stacks = 0;
                        return;
                    }
                    if (!Objects.equals(event.getKiller(), warlordsPlayer)) {
                        return;
                    }
                    if (event.getWarlordsEntity().getTeam().equals(warlordsPlayer.getTeam())) {
                        return;
                    }
                    stacks++;
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Echo of Ruin",
                    null,
                    EchoOfRuin.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Echo of Ruin", 1 + (damagePerKillPercent * stacks / 100f));
                    }
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Echo of Ruin", 1 + (damagePerKillPercent * stacks / 100f));
                    }
            ));
        }

    }

}