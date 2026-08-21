package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ghostly extends BaseSet {

    private int dodgeChance;
    private int maxStacks;
    private float bonusPerStackPercent;
    private float stackDurationSeconds;

    @Override
    public void init() {
        super.init();
        this.dodgeChance = getValue("dodgeChance", int.class);
        this.bonusPerStackPercent = getValue("bonusPerStackPercent", float.class);
        this.stackDurationSeconds = getValue("stackDurationSeconds", float.class);
        this.maxStacks = getValue("maxStacks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ghostly";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(dodgeChance, bonusPerStackPercent, stackDurationSeconds, maxStacks);
    }

    public class Bonus implements SetBonus.Bonus {

        private final Deque<Integer> expirations = new ArrayDeque<>();
        private int elapsed;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Ghostly.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticks) -> {
                        elapsed++;
                        while (!expirations.isEmpty() && expirations.peekFirst() <= elapsed) {
                            expirations.removeFirst();
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> applyStacks(currentDamageValue)
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealValue) -> applyStacks(currentHealValue)
            ));
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onDamage(WarlordsDamageHealingEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer) ||
                            event.isHealingInstance() ||
                            ThreadLocalRandom.current().nextDouble() >= dodgeChance / 100.0
                    ) {
                        return;
                    }
                    if (expirations.size() >= maxStacks) {
                        expirations.removeFirst();
                    }
                    expirations.addLast(elapsed + Math.round(stackDurationSeconds * 20));
                    warlordsPlayer.sendMessage(Component
                            .text("Your " + getName() + " dodged ", NamedTextColor.GREEN)
                            .append(event.getSource().getColoredName())
                            .append(Component.text("'s attack."))
                    );
                    event.setCancelled(true);
                }

            });
        }

        private void applyStacks(MultiFloatModifiable value) {
            if (!expirations.isEmpty()) {
                value.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName(),
                        1 + expirations.size() * bonusPerStackPercent / 100f
                );
            }
        }

    }

}
