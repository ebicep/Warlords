package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

@CommandAlias("printfloatmodifiable|pfm")
@CommandPermission("group.administrator")
public class PrintFloatModifiableCommand extends BaseCommand {

    @Default
    @CommandCompletion("@floatmodifiabletype @warlordsplayers")
    @Description("Prints FloatModifiable log for given type")
    public void respawn(CommandIssuer issuer, Type type, @Optional WarlordsPlayer target) {
        if (target == null) {
            ChatChannels.sendDebugMessage(issuer, "Cannot print FloatModifiable - No target found");
            return;
        }
        ChatChannels.sendDebugMessage(issuer, Component.text(type + " FloatModifiable for ", NamedTextColor.GREEN, TextDecoration.BOLD).append(target.getColoredNameBold()));
        type.sendDebugInfo(issuer, target);
    }


    public enum Type {
        HEALTH { // WarlordEntity health

            @Override
            public void sendDebugInfo(CommandIssuer issuer, WarlordsPlayer player) {
                sendDebugInfo(issuer, player.getHealth().getDebugInfo());
            }
        },
        COOLDOWN { // ability cooldowns

            @Override
            public void sendDebugInfo(CommandIssuer issuer, WarlordsPlayer player) {
                for (AbstractAbility ability : player.getAbilities()) {
                    ChatChannels.sendDebugMessage(issuer, ComponentBuilder.create().text(ability.getName(), NamedTextColor.AQUA, TextDecoration.UNDERLINED).build());
                    sendDebugInfo(issuer, ability.getCooldown().getDebugInfo());
                }
            }
        },
        ENERGY { // ability energy cost

            @Override
            public void sendDebugInfo(CommandIssuer issuer, WarlordsPlayer player) {
                for (AbstractAbility ability : player.getAbilities()) {
                    ChatChannels.sendDebugMessage(issuer, ComponentBuilder.create().text(ability.getName(), NamedTextColor.AQUA, TextDecoration.UNDERLINED).build());
                    sendDebugInfo(issuer, ability.getEnergyCost().getDebugInfo());
                }
            }
        },
        UPGRADE { // upgrades (hitbox, splash, etc)

            @Override
            public void sendDebugInfo(CommandIssuer issuer, WarlordsPlayer player) {
                for (AbstractAbility ability : player.getAbilities()) {
                    ChatChannels.sendDebugMessage(issuer, ComponentBuilder.create().text(ability.getName(), NamedTextColor.AQUA, TextDecoration.UNDERLINED).build());
                    if (ability instanceof HitBox hitBox) {
                        ChatChannels.sendDebugMessage(issuer, ComponentBuilder.create("HitBox", NamedTextColor.LIGHT_PURPLE).build());
                        sendDebugInfo(issuer, hitBox.getHitBoxRadius().getDebugInfo());
                    }
                    if (ability instanceof Splash splash) {
                        ChatChannels.sendDebugMessage(issuer, ComponentBuilder.create("Splash", NamedTextColor.LIGHT_PURPLE).build());
                        sendDebugInfo(issuer, splash.getSplashRadius().getDebugInfo());
                    }
                }
            }
        },

        ;

        public static final Type[] VALUES = values();

        Type() {
        }

        public abstract void sendDebugInfo(CommandIssuer issuer, WarlordsPlayer player);

        protected void sendDebugInfo(CommandIssuer issuer, List<Component> components) {
            for (Component component : components) {
                ChatChannels.sendDebugMessage(issuer, component);
            }
        }

    }

}
