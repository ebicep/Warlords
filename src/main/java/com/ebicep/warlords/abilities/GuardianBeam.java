package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.GuardianBeamBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuardianBeam extends AbstractBeam<GuardianBeam, GuardianBeam.GuardianBeamStats> implements Duration, CanReduceCooldowns, Damages<GuardianBeam.DamageValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.WARPED_SLAB);
    private final GuardianBeamStats stats = new GuardianBeamStats();
    private final DamageValues damageValues = new DamageValues();
    private List<Integer> shieldValues = new ArrayList<>(List.of(5, 10, 20));
    private float carrierBonusMultiplier = 2.4f;
    private float runeTimerIncrease = 1.5f;
    private int tickDuration = 120;

    public GuardianBeam() {
        super(AbstractAbilityBuilder.create("guardianBeam").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        shooter.playSound(shooter.getLocation(), "mage.firebreath.activation", 2, 0.7f);
        giveShield(shooter, shooter);
        return super.onActivateInternal(shooter);
    }

    @Override
    public Pair<Float, Float> getChainAnimationData(int distance) {
        float increment = distance / 3f;
        return new Pair<>(increment * .75f, increment);
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.guardianbeamalt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            if (hit.isEnemy(wp)) {
                if (inPve) {
                    hit.getSpec().increaseAllCooldownTimersBy(runeTimerIncrease);
                }
                hit.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.beamDamage));
                if (pveMasterUpgrade2) {
                    hit.addSpeedModifier(wp, "Conservator Beam", -25, 5 * 20);
                }
            } else {
                giveShield(wp, hit);
                if (pveMasterUpgrade2) {
                    hit.getSpec().decreaseAllCooldownTimersBy(runeTimerIncrease);
                }
            }
        }
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder
                .create("Unleash a concentrated beam of mystical power, piercing all enemies and allies");
        if (inPve) {
            builder.text(". Enemies hit take ")
                   .damage(damageValues.beamDamage)
                   .text(" damage and have their cooldowns increased by ")
                   .durationSeconds(runeTimerIncrease);
        }
        description = builder
                .text(". You and any hit allies with stacks of ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" are granted shields with ")
                .text(shieldValues.get(0), AbilityDescriptionBuilder.COLOR_BROWN)
                .text("/")
                .text(shieldValues.get(1), AbilityDescriptionBuilder.COLOR_BROWN)
                .text("/")
                .text(shieldValues.get(2), AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" health and all stacks are removed. Shield health on flag carriers is increased by ")
                .text(format(carrierBonusMultiplier) + "x", AbilityDescriptionBuilder.COLOR_BROWN)
                .text(". Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .maxRange(maxDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GuardianBeamBranch(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.shieldValues = ConfigManager.getAbilityConfigListValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldValues"), int.class);
        this.carrierBonusMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("carrierBonusMultiplier"), float.class);
        this.runeTimerIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("runeTimerIncrease"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    private void giveShield(WarlordsEntity from, WarlordsEntity to) {
        boolean hasSanctuary = from.getCooldownManager().hasCooldown(Sanctuary.class);
        int selfHexStacks = (int) new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(FortifyingHex.FortifyingHexData.class).stream().count();
        if (selfHexStacks <= 0) {
            return;
        }
        if (!hasSanctuary) {
            to.getCooldownManager().removeCooldown(FortifyingHex.FortifyingHexData.class, false);
        } else {
            from.doOnStaticAbility(Sanctuary.class,
                    sanctuary -> sanctuary.getAbilityStats().setHexesNotConsumed(sanctuary.getAbilityStats().getHexesNotConsumed() + selfHexStacks)
            );
        }
        if (from == to) {
            from.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" is now shielding you!", NamedTextColor.GRAY)));
        } else {
            from.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" is now shielding " + to.getName() + "!", NamedTextColor.GRAY)));
            to.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + from.getName() + " is shielding you with their ", NamedTextColor.GRAY))
                    .append(Component.text("Guardian Beam", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
        }
        Utils.playGlobalSound(to.getLocation(), "arcanist.guardianbeam.giveshield", 1, 1.7f);
        getAbilityStats().getStacksRemoved().merge(selfHexStacks, 1, Integer::sum);
        float value = shieldValues.get(Math.min(selfHexStacks, 3) - 1) * (to.hasFlag() ? carrierBonusMultiplier : 1);
        GuardianBeamShield shield = new GuardianBeamShield(value);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                name + " Shield",
                "SHIELD",
                Shield.class,
                shield,
                from,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = to.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CHERRY_LEAVES, location, 2, 0.15F, 0.3F, 0.15F, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001);
                        EffectUtils.displayParticle(Particle.CRIMSON_SPORE, location, 1, 0.3F, 0.3F, 0.3F, 0);
                    }
                })
        ) {
            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(from), NamedTextColor.YELLOW);
            }
        });
    }

    @Override
    public GuardianBeamStats getAbilityStats() {
        return stats;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public List<Integer> getShieldValues() {
        return shieldValues;
    }

    public float getRuneTimerIncrease() {
        return runeTimerIncrease;
    }

    public void setRuneTimerIncrease(float runeTimerIncrease) {
        this.runeTimerIncrease = runeTimerIncrease;
    }

    public void setShieldValues(List<Integer> shieldValues) {
        this.shieldValues = shieldValues;
    }

    public static class GuardianBeamShield extends Shield {

        private final float shieldValue;

        public GuardianBeamShield(float maxShieldHealth) {
            super("Guardian Beam", maxShieldHealth);
            this.shieldValue = maxShieldHealth;
        }

        public float getShieldValue() {
            return shieldValue;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable beamDamage = new Value.RangedValueCritable(282, 381, 20, 175);

        private List<Value> values = List.of(beamDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.beamDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("beamDamage"), Value.RangedValueCritable.class);
            this.values = List.of(beamDamage);
        }

        public Value.RangedValueCritable getBeamDamage() {
            return beamDamage;
        }

    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    public static class GuardianBeamStats extends AbstractBeamStats<GuardianBeam, GuardianBeamStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public GuardianBeamStats merge(GuardianBeamStats other, int multiplier) {
            GuardianBeamStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<GuardianBeamStats> getClazz() {
            return GuardianBeamStats.class;
        }

        @Override
        public GuardianBeamStats create() {
            return new GuardianBeamStats();
        }

    }

}
