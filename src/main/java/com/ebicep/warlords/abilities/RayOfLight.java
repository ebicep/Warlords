package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.RayOfLightBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RayOfLight extends AbstractBeam<RayOfLight, RayOfLight.RayOfLightStats> implements Heals<RayOfLight.HealingValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.WITHER_ROSE);
    private final RayOfLightStats stats = new RayOfLightStats();
    private final HealingValues healingValues = new HealingValues();
    private boolean removeDebuffs = true;

    public RayOfLight() {
        super(AbstractAbilityBuilder.create("rayOfLight").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        beamPlayer(shooter, shooter);
        Utils.playGlobalSound(shooter.getLocation(), "arcanist.rayoflightalt.activation", 2, 0.9f);
        return super.onActivateInternal(shooter);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Unleash a concentrated beam of holy light, healing ")
                                               .heal(healingValues.rayHealing)
                                               .text(" health to all allies hit and cleansing all ")
                                               .text("de-buffs", NamedTextColor.DARK_RED)
                                               .text(" from allies with max stacks of ")
                                               .text("MHEX", NamedTextColor.DARK_GREEN)
                                               .text(". If the target is affected by ")
                                               .text("MHEX", NamedTextColor.DARK_GREEN)
                                               .text(" the healing given is increased by ")
                                               .percent(25, NamedTextColor.GREEN)
                                               .text("/")
                                               .percent(50, NamedTextColor.GREEN)
                                               .text("/")
                                               .percent(100, NamedTextColor.GREEN)
                                               .text(" relative to the number of stacks and all stacks are removed.")
                                               .maxRange(maxDistance)
                                               .build();
    }

    @Override
    public RayOfLightStats getAbilityStats() {
        return stats;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RayOfLightBranch(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.energyseer.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1.1f;
    }

    @Override
    protected void playEffect(@Nonnull InternalProjectile projectile) {
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (hit.isTeammate(wp) && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            beamPlayer(hit, wp);
        }
    }

    public void setRemoveDebuffs(boolean removeDebuffs) {
        this.removeDebuffs = removeDebuffs;
    }

    private void beamPlayer(@Nonnull WarlordsEntity hit, WarlordsEntity wp) {
        int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class).filterCooldownClass(MercifulHex.class).stream().count();
        boolean hasDivineBlessing = wp.getCooldownManager().hasCooldown(DivineBlessing.DivineBlessingData.class);
        if (!hasDivineBlessing) {
            hit.getCooldownManager().removeCooldown(MercifulHex.class, false);
        } else {
            wp.doOnStaticAbility(DivineBlessing.class,
                    divineBlessing -> divineBlessing.getAbilityStats().setHexesNotConsumed(divineBlessing.getAbilityStats().getHexesNotConsumed() + hexStacks)
            );
        }
        boolean maxStacks = hexStacks >= 3;
        if (maxStacks && removeDebuffs) {
            hit.getCooldownManager().removeDebuffCooldowns();
        }
        float multiplier = switch (hexStacks) {
            case 0 -> 1f;
            case 1 -> 1.25f;
            case 2 -> 1.5f;
            default -> 2f;
        };
        getAbilityStats().getStacksRemoved().merge(hexStacks, 1, Integer::sum);
        if (pveMasterUpgrade) {
            hit.getCooldownManager().addCooldown(new RegularCooldown<>(name, "RAY", RayOfLight.class, new RayOfLight(), wp, CooldownTypes.ABILITY, cooldownManager -> {
            }, cooldownManager -> {
            }, 100
            ) {

                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (maxStacks ? 1.15f : 1.05f);
                }
            });
        }
        hit.addInstance(InstanceBuilder.healing()
                                       .ability(this)
                                       .source(wp)
                                       .min(healingValues.rayHealing.getMinValue() * multiplier)
                                       .max(healingValues.rayHealing.getMaxValue() * multiplier)
                                       .crit(healingValues.rayHealing));
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable rayHealing = new Value.RangedValueCritable(389, 523, 20, 150);

        private List<Value> values = List.of(rayHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.rayHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("rayHealing"), Value.RangedValueCritable.class);
            this.values = List.of(rayHealing);
        }

        public Value.RangedValueCritable getRayHealing() {
            return rayHealing;
        }

    }

    public static class RayOfLightStats extends AbstractBeamStats<RayOfLight, RayOfLightStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public RayOfLightStats merge(RayOfLightStats other, int multiplier) {
            RayOfLightStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<RayOfLightStats> getClazz() {
            return RayOfLightStats.class;
        }

        @Override
        public RayOfLightStats create() {
            return new RayOfLightStats();
        }

    }

}
