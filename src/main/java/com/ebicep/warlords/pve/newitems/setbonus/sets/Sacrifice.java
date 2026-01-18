package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.LegendaryDivine;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.LegendaryStalwart;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(
                    new PermanentCooldown<>(
                            getName(),
                            null,
                            Sacrifice.class,
                            null,
                            warlordsPlayer,
                            CooldownTypes.WEAPON,
                            cooldownManager -> {

                            },
                            false,
                            (cooldown, ticksElapsed) -> {
                                reviveCooldownSeconds--;
                            }
                    ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS, (event, currentDamageValue, isCrit) -> {
                                if (reviveCooldownSeconds > 0) {
                                    return;
                                }
                                if (warlordsPlayer.getCurrentHealth() - currentDamageValue.getCalculatedValue() > 0) {
                                    return;
                                }
                                warlordsPlayer.setCurrentHealth(warlordsPlayer.getMaxBaseHealth() * .2f);
                                warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ITEM_TOTEM_USE, 1, 0.5f);
                                currentDamageValue.addModifier(FloatModifiable.ModifierType.OVERRIDING, "Sacrifice", 0);
                                WarlordsEntity ally = PlayerFilter.entitiesAround(warlordsPlayer, 100, 100, 100)
                                        .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                        .findFirstOrNull();
                                if (ally == null) {
                                    return;
                                }
                                ally.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Sacrifice")
                                        .source(warlordsPlayer)
                                        .value(warlordsPlayer.getCurrentHealth() * allyHealthReductionPercent / 100f)
                                        .flags(InstanceFlags.TRUE_DAMAGE)
                                );
                                ally.playSound(ally.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
                                warlordsPlayer.sendMessage(Component.text("You have sacrificed " + ally.getName() + " to the unholy gods!", NamedTextColor.RED));
                                reviveCooldownSeconds = 30 * 20;
                    }));
        }

    }

}