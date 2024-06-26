package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.pve.mobs.bosses.Enavuris;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.List;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

@CommandAlias("ability")
@CommandPermission("group.administrator")
public class AbilityCommand extends BaseCommand {

    @Subcommand("forceactivate")
    @CommandCompletion("@warlordsplayers")
    @Description("Makes player activate their ability")
    public void respawn(CommandIssuer issuer, @Conditions("limits:min=0,max=4") Integer ability, @Optional WarlordsPlayer target) {
        if (target.getEntity() instanceof Player) {
            target.getSpec().onRightClick(target, (Player) target.getEntity(), ability, true);
        }
    }

    @Subcommand("test")
    public void test(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getSpec().getAbilities().clear();
        warlordsPlayer.getSpec().getAbilities().add(new Enavuris.EnderStones());
        warlordsPlayer.getSpec().getAbilities().add(new Enavuris.Imprisonment());
        warlordsPlayer.updateInventory(false);
    }

    @Subcommand("useall")
    public void useAll(WarlordsPlayer warlordsPlayer) {
        for (Ability value : Ability.VALUES) {
            AbstractAbility ability = value.create.get();
            ability.onActivate(warlordsPlayer);
        }
    }

    @Subcommand("cooldown")
    public class CooldownCommand extends BaseCommand {

        @Subcommand("printlisteners")
        public void printListeners(Player player) {
            List<AbstractCooldown<?>> listeners = AbstractCooldown.COOLDOWNS_WITH_LISTENERS;
            if (listeners.isEmpty()) {
                sendDebugMessage(player, Component.text("No listeners found", NamedTextColor.RED));
                return;
            }
            for (AbstractCooldown<?> listener : listeners) {
                sendDebugMessage(player, Component.text("Cooldown: " + listener.getName() + " - " + listener + " - " + listener.getCooldownObject(), NamedTextColor.GREEN));
            }
        }

        @Subcommand("forceunregisterall")
        public void forceUnregisterAll(Player player) {
            for (AbstractCooldown<?> listener : AbstractCooldown.COOLDOWNS_WITH_LISTENERS) {
                HandlerList.unregisterAll(listener.getActiveListener());
                sendDebugMessage(player,
                        Component.text("Unregistered Cooldown: " + listener.getName() + " - " + listener + " - " + listener.getCooldownObject(), NamedTextColor.GOLD)
                );
            }
        }

        @Subcommand("forceunregisterfirst")
        public void forceUnregisterFirst(Player player) {
            if (!AbstractCooldown.COOLDOWNS_WITH_LISTENERS.isEmpty()) {
                AbstractCooldown<?> listener = AbstractCooldown.COOLDOWNS_WITH_LISTENERS.get(0);
                HandlerList.unregisterAll(listener.getActiveListener());
                sendDebugMessage(player,
                        Component.text("Unregistered Cooldown: " + listener.getName() + " - " + listener + " - " + listener.getCooldownObject(), NamedTextColor.GOLD)
                );
            }
        }

    }


}
