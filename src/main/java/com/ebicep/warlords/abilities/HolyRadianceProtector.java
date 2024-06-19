package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractHolyRadiance;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.HolyRadianceBranchProtector;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HolyRadianceProtector extends AbstractHolyRadiance implements Heals<HolyRadianceProtector.HealingValues> {

    private final FloatModifiable markRadius = new FloatModifiable(15);
    private final HealingValues healingValues = new HealingValues();
    private int markDuration = 8;
    private float markBonusHealing = 10;

    public HolyRadianceProtector(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    public HolyRadianceProtector() {
        super("Holy Radiance", 582, 760, 9.87f, 60, 15, 175, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Radiate with holy energy, healing yourself and all nearby allies for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health."))
                               .append(Component.text("\n\nYou may look at an ally to mark them for "))
                               .append(Component.text(markDuration, NamedTextColor.GOLD))
                               .append(Component.text("seconds. Marked allies receive "))
                               .append(Component.text(format(markBonusHealing) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" more healing from all sources."))
                               .append(Component.text("\n\nMark has an optimal range of "))
                               .append(Component.text(format(markRadius.getCalculatedValue()), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Marked", "" + playersMarked));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HolyRadianceBranchProtector(abilityTree, this);
    }

    @Override
    public boolean chain(WarlordsEntity wp) {
        float radius = markRadius.getCalculatedValue();

        if (pveMasterUpgrade) {
            for (WarlordsEntity circleTarget : PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                emitMarkRadiance(wp, circleTarget);
            }

            return true;
        }

        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
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

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Protector's Mark", NamedTextColor.YELLOW))
                    .append(Component.text(" marked " + markTarget.getName() + "!", NamedTextColor.GRAY))
            );

            markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" You have been granted ", NamedTextColor.GRAY))
                    .append(Component.text("Protector's Mark", NamedTextColor.GREEN))
                    .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY))
            );

            return true;
        }

        return false;
    }

    private void emitMarkRadiance(WarlordsEntity giver, WarlordsEntity target) {
        HolyRadianceProtector tempMark = new HolyRadianceProtector(
                minDamageHeal.getCalculatedValue(),
                maxDamageHeal.getCalculatedValue(),
                cooldown.getBaseValue(),
                energyCost.getBaseValue(),
                critChance,
                critMultiplier
        );
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "PROT MARK",
                HolyRadianceProtector.class,
                tempMark,
                giver,
                CooldownTypes.BUFF,
                cooldownManager -> {

                },
                markDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        Location playerLoc = target.getLocation();
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 9D * Math.PI * 2;
                                double width = 1;
                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(playerLoc.getY() + i / 6D);
                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                EffectUtils.displayParticle(
                                        Particle.REDSTONE,
                                        particleLoc,
                                        1,
                                        new Particle.DustOptions(Color.fromRGB(0, 255, 70), 1)
                                );
                            }
                        }
                    }
                    if (pveMasterUpgrade2) {
                        if (ticksElapsed % 20 == 0 && ticksElapsed != 0) {
                            PlayerFilter.entitiesAround(target, 10, 10, 10)
                                        .aliveTeammatesOf(giver)
                                        .forEach(warlordsEntity -> {
                                            warlordsEntity.addInstance(InstanceBuilder
                                                    .healing()
                                                    .ability(this)
                                                    .source(giver)
                                                    .value(healingValues.unrivalledRadianceHealing)
                                            );
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
    public Value.RangedValueCritable getRadianceHealing() {
        return healingValues.radianceHealing;
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

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable radianceHealing = new Value.RangedValueCritable(582, 760, 15, 175);
        private final Value.RangedValue unrivalledRadianceHealing = new Value.RangedValue(150, 350);
        private final List<Value> values = List.of(radianceHealing, unrivalledRadianceHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}