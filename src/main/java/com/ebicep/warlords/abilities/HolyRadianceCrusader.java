package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractHolyRadiance;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.HolyRadianceBranchCrusader;
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

public class HolyRadianceCrusader extends AbstractHolyRadiance implements Heals<HolyRadianceCrusader.HealingValues> {

    private final int markRadius = 15;
    private final HealingValues healingValues = new HealingValues();
    private int markDuration = 8;
    private int energyPerSecond = 6;
    private int markSpeed = 25;

    public HolyRadianceCrusader() {
        super("Holy Radiance", 16.53f, 20, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Radiate with holy energy, healing yourself and all nearby allies for ")
                               .append(Heals.formatHealing(healingValues.radianceHealing))
                               .append(Component.text(" health."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("You may look at an ally to mark them for "))
                               .append(Component.text(markDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Increasing their EPS by "))
                               .append(Component.text(energyPerSecond, NamedTextColor.YELLOW))
                               .append(Component.text(" and speed by "))
                               .append(Component.text(markSpeed + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for the duration.\n\nMark has a maximum range of "))
                               .append(Component.text(markRadius, NamedTextColor.YELLOW))
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
        return new HolyRadianceBranchCrusader(abilityTree, this);
    }

    @Override
    public boolean chain(WarlordsEntity wp) {
        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(wp, markRadius, markRadius, markRadius)
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
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 170, 0, 1);
            EffectUtils.playChainAnimation(wp, markTarget, new ItemStack(Material.PUMPKIN), 20);


            List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
            if (pveMasterUpgrade2) {
                for (AbstractAbility ability : markTarget.getAbilities()) {
                    modifiers.add(ability.getEnergyCost().addAdditiveModifier("Unrivalled Radiance", -10));
                }
            }

            markTarget.addSpeedModifier(wp, "Crusader Mark Speed", markSpeed, 20 * markDuration, "BASE");
            markTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "CRUS MARK",
                    HolyRadianceCrusader.class,
                    new HolyRadianceCrusader(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (pveMasterUpgrade2) {
                            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                        }
                    },
                    markDuration * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 10 == 0) {
                            Location playerLoc = markTarget.getLocation();
                            Location particleLoc = playerLoc.clone();
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 10; j++) {
                                    double angle = j / 8D * Math.PI * 2;
                                    double width = 1;
                                    particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                    particleLoc.setY(playerLoc.getY() + i / 6D);
                                    particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                    particleLoc.getWorld().spawnParticle(
                                            Particle.REDSTONE,
                                            particleLoc,
                                            1,
                                            0,
                                            0,
                                            0,
                                            0,
                                            new Particle.DustOptions(Color.fromRGB(255, 170, 0), 1),
                                            true
                                    );
                                }
                            }
                        }
                    })
            ) {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    return energyGainPerTick + energyPerSecond / 20f;
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Crusader's Mark", NamedTextColor.YELLOW))
                    .append(Component.text(" marked " + markTarget.getName() + "!", NamedTextColor.GRAY))
            );

            markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" You have been granted ", NamedTextColor.GRAY))
                    .append(Component.text("Crusader's Mark", NamedTextColor.YELLOW))
                    .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY))
            );

            return true;
        }

        return false;
    }

    @Override
    public Value.RangedValueCritable getRadianceHealing() {
        return healingValues.radianceHealing;
    }

    public int getMarkDuration() {
        return markDuration;
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public int getMarkSpeed() {
        return markSpeed;
    }

    public void setMarkSpeed(int markSpeed) {
        this.markSpeed = markSpeed;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable radianceHealing = new Value.RangedValueCritable(582, 760, 15, 175);
        private final List<Value> values = List.of(radianceHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}