package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.DamagePowerup;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldTestCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (commandSender instanceof Player player) {
            if (!player.isOp()) {
                return;
            }
            WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
            if (warlordsEntity != null) {
                RegularCooldown<DamagePowerup> cooldown = new RegularCooldown<>(
                        "TEST",
                        "TEST",
                        null,
                        null,
                        warlordsEntity,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                        },
                        100
                );
                cooldown.addModifier(
                        Modifier.ON_INCOMING_SHIELD_DAMAGE,
                        (event, currentDamageValue, isCrit) -> {

                        }
                );
                cooldown.addModifier(
                        Modifier.ON_OUTGOING_SHIELD_DAMAGE,
                        (event, currentDamageValue, isCrit) -> {

                        }
                );
                cooldown.addModifier(
                        Modifier.MODIFY_OUTGOING_DAMAGE_AFTER_INTERVENE,
                        (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "TEST2", 2);
                        }
                );
                cooldown.addModifier(
                        Modifier.MODIFY_OUTGOING_DAMAGE_AFTER_INTERVENE,
                        (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "TEST3", 3);
                        }
                );
                cooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Ice Barrier",
                                    0.75f
                            );
                        }
                );
                cooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(
                                    75, MultiFloatModifiable.ApplyFloatModifiableType.MULTIPLICATIVE,
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Fortifying Hex 1",
                                    -0.1f
                            );
                        }
                );
                cooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(
                                    75, MultiFloatModifiable.ApplyFloatModifiableType.MULTIPLICATIVE,
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Fortifying Hex 2",
                                    -0.1f
                            );
                        }
                );
                cooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(
                                    75, MultiFloatModifiable.ApplyFloatModifiableType.MULTIPLICATIVE,
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Fortifying Hex 3",
                                    -0.1f
                            );
                        }
                );
                cooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(
                                    75, MultiFloatModifiable.ApplyFloatModifiableType.MULTIPLICATIVE,
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Sanctuary",
                                    -0.6f,
                                    f -> {
                                        ChatChannels.sendDebugMessage(player, "" + f);
                                    }
                            );
                        }
                );
                warlordsEntity.getCooldownManager().addCooldown(cooldown);
                warlordsEntity.addInstance(InstanceBuilder
                        .damage()
                        .cause("Test Damage")
                        .source(warlordsEntity)
                        .value(100)
                        .flags(InstanceFlags.IGNORE_SELF_RES)
                );
            }
        }
        ChatChannels.sendDebugMessage(commandSender instanceof Player player ? player : null, "Executed OldTestCommand");
    }

}
