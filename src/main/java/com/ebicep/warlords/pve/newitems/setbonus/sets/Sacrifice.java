package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;

import java.util.List;

public class Sacrifice extends BaseSet {

    private int selfReviveHealthPercent;
    private int allyHealthReductionPercent;
    private float reviveCooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.selfReviveHealthPercent = getValue("selfReviveHealthPercent", int.class);
        this.allyHealthReductionPercent = getValue("allyHealthReductionPercent", int.class);
        this.reviveCooldownSeconds = getValue("reviveCooldownSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sacrifice";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(selfReviveHealthPercent, allyHealthReductionPercent, reviveCooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private int cooldownTicks;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Sacrifice.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticks) -> {
                        if (cooldownTicks > 0) {
                            cooldownTicks--;
                        }
                    }
            ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS, (event, currentDamageValue, isCrit) -> {
                if (cooldownTicks > 0 ||
                        warlordsPlayer.getCurrentHealth() - currentDamageValue.getCalculatedValue() > 0
                ) {
                    return;
                }
                WarlordsEntity ally = PlayerFilter
                        .entitiesAround(warlordsPlayer, 100, 100, 100)
                        .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                        .closestFirst(warlordsPlayer)
                        .findFirstOrNull();
                if (ally == null) {
                    return;
                }
                currentDamageValue.addModifier(FloatModifiable.ModifierType.OVERRIDING, getName(), 0);
                warlordsPlayer.setCurrentHealth(warlordsPlayer.getMaxHealth() * selfReviveHealthPercent / 100f);
                ally.setCurrentHealth(Math.max(
                        1,
                        ally.getCurrentHealth() * (1 - allyHealthReductionPercent / 100f)
                ));
                cooldownTicks = Math.round(reviveCooldownSeconds * 20);
                warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ITEM_TOTEM_USE, 1, .5f);
                ally.playSound(ally.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, .5f);
                warlordsPlayer.sendMessage(Component.text(
                        "You sacrificed " + ally.getName() + " to the unholy gods!",
                        NamedTextColor.RED
                ));
            }));
        }

    }

}
