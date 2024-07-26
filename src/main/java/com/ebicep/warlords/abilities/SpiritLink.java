package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractChain;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.SpiritLinkBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class SpiritLink extends AbstractChain implements RedAbilityIcon, Damages<SpiritLink.DamageValues> {

    public static final ItemStack CHAIN_ITEM = new ItemStack(Material.SPRUCE_FENCE_GATE);
    public int numberOfDismounts = 0;
    private final DamageValues damageValues = new DamageValues();
    private float speedBuff = 40;
    private float speedDuration = 1.5f;
    private float damageReduction = 15;
    private float damageReductionDuration = 4.5f;
    private float damageDecreasePerBounce = 20;

    public SpiritLink() {
        super("Spirit Link", 8.5f, 40, 20, 10, 2);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Links your spirit with up to ")
                .text(additionalBounces + 1, NamedTextColor.YELLOW)
                .text(" enemy players, dealing ")
                .damage(damageValues.linkDamage)
                .text(" damage to the first target hit. Each additional hit deals ")
                .percent(damageDecreasePerBounce, NamedTextColor.RED)
                .text(" reduced damage. You gain ")
                .percent(speedBuff, NamedTextColor.YELLOW)
                .text(" speed for ")
                .durationSeconds(speedDuration)
                .text(", and take ")
                .percent(damageReduction, NamedTextColor.YELLOW)
                .text(" reduced damage for ")
                .durationSeconds(damageReductionDuration)
                .text(".")
                .initialRange(radius)
                .build();
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SpiritLinkBranch(abilityTree, this);
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp) {
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        for (WarlordsEntity nearPlayer : PlayerFilter
                .entitiesAround(wp, radius, radius - 2, radius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .soulBindedFirst(wp)
        ) {
            if (LocationUtils.isLookingAtChain(wp, nearPlayer) && LocationUtils.hasLineOfSight(wp, nearPlayer)) {
                playersHit++;
                if (nearPlayer.onHorse()) {
                    numberOfDismounts++;
                }
                chain(wp.getLocation(), nearPlayer.getLocation());
                nearPlayer.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.linkDamage)
                );
                hitCounter.add(nearPlayer);

                List<Soulbinding> soulbindings = wp.getCooldownManager().getNumberOfBoundPlayersLink(nearPlayer);
                for (Soulbinding information : soulbindings) {
                    healNearPlayers(wp, nearPlayer, information);
                }

                additionalBounce(wp, hitCounter, nearPlayer, new ArrayList<>(Arrays.asList(wp, nearPlayer)), pveMasterUpgrade2 && !soulbindings.isEmpty() ? -1 : 0);

                if (pveMasterUpgrade2 && nearPlayer instanceof WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().setTarget(wp);
                    EffectUtils.displayParticle(
                            Particle.SPELL_INSTANT,
                            warlordsNPC.getLocation().add(0, 1.2, 0),
                            5,
                            .25,
                            .25,
                            .25,
                            0
                    );
                }

                break;
            }
        }
        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity we, int hitCounter) {
        we.playSound(we.getLocation(), "mage.firebreath.activation", 1, 1);
        we.getCooldownManager().limitCooldowns(RegularCooldown.class, SpiritLink.class, inPve ? 4 : 1);
        // speed buff
        we.addSpeedModifier(we, "Spirit Link", speedBuff, (int) (speedDuration * 20)); // 30 is ticks
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LINK",
                SpiritLink.class,
                new SpiritLink(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                (int) (damageReductionDuration * 20)
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - damageReduction / 100f);
            }
        });
    }

    @Override
    protected ItemStack getChainItem() {
        return CHAIN_ITEM;
    }

    private void additionalBounce(WarlordsEntity wp, Set<WarlordsEntity> hitCounter, WarlordsEntity chainTarget, List<WarlordsEntity> toExclude, int bounceCount) {
        float bounceDamageReduction = Math.max(0, 1 - (bounceCount + 1) * .2f);
        if (bounceCount >= additionalBounces || bounceDamageReduction == 0) {
            return;
        }
        for (WarlordsEntity bounceTarget : PlayerFilter
                .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                .aliveEnemiesOf(wp)
                .excluding(toExclude)
                .soulBindedFirst(wp)
        ) {
            playersHit++;
            if (bounceTarget.onHorse()) {
                numberOfDismounts++;
            }
            chain(chainTarget.getLocation(), bounceTarget.getLocation());
            bounceTarget.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(damageValues.linkDamage.getMinValue() * bounceDamageReduction)
                    .max(damageValues.linkDamage.getMaxValue() * bounceDamageReduction)
                    .crit(damageValues.linkDamage)
            );

            hitCounter.add(bounceTarget);

            List<Soulbinding> soulbindings = wp.getCooldownManager().getNumberOfBoundPlayersLink(bounceTarget);
            for (Soulbinding information : soulbindings) {
                healNearPlayers(wp, bounceTarget, information);
            }

            toExclude.add(bounceTarget);
            additionalBounce(wp, hitCounter, bounceTarget, toExclude, bounceCount + (pveMasterUpgrade2 && !soulbindings.isEmpty() ? 0 : 1));

            if (pveMasterUpgrade2 && bounceTarget instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().setTarget(wp);
                EffectUtils.displayParticle(
                        Particle.SPELL_INSTANT,
                        warlordsNPC.getLocation().add(0, 1.2, 0),
                        5,
                        .25,
                        .25,
                        .25,
                        0
                );
            }

            break;
        }
    }

    private void healNearPlayers(WarlordsEntity warlordsPlayer, WarlordsEntity hitPlayer, Soulbinding soulbinding) {
        float radius = soulbinding.getRadius();
        int limit = soulbinding.getMaxAlliesHit();
        Soulbinding.HealingValues healValues = soulbinding.getHealValues();
        warlordsPlayer.addInstance(InstanceBuilder
                .healing()
                .ability(soulbinding)
                .source(warlordsPlayer)
                .value(healValues.getSelfHealing())
        );
        for (WarlordsEntity nearPlayer : PlayerFilter
                .entitiesAround(warlordsPlayer, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .closestWarlordPlayersFirst(warlordsPlayer.getLocation())
                .limit(limit)
        ) {
            warlordsPlayer.doOnStaticAbility(Soulbinding.class, Soulbinding::addLinkTeammatesHealed);
            nearPlayer.addInstance(InstanceBuilder
                    .healing()
                    .ability(soulbinding)
                    .source(warlordsPlayer)
                    .value(healValues.getAllyHealing())
            );
        }
        new CooldownFilter<>(warlordsPlayer, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .filter(binding -> binding.hasBoundPlayerSoul(hitPlayer))
                .forEach(binding -> {
                    if (binding.isPveMasterUpgrade()) {
                        warlordsPlayer.addEnergy(warlordsPlayer, "Soulbinding Weapon", 1);
                    }
                });
    }

    public float getSpeedDuration() {
        return speedDuration;
    }

    public void setSpeedDuration(float speedDuration) {
        this.speedDuration = speedDuration;
    }

    public float getDamageReductionDuration() {
        return damageReductionDuration;
    }

    public void setDamageReductionDuration(float damageReductionDuration) {
        this.damageReductionDuration = damageReductionDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable linkDamage = new Value.RangedValueCritable(290, 392, 20, 175);
        private final List<Value> values = List.of(linkDamage);

        public Value.RangedValueCritable getLinkDamage() {
            return linkDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
