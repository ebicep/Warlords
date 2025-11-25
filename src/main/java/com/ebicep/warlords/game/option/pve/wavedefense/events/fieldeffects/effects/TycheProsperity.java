package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;
import java.util.function.Consumer;

public class TycheProsperity implements FieldEffect {

    private final Map<Classes, ClassBonus> classBonuses = new LinkedHashMap<>() {{
        put(Classes.MAGE, new ClassBonus(Component.text("+5 EPS / +10% Projectile Speed", NamedTextColor.GRAY), TycheProsperity.this::mageBonus));
        put(Classes.WARRIOR, new ClassBonus(Component.text("+5% Damage / -5% Knockback", NamedTextColor.GRAY), TycheProsperity.this::warriorBonus));
        put(Classes.PALADIN, new ClassBonus(Component.text("+5 EPS / +5% Speed", NamedTextColor.GRAY), TycheProsperity.this::paladinBonus));
        put(Classes.SHAMAN, new ClassBonus(Component.text("+5% Health / +10% Melee Damage", NamedTextColor.GRAY), TycheProsperity.this::shamanBonus));
        put(Classes.ROGUE, new ClassBonus(Component.text("+10% Cooldown Reduction / +5% Healing", NamedTextColor.GRAY), TycheProsperity.this::rogueBonus));
        put(Classes.ARCANIST, new ClassBonus(Component.text("+10% Crit Chance / -5% Damage Taken", NamedTextColor.GRAY), TycheProsperity.this::arcanistBonus));
    }};

    private record ClassBonus(Component component, Consumer<WarlordsEntity> consumer) {
    }

    @Override
    public String getName() {
        return "Tyche Prosperity";
    }

    @Override
    public String getDescription() {
        return "When two specs of the same class are present in the game, the following buffs are applied to all players:";
    }

    @Override
    public List<Component> getSubDescription() {
        return new ArrayList<>() {{
            add(Component.empty());
            addAll(classBonuses.values().stream().map(ClassBonus::component).toList());
        }};
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        if (players.isEmpty()) {
            return;
        }
        Game game = players.get(0).getGame();
        if (game == null) {
            return;
        }
        Map<Classes, Integer> classCounts = new HashMap<>();
        players.forEach(p -> classCounts.merge(Specializations.getClass(p.getSpecClass()), 1, Integer::sum));
        classCounts.forEach((classes, integer) -> {
            if (integer < 2) {
                return;
            }
            ChatUtils.MessageType.GAME.sendMessage(getName() + ": Applied " + classes.name + " Bonus");
            game.onlinePlayers().forEach(playerTeamEntry -> playerTeamEntry.getKey().sendMessage(
                    Component.textOfChildren(
                            Component.text("Tyche Prosperity Bonus", NamedTextColor.WHITE, TextDecoration.BOLD),
                            Component.text(" > ", NamedTextColor.DARK_GRAY),
                            classBonuses.get(classes).component
                    )
            ));
            players.forEach(classBonuses.get(classes).consumer);
            switch (classes) {
                case MAGE -> players.forEach(this::mageBonus);
                case PALADIN -> players.forEach(this::paladinBonus);
                case WARRIOR -> players.forEach(this::warriorBonus);
                case SHAMAN -> players.forEach(this::shamanBonus);
                case ROGUE -> players.forEach(this::rogueBonus);
                case ARCANIST -> players.forEach(this::arcanistBonus);
            }
        });
    }

    private void mageBonus(WarlordsEntity warlordsEntity) {
        warlordsEntity.getEnergyPerSec().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 5);
        warlordsEntity.getAbilitiesMatching(AbstractPiercingProjectile.class)
                      .forEach(proj -> proj.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE,
                              getName(), .1f
                      ));
    }

    private void paladinBonus(WarlordsEntity warlordsEntity) {
        warlordsEntity.getEnergyPerHit().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 5);
        warlordsEntity.getSpeed().addBaseModifier(5);
    }

    private void warriorBonus(WarlordsEntity warlordsEntity) {
        PermanentCooldown<FieldEffectOption> warriorCooldown = new PermanentCooldown<>(
                getName(),
                null,
                FieldEffectOption.class,
                null,
                warlordsEntity,
                CooldownTypes.FIELD_EFFECT,
                cooldownManager -> {
                },
                false
        );
        warriorCooldown.addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getName(), 1.05f);
                }
        );
        warlordsEntity.getCooldownManager().addCooldown(warriorCooldown);
        warlordsEntity.addKnockbackModifier(warlordsEntity, getName(), -5, warriorCooldown);
    }

    private void shamanBonus(WarlordsEntity warlordsEntity) {
        warlordsEntity.getHealth().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, getName() + " (Base)", .05f);
        warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                FieldEffectOption.class,
                null,
                warlordsEntity,
                CooldownTypes.FIELD_EFFECT,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    if (event.getCause().isEmpty()) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getName(), 1.1f);
                    }
                }
        ));
    }

    private void rogueBonus(WarlordsEntity warlordsEntity) {
        warlordsEntity.getAbilities().forEach(ability -> ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE,
                getName(), .9f
        ));
        warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                FieldEffectOption.class,
                null,
                warlordsEntity,
                CooldownTypes.FIELD_EFFECT,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getName(), 1.05f);
                }
        ));
    }

    private void arcanistBonus(WarlordsEntity warlordsEntity) {
        warlordsEntity.getAbilities().forEach(ability -> {
            Value.applyDamageHealing(ability, value -> {
                        if (value instanceof Value.RangedValueCritable rangedValueCritable) {
                            rangedValueCritable.critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 10);
                        }
                    }
            );
        });
        warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                FieldEffectOption.class,
                null,
                warlordsEntity,
                CooldownTypes.FIELD_EFFECT,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getName(), .95f);
                }
        ));
    }

}
