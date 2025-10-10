package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.NoTargetAbilities;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.HolyRadianceBranchProtector;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class HolyRadianceProtector extends AbstractHolyRadiance implements Heals<HolyRadianceProtector.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable markRadius = new FloatModifiable(15);
    private int markDuration = 8;
    private float markBonusHealing = 10;

    public HolyRadianceProtector() {
        super(AbstractAbilityBuilder.create("holyRadianceProtector").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.markRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("markRadius"), float.class));
        this.markDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("markDuration"), int.class);
        this.markBonusHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("markBonusHealing"), float.class);
    }

    @Override
    public Value.RangedValueCritable getRadianceHealing() {
        return healingValues.radianceHealing;
    }

    @Override
    public List<WarlordsEntity> chain(WarlordsEntity wp) {
        float radius = markRadius.getCalculatedValue();
        if (pveMasterUpgrade) {
            List<WarlordsEntity> targets = PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveTeammatesOfExcludingSelf(wp).toList();
            for (WarlordsEntity circleTarget : targets) {
                emitMarkRadiance(wp, circleTarget);
            }
            return targets;
        }
        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .excludingAlliedMobs()
                .limit(1)
        ) {
            if (!LocationUtils.isLookingAtMark(wp, markTarget) || !LocationUtils.hasLineOfSight(wp, markTarget)) {
                wp.sendMessage(Component.text("Your mark was out of range or you did not target a player!", NamedTextColor.RED));
                continue;
            }
            Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
            // chain particles
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 0, 255, 70, 1);
            EffectUtils.playChainAnimation(wp.getLocation(), markTarget.getLocation(), new ItemStack(Material.POPPY), 8);
            emitMarkRadiance(wp, markTarget);
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                          .append(Component.text("Protector's Mark", NamedTextColor.YELLOW))
                                                          .append(Component.text(" marked " + markTarget.getName() + "!", NamedTextColor.GRAY)));
            markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.text(" You have been granted ", NamedTextColor.GRAY))
                                                                     .append(Component.text("Protector's Mark", NamedTextColor.YELLOW))
                                                                     .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY)));
            return List.of(markTarget);
        }
        return Collections.emptyList();
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Radiate with holy energy, healing yourself and all nearby allies for ")
                .heal(healingValues.radianceHealing)
                .text(" health.")
                .emptyLine()
                .text("You may look at an ally to grant them with ")
                .text("MARK", NamedTextColor.DARK_GREEN)
                .text(" for ")
                .durationSeconds(markDuration)
                .text(". Marked allies receive ")
                .percent(markBonusHealing, NamedTextColor.GREEN)
                .text(" more healing from all sources.")
                .maxRange(markRadius)
                .build();
    }

    private void emitMarkRadiance(WarlordsEntity giver, WarlordsEntity target) {
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "PROT MARK",
                HolyRadianceProtector.class,
                new HolyRadianceProtector(),
                giver,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                markDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 16 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1, 0, 255, 70, 8, 3, .3);
                    }
                    if (pveMasterUpgrade2) {
                        if (ticksElapsed % 20 == 0 && ticksElapsed != 0) {
                            PlayerFilter.entitiesAround(target, 10, 10, 10).aliveTeammatesOf(giver).forEach(warlordsEntity -> {
                                warlordsEntity.addInstance(InstanceBuilder.healing().ability(this).source(giver).value(healingValues.unrivalledRadianceHealing));
                            });
                        }
                    }
                })
        ) {

            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * convertToMultiplicationDecimal(markBonusHealing);
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HolyRadianceBranchProtector(abilityTree, this);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public FloatModifiable getMarkRadius() {
        return markRadius;
    }

    public float getMarkBonusHealing() {
        return markBonusHealing;
    }

    public void setMarkBonusHealing(float markBonusHealing) {
        this.markBonusHealing = markBonusHealing;
    }

    public int getMarkDuration() {
        return markDuration;
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable radianceHealing = new Value.RangedValueCritable(582, 760, 15, 175);

        private Value.RangedValue unrivalledRadianceHealing = new Value.RangedValue(150, 350);

        private List<Value> values = List.of(radianceHealing, unrivalledRadianceHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.radianceHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("radianceHealing"),
                    Value.RangedValueCritable.class
            );
            this.unrivalledRadianceHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("unrivalledRadianceHealing"),
                    Value.RangedValue.class
            );
            this.values = List.of(radianceHealing, unrivalledRadianceHealing);
        }

        public Value.RangedValueCritable getRadianceHealing() {
            return radianceHealing;
        }

    }

}
