package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.MysticalBarrierBranch;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MysticalBarrier extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<MysticalBarrier, MysticalBarrier.MysticalBarrierStats> {

    private final MysticalBarrierStats stats = new MysticalBarrierStats();
    private int tickDuration = 100;
    private float meleeDamageReduction = 80;
    private int radius = 12;
    private int shieldBase = 400;
    private int shieldIncrease = 100;
    private int shieldMaxHealth = 1200;
    private int reactivateTickDuration = 100;
    private int stacksGranted = 2;
    private float guardianBeamShieldMultiplier;

    public MysticalBarrier() {
        super(AbstractAbilityBuilder.create("mysticalBarrier").pvp());
    }

    public MysticalBarrier(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.meleeDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("meleeDamageReduction"), float.class);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.shieldBase = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldBase"), int.class);
        this.shieldIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldIncrease"), int.class);
        this.shieldMaxHealth = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldMaxHealth"), int.class);
        this.reactivateTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("reactivateTickDuration"), int.class);
        this.stacksGranted = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("stacksGranted"), int.class);
        this.guardianBeamShieldMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("guardianBeamShieldMultiplier"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 2, 0.4f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 1);

        if (pveMasterUpgrade2) {
            giveBarrier(wp, wp);
            for (WarlordsEntity target : PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .limit(5)
            ) {
                giveBarrier(wp, target);
            }
        } else {
            List<WarlordsEntity> targets = PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .requireLineOfSightIntervene(wp, true)
                    .lookingAtFirst(wp)
                    .limit(1)
                    .toList();
            WarlordsEntity target = wp.hasFlag() || targets.isEmpty() ? wp : targets.getFirst();
            giveBarrier(wp, target);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Grant the target ally ")
                .text(stacksGranted, NamedTextColor.BLUE)
                .text(" stacks of ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" and the protection of magical spirits that reduce all melee damage taken by")
                .percent(meleeDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(".")
                .emptyLine()
                .text("After ")
                .durationTicks(tickDuration)
                .text(" the spirits transform into a shield equal to")
                .text(shieldBase, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" + ")
                .text(shieldIncrease, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" for each instance of damage taken, up to a maximum of ")
                .text(shieldMaxHealth, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" health, that lasts ")
                .durationTicks(reactivateTickDuration)
                .text(". Players under Mystical Barrier receive ")
                .text(NumberFormat.formatOptionalHundredths(guardianBeamShieldMultiplier) + "x", AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" more shields from Guardian Beam.")
                .emptyLine()
                .text("If you are holding the flag or no ally is targeted, receive the Mystical Barrier yourself.")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MysticalBarrierBranch(abilityTree, this);
    }

    private void giveBarrier(@Nonnull WarlordsEntity wp, WarlordsEntity target) {
        if (wp != target) {
            stats.timesTeammatesShielded++;
            if (target.hasFlag()) {
                stats.timesCarrierShielded++;
            }
        }
        AtomicInteger damageInstances = new AtomicInteger();
        boolean isSelf = wp.equals(target);
        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                .append(Component.text(" Your ", NamedTextColor.GRAY))
                .append(Component.text(name, NamedTextColor.YELLOW))
                .append(Component.text(" is now protecting " + (isSelf ? "yourself" : target.getName()) + "!", NamedTextColor.GRAY)));
        if (!isSelf) {
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), target.getLocation(), 0, 180, 180, 2);
            target.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Mystical Barrier", NamedTextColor.YELLOW))
                    .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY)));
        }
        for (int i = 0; i < stacksGranted; i++) {
            FortifyingHex.giveFortifyingHex(wp, target);
        }
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "MYSTIC",
                MysticalBarrier.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (!target.isAlive()) {
                        return;
                    }
                    Utils.playGlobalSound(target.getLocation(), "arcanist.mysticalbarrier.giveshield", 2, 1.75f);
                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                    giveShield(wp, target, shieldHealth);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 2 != 0) {
                        return;
                    }
                    if (isSelf && pveMasterUpgrade && ticksElapsed % 5 == 0) {
                        for (WarlordsEntity npc : PlayerFilter
                                .entitiesAround(wp, 15, 15, 15)
                        ) {
                            if (npc instanceof WarlordsNPC) {
                                ((WarlordsNPC) npc).getMob().setTarget(wp);
                            }
                        }
                    }
                    EffectUtils.playCircularEffectAround(target.getGame(), target.getLocation(), Particle.TOTEM_OF_UNDYING, 3, 1, 0.15, 2.2, 8, 1, 4, ticksElapsed);
                })
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (!Objects.equals(cooldown.getFrom(), wp) || !(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                            return;
                        }
                        Object cdObject = cooldown.getCooldownObject();
                        if (event.getWarlordsEntity().equals(target) && cdObject instanceof GuardianBeam.GuardianBeamShield guardianBeamShield) {
                            float newShieldHealth = guardianBeamShield.getShieldValue() * guardianBeamShieldMultiplier;
                            guardianBeamShield.setMaxShieldHealth(newShieldHealth);
                            guardianBeamShield.setShieldHealth(newShieldHealth);
                            stats.shieldsIncreased++;
                        }
                    }
                };
            }
        }.addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    if (event.getFlags().contains(InstanceFlags.DOT)) {
                        return;
                    }
                    String cause = event.getCause();
                    if (cause.equals("Hammer of Light") || cause.equals("Sanctuary")) { // TODO
                        return;
                    }
                    damageInstances.getAndIncrement();
                    stats.timesCooldownsIncreased++;
                }
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (event.getCause().isEmpty()) {
                        stats.meleesReduced++;
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, convertToDivisionDecimal(meleeDamageReduction));
                    }
                }
        ));
    }

    private void giveShield(WarlordsEntity from, @Nonnull WarlordsEntity to, int shieldHealth) {
        Shield shield = new Shield(name, shieldHealth);
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
                    if (ticksElapsed % 2 != 0) {
                        return;
                    }
                    EffectUtils.displayParticle(Particle.FIREWORK, to.getLocation().add(0, 1.5, 0), 2, 0.3, 0.2, 0.3, 0);
                })
        ) {

            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(from), NamedTextColor.YELLOW);
            }
        });
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
    public MysticalBarrierStats getAbilityStats() {
        return stats;
    }

    public int getShieldBase() {
        return shieldBase;
    }

    public void setShieldBase(int shieldBase) {
        this.shieldBase = shieldBase;
    }

    public int getShieldMaxHealth() {
        return shieldMaxHealth;
    }

    public void setShieldMaxHealth(int shieldMaxHealth) {
        this.shieldMaxHealth = shieldMaxHealth;
    }

    public int getShieldIncrease() {
        return shieldIncrease;
    }

    public void setShieldIncrease(int shieldIncrease) {
        this.shieldIncrease = shieldIncrease;
    }

    public int getStacksGranted() {
        return stacksGranted;
    }

    public void setStacksGranted(int stacksGranted) {
        this.stacksGranted = stacksGranted;
    }

    public float getMeleeDamageReduction() {
        return meleeDamageReduction;
    }

    public void setMeleeDamageReduction(float meleeDamageReduction) {
        this.meleeDamageReduction = meleeDamageReduction;
    }

    public float getGuardianBeamShieldMultiplier() {
        return guardianBeamShieldMultiplier;
    }

    public void setGuardianBeamShieldMultiplier(float guardianBeamShieldMultiplier) {
        this.guardianBeamShieldMultiplier = guardianBeamShieldMultiplier;
    }

    public static class MysticalBarrierStats extends AbstractAbilityStats<MysticalBarrier, MysticalBarrierStats> {

        @Field("times_teammates_shielded")
        private int timesTeammatesShielded = 0;

        @Field("times_carrier_shielded")
        private int timesCarrierShielded = 0;

        @Field("melees_reduced")
        private int meleesReduced = 0;

        @Field("times_cooldowns_increased")
        private int timesCooldownsIncreased = 0;

        @Field("shields_increased")
        private int shieldsIncreased = 0;

        @Override
        public Class<MysticalBarrierStats> getClazz() {
            return MysticalBarrierStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Melees Reduced", meleesReduced));
            statsDisplay.add(new AbilityStatDisplay("Times Hit", timesCooldownsIncreased));
            statsDisplay.add(new AbilityStatDisplay("Times Teammates Shielded", timesTeammatesShielded));
            statsDisplay.add(new AbilityStatDisplay("Times Carrier Shielded", timesCarrierShielded));
            statsDisplay.add(new AbilityStatDisplay("Shields Increased", shieldsIncreased));
            return statsDisplay;
        }

        @Override
        public MysticalBarrierStats merge(MysticalBarrierStats other, int multiplier) {
            MysticalBarrierStats stats = super.merge(other, multiplier);
            stats.timesTeammatesShielded = this.timesTeammatesShielded + other.timesTeammatesShielded * multiplier;
            stats.timesCarrierShielded = this.timesCarrierShielded + other.timesCarrierShielded * multiplier;
            stats.meleesReduced = this.meleesReduced + other.meleesReduced * multiplier;
            stats.timesCooldownsIncreased = this.timesCooldownsIncreased + other.timesCooldownsIncreased * multiplier;
            stats.shieldsIncreased = this.shieldsIncreased + other.shieldsIncreased * multiplier;
            return stats;
        }

        @Override
        public MysticalBarrierStats create() {
            return new MysticalBarrierStats();
        }

    }

}
