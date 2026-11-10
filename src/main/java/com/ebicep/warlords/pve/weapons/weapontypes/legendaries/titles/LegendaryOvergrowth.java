package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.*;

public class LegendaryOvergrowth extends AbstractLegendaryWeapon implements LibraryArchivesTitle {

    public static final int LINK_RANGE = 24;
    public static final int RELINK_COOLDOWN_SECONDS = 40;

    public static final float DAMAGE_BONUS_PER_150_SHIELD = 5f;
    public static final float DAMAGE_BONUS_PER_150_SHIELD_PER_UPGRADE = 1f;

    public static final float SHIELD_FROM_DAMAGE_PERCENT = 0.75f;
    public static final float SHIELD_FROM_DAMAGE_PERCENT_PER_UPGRADE = .25f;

    public static final int SHIELD_HEALTH_INTERVAL = 150;
    public static final int MAX_SHIELD_PERCENT_OF_LINKED_ALLY_HEALTH = 15;

    @Transient
    private LegendaryOvergrowthAbility ability;
    @Transient
    private WarlordsEntity linkedAlly;
    @Transient
    private OvergrowthLinkData linkData;

    public LegendaryOvergrowth() {
    }

    public LegendaryOvergrowth(UUID uuid) {
        super(uuid);
    }

    public LegendaryOvergrowth(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void cleanup() {
        breakLink(false);
        super.cleanup();
        ability = null;
        linkedAlly = null;
        linkData = null;
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Activate to link to one ally within " + LINK_RANGE + " blocks. While linked, they deal ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getDamageBonusPerShieldInterval(), "%"))
                .append(Component.text(" more damage for every " + SHIELD_HEALTH_INTERVAL + " Overgrowth shield health they have. When they deal damage, they gain an Overgrowth shield equal to "))
                .append(formatTitleUpgrade(getShieldFromDamagePercent(), "%"))
                .append(Component.text(" of the damage dealt, up to " + MAX_SHIELD_PERCENT_OF_LINKED_ALLY_HEALTH + "% of their max health. The link lasts until broken or recast. Has a cooldown of " + RELINK_COOLDOWN_SECONDS + " seconds."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getDamageBonusPerShieldInterval(), "%"),
                        formatTitleUpgrade(getDamageBonusPerShieldIntervalUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(getShieldFromDamagePercent(), "%"),
                        formatTitleUpgrade(getShieldFromDamagePercentUpgraded(), "%")
                )
        );
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.OVERGROWTH;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryOvergrowthAbility(this);
    }

    @Override
    public AbstractAbility getAbility() {
        return ability;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 100;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 130;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 100;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 8;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (linkedAlly == null) {
                    return;
                } else {
                    EffectUtils.playParticleLinkAnimation(player.getLocation(), linkedAlly.getLocation(), Particle.PALE_OAK_LEAVES);
                }
                if (!isLinkValid()) {
                    breakLink(true);
                }
            }
        }.runTaskTimer(0, 10);
    }

    private boolean linkToAlly(WarlordsPlayer source, WarlordsEntity ally) {
        if (ally == null || ally == source || !source.isTeammateAlive(ally)) {
            source.sendMessage(Component.text("No valid ally found to link Overgrowth.", NamedTextColor.RED));
            return false;
        }

        if (source.getLocation().distanceSquared(ally.getLocation()) > LINK_RANGE * LINK_RANGE) {
            source.sendMessage(Component.text("That ally is too far away to link Overgrowth.", NamedTextColor.RED));
            return false;
        }

        breakLink(false);

        linkedAlly = ally;
        linkData = new OvergrowthLinkData(source, ally);

        ally.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getTitleName() + " Link",
                null,
                OvergrowthLinkData.class,
                linkData,
                source,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            if (!isLinkActiveFor(event.getSource())) {
                return;
            }

            float shieldHealth = getOvergrowthShieldHealth(linkedAlly);
            int stacks = (int) (shieldHealth / SHIELD_HEALTH_INTERVAL);

            if (stacks <= 0) {
                return;
            }

            float multiplier = 1 + stacks * getDamageBonusPerShieldInterval() / 100f;
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), multiplier);
        }).addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
            if (!isLinkActiveFor(event.getSource())) {
                return;
            }
            if (!event.isDamageInstance()) {
                return;
            }
            if (currentDamageValue <= 0) {
                return;
            }

            float shieldGain = currentDamageValue * getShieldFromDamagePercent() / 100f;
            addOvergrowthShield(linkData, shieldGain);
        }));

        source.sendMessage(Component.text("Linked Overgrowth to ", NamedTextColor.GREEN).append(ally.getColoredName()).append(Component.text(".")));
        ally.sendMessage(source.getColoredName().append(Component.text(" linked Overgrowth to you.", NamedTextColor.GREEN)));

        source.playSound(source.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 0.5f);
        ally.playSound(ally.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 0.5f);

        return true;
    }

    private boolean isLinkActiveFor(WarlordsEntity source) {
        return linkedAlly != null && linkedAlly == source && isLinkValid();
    }

    private boolean isLinkValid() {
        if (warlordsPlayer == null || linkedAlly == null || linkData == null) {
            return false;
        }
        if (!warlordsPlayer.isAlive() || !linkedAlly.isAlive()) {
            return false;
        }
        if (!warlordsPlayer.isActive() || !linkedAlly.isActive()) {
            return false;
        }
        if (warlordsPlayer.getWorld() != linkedAlly.getWorld()) {
            return false;
        }
        return warlordsPlayer.getLocation().distanceSquared(linkedAlly.getLocation()) <= LINK_RANGE * LINK_RANGE;
    }

    private void breakLink(boolean notify) {
        if (linkedAlly == null || linkData == null) {
            linkedAlly = null;
            linkData = null;
            return;
        }

        WarlordsEntity oldLinkedAlly = linkedAlly;
        OvergrowthLinkData oldLinkData = linkData;

        oldLinkedAlly.getCooldownManager().removeCooldownByObject(oldLinkData);

        if (oldLinkData.overgrowthShield != null) {
            oldLinkedAlly.getCooldownManager().removeCooldownByObject(oldLinkData.overgrowthShield);
        }

        if (notify && warlordsPlayer != null) {
            warlordsPlayer.sendMessage(Component.text("Your Overgrowth link was broken.", NamedTextColor.RED));
            oldLinkedAlly.sendMessage(Component.text("Your Overgrowth link was broken.", NamedTextColor.RED));
        }

        linkedAlly = null;
        linkData = null;
    }

    private void addOvergrowthShield(OvergrowthLinkData data, float shieldGain) {
        if (data == null || data.linkedAlly == null || shieldGain <= 0) {
            return;
        }

        WarlordsEntity ally = data.linkedAlly;
        float shieldCap = ally.getMaxHealth() * MAX_SHIELD_PERCENT_OF_LINKED_ALLY_HEALTH / 100f;
        Optional<RegularCooldown<Shield>> existingShield = getExistingOvergrowthShield(ally);

        if (existingShield.isPresent()) {
            RegularCooldown<Shield> cooldown = existingShield.get();
            Shield shield = cooldown.getCooldownObject();

            shield.setMaxShieldHealth(shieldCap);
            shield.addShieldHealth(shieldGain);

            if (shield.getShieldHealth() > shieldCap) {
                shield.setShieldHealth(shieldCap);
            }

            data.overgrowthShield = shield;
            Shield.updateAbsorption(ally);
            return;
        }

        float initialShield = Math.min(shieldGain, shieldCap);
        if (initialShield <= 0) {
            return;
        }

        Shield shield = new Shield(getShieldName(), shieldCap);
        shield.setShieldHealth(initialShield);
        data.overgrowthShield = shield;

        ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                getShieldName(),
                null,
                Shield.class,
                shield,
                data.source,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                true,
                Integer.MAX_VALUE,
                Collections.emptyList()
        ) {

            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(data.source), NamedTextColor.GREEN);
            }

        });

    }

    @SuppressWarnings("unchecked")
    private Optional<RegularCooldown<Shield>> getExistingOvergrowthShield(WarlordsEntity ally) {
        return (Optional<RegularCooldown<Shield>>) (Optional<?>) new CooldownFilter<>(ally, RegularCooldown.class)
                .filterCooldownClass(Shield.class)
                .filterCooldownFrom(warlordsPlayer)
                .filterName(getShieldName())
                .filter(RegularCooldown::hasTicksLeft)
                .findFirst();
    }

    private float getOvergrowthShieldHealth(WarlordsEntity ally) {
        return (float) new CooldownFilter<>(ally, RegularCooldown.class)
                .filterCooldownClass(Shield.class)
                .filterCooldownFrom(warlordsPlayer)
                .filterName(getShieldName())
                .filter(RegularCooldown::hasTicksLeft)
                .stream()
                .map(RegularCooldown::getCooldownObject)
                .filter(Shield.class::isInstance)
                .map(Shield.class::cast)
                .mapToDouble(Shield::getShieldHealth)
                .sum();
    }

    private String getShieldName() {
        return getTitleName() + " Shield";
    }

    private float getDamageBonusPerShieldInterval() {
        return DAMAGE_BONUS_PER_150_SHIELD + DAMAGE_BONUS_PER_150_SHIELD_PER_UPGRADE * getTitleLevel();
    }

    private float getDamageBonusPerShieldIntervalUpgraded() {
        return DAMAGE_BONUS_PER_150_SHIELD + DAMAGE_BONUS_PER_150_SHIELD_PER_UPGRADE * getTitleLevelUpgraded();
    }

    private float getShieldFromDamagePercent() {
        return SHIELD_FROM_DAMAGE_PERCENT + SHIELD_FROM_DAMAGE_PERCENT_PER_UPGRADE * getTitleLevel();
    }

    private float getShieldFromDamagePercentUpgraded() {
        return SHIELD_FROM_DAMAGE_PERCENT + SHIELD_FROM_DAMAGE_PERCENT_PER_UPGRADE * getTitleLevelUpgraded();
    }

    private static class OvergrowthLinkData {

        private final WarlordsPlayer source;
        private final WarlordsEntity linkedAlly;
        private Shield overgrowthShield;

        private OvergrowthLinkData(WarlordsPlayer source, WarlordsEntity linkedAlly) {
            this.source = source;
            this.linkedAlly = linkedAlly;
        }

    }

    private static class LegendaryOvergrowthAbility extends AbstractAbility {

        private final LegendaryOvergrowth weapon;

        public LegendaryOvergrowthAbility(LegendaryOvergrowth weapon) {
            super(AbstractAbilityBuilder.create("overgrowth").weapon().cooldown(RELINK_COOLDOWN_SECONDS).energyCost(0).startNoCooldown());
            this.weapon = weapon;
        }

        @Override
        protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
            if (!(wp instanceof WarlordsPlayer player)) {
                return false;
            }

            Optional<WarlordsEntity> target = PlayerFilter
                    .entitiesAround(player, LINK_RANGE, LINK_RANGE, LINK_RANGE)
                    .aliveTeammatesOfExcludingSelf(player)
                    .requireLineOfSight(player)
                    .lookingAtFirst(player)
                    .findFirst();

            if (target.isEmpty()) {
                player.sendMessage(Component.text("Look at an ally within " + LINK_RANGE + " blocks to link Overgrowth.", NamedTextColor.RED));
                return false;
            }

            EffectUtils.playParticleLinkAnimation(wp.getLocation(), target.get().getLocation(), Particle.PALE_OAK_LEAVES);

            return weapon.linkToAlly(player, target.get());
        }

        @Override
        public void updateDescription(Player player) {
            description = Component.text("Link to one ally within " + LINK_RANGE + " blocks. The linked ally gains damage based on their Overgrowth shield health and gains an absorption shield whenever they deal damage. Recasting links a new ally.");
        }

    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_LIBRARY_ARCHIVES, 1L);
        return baseCost;
    }
}