package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
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
import java.util.*;

public class GuardianBeam extends AbstractBeam implements Duration, Damages<GuardianBeam.DamageValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.WARPED_SLAB);
    public Map<Integer, Integer> stacksRemoved = new HashMap<>();
    private final DamageValues damageValues = new DamageValues();
    private final List<Integer> shieldPercents = new ArrayList<>(List.of(5, 10, 20));
    private final float carrierBonusMultiplier = 2.4f;
    private float runeTimerIncrease = 1.5f;
    private int tickDuration = 120;

    public GuardianBeam() {
        super("Guardian Beam", 10, 10, 30, 30, true);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = Component.text("Unleash a concentrated beam of mystical power, piercing all enemies and allies. Enemies hit take ")
                                   .append(Damages.formatDamage(damageValues.beamDamage))
                                   .append(Component.text(" damage and have their cooldowns increased by "))
                                   .append(Component.text(format(runeTimerIncrease), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds. Any ally hit with stacks of Fortifying Hex is granted a shield with "))
                                   .append(Component.text(format(shieldPercents.get(0)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text("/"))
                                   .append(Component.text(format(shieldPercents.get(1)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text("/"))
                                   .append(Component.text(format(shieldPercents.get(2)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text(" of the ally’s maximum health relative to the number of stacks. Shield health on flag carriers is increased by "))
                                   .append(Component.text(format(carrierBonusMultiplier) + "x", NamedTextColor.YELLOW))
                                   .append(Component.text(". Lasts "))
                                   .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds and all stacks are removed.\n\n" +
                                           "If Guardian Beam hits a target, you also receive a shield based on the same percentages."))
                                   .append(Component.text(".\n\nHas a maximum range of "))
                                   .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                                   .append(Component.text(" blocks."));
        } else {
            description = Component.text(
                                           "Unleash a concentrated beam of mystical power, piercing all enemies and allies. Any ally hit with stacks of Fortifying Hex is granted a shield with ")
                                   .append(Component.text(format(shieldPercents.get(0)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text("/"))
                                   .append(Component.text(format(shieldPercents.get(1)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text("/"))
                                   .append(Component.text(format(shieldPercents.get(2)) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text(" of the ally’s maximum health relative to the number of stacks. Shield health on flag carriers is increased by "))
                                   .append(Component.text(format(carrierBonusMultiplier) + "x", NamedTextColor.YELLOW))
                                   .append(Component.text(". Lasts "))
                                   .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds and all stacks are removed.\n\n" +
                                           "If Guardian Beam hits a target, you also receive a shield based on the same percentages."))
                                   .append(Component.text(".\n\nHas a maximum range of "))
                                   .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                                   .append(Component.text(" blocks."));
        }
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        stacksRemoved.entrySet()
                     .stream()
                     .forEach(integerIntegerEntry -> {
                         info.add(new Pair<>("Stacks Removed (" + integerIntegerEntry.getKey() + ")", "" + integerIntegerEntry.getValue()));
                     });
        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GuardianBeamBranch(abilityTree, this);
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
                hit.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.beamDamage)
                );
                if (pveMasterUpgrade2) {
                    hit.addSpeedModifier(wp, "Conservator Beam", -25, 5 * 20);
                }
            } else {
                giveShield(wp, hit);
                hit.addSpeedModifier(wp, "Conservator Beam", 25, 7 * 20);
            }
            if (projectile.getHit().size() == 1) {
                giveShield(wp, wp);
            }
        }
    }

    private void giveShield(WarlordsEntity from, WarlordsEntity to) {
        boolean hasSanctuary = from.getCooldownManager().hasCooldown(Sanctuary.class);
        int selfHexStacks = (int) new CooldownFilter<>(to, RegularCooldown.class)
                .filterCooldownClass(FortifyingHex.class)
                .stream()
                .count();
        if (selfHexStacks <= 0) {
            return;
        }
        if (!hasSanctuary) {
            to.getCooldownManager().removeCooldown(FortifyingHex.class, false);
        } else {
            from.doOnStaticAbility(Sanctuary.class, sanctuary -> sanctuary.hexesNotConsumed += selfHexStacks);
        }
        if (from == to) {
            from.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" is now shielding you!", NamedTextColor.GRAY))
            );
        } else {
            from.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" is now shielding " + to.getName() + "!", NamedTextColor.GRAY))
            );
            to.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + from.getName() + " is shielding you with their ", NamedTextColor.GRAY))
                    .append(Component.text("Guardian Beam", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );
        }
        Utils.playGlobalSound(to.getLocation(), "arcanist.guardianbeam.giveshield", 1, 1.7f);
        stacksRemoved.merge(selfHexStacks, 1, Integer::sum);
        float percent = shieldPercents.get(Math.min(selfHexStacks, 3) - 1) * (to.hasFlag() ? carrierBonusMultiplier : 1);
        GuardianBeamShield shield = new GuardianBeamShield(to.getMaxHealth() * convertToPercent(percent), percent);
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
                        EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001);
                        EffectUtils.displayParticle(Particle.CRIMSON_SPORE, location, 1, 0.3F, 0.3F, 0.3F, 0);
                    }
                })
        ) {
            @Override
            public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getWarlordsEntity().getCooldownManager().queueUpdatePlayerNames();
            }

            @Override
            public PlayerNameData addPrefixFromOther() {
                return new PlayerNameData(
                        Component.text((int) (shield.getShieldHealth()), NamedTextColor.YELLOW),
                        we -> we.isTeammate(from)
                );
            }
        });

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
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        shooter.playSound(shooter.getLocation(), "mage.firebreath.activation", 2, 0.7f);
        return super.onActivate(shooter);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public List<Integer> getShieldPercents() {
        return shieldPercents;
    }

    public float getRuneTimerIncrease() {
        return runeTimerIncrease;
    }

    public void setRuneTimerIncrease(float runeTimerIncrease) {
        this.runeTimerIncrease = runeTimerIncrease;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class GuardianBeamShield extends Shield {
        private final float shieldPercent;

        public GuardianBeamShield(float maxShieldHealth, float shieldPercent) {
            super("Guardian Beam", maxShieldHealth);
            this.shieldPercent = shieldPercent;
        }

        public float getShieldPercent() {
            return shieldPercent;
        }
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable beamDamage = new Value.RangedValueCritable(313, 423, 20, 175);
        private final List<Value> values = List.of(beamDamage);

        public Value.RangedValueCritable getBeamDamage() {
            return beamDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
