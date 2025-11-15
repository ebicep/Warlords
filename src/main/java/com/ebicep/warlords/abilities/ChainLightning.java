package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.ChainLightningBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class ChainLightning extends AbstractChain<ChainLightning, ChainLightning.ChainLightningStats> implements RedAbilityIcon, Duration, Damages<ChainLightning.DamageValues> {

    public static final ItemStack CHAIN_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS);

    public static <T> void giveShockedEffect(WarlordsEntity giver, WarlordsEntity receiver, Class<T> clazz, T object) {
        receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Aftershock",
                "SHOCKED",
                clazz,
                object,
                giver,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {},
                3 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        EffectUtils.displayParticle(Particle.ELECTRIC_SPARK, receiver.getLocation().add(0, 1.2, 0), 5, .25, .25, .25, 0);
                    }
                })
        ) {
            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (event.getSource().equals(giver) && isKiller) {
                    for (AbstractAbility ability : giver.getAbilities()) {
                        if (ability instanceof OrangeAbilityIcon) {
                            ability.subtractCurrentCooldown(.5f);
                        }
                    }
                }
            }
        }.addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    if (event.getSource().equals(giver)) {
                        currentDamageValue.addMultiplicativeModifierMult("Aftershock", 1.3f);
                    }
                }
        ));
    }

    private final ChainLightningStats stats = new ChainLightningStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable damageReductionPerBounce = new FloatModifiable(10);
    private FloatModifiable maxDamageReduction = new FloatModifiable(25);
    private FloatModifiable damageDecreasePerBounce = new FloatModifiable(15);
    private int damageReductionTickDuration = 90;

    public ChainLightning() {
        super(AbstractAbilityBuilder.create("chainLightning").pvp());
    }

    public ChainLightning(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.damageReductionPerBounce = new FloatModifiable(ConfigManager.getAbilityConfigValue(
                builder.getNamespaces(),
                builder.getAppendedFieldName("damageReductionPerBounce"),
                float.class
        ));
        this.maxDamageReduction = new FloatModifiable(ConfigManager.getAbilityConfigValue(
                builder.getNamespaces(),
                builder.getAppendedFieldName("maxDamageReduction"),
                float.class
        ));
        this.damageDecreasePerBounce = new FloatModifiable(ConfigManager.getAbilityConfigValue(
                builder.getNamespaces(),
                builder.getAppendedFieldName("damageDecreasePerBounce"),
                float.class
        ));
        this.damageReductionTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionTickDuration"), int.class);
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp) {
        return partOfChainLightning(wp, new HashSet<>(), wp.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsEntity wp, int hitCounter) {
        Utils.playGlobalSound(wp.getLocation(), "shaman.chainlightning.activation", 3, 1);
        wp.playSound(wp.getLocation(), "shaman.chainlightning.impact", 2, 1);
        wp.getCooldownManager().removeCooldown(ChainLightning.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CHAIN",
                ChainLightning.class,
                new ChainLightning(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                damageReductionTickDuration
        ).addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    currentDamageValue.addMultiplicativeModifierMult(name,
                            convertToDivisionDecimal(Math.min(hitCounter * damageReductionPerBounce.getCalculatedValue(), maxDamageReduction.getCalculatedValue()))
                    );
                }
        ));
    }

    @Override
    protected ItemStack getChainItem() {
        return CHAIN_ITEM;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Discharge a bolt of lightning at the targeted enemy player that deals ")
                                               .damage(damageValues.chainDamage)
                                               .text(" damage and jumps to ")
                                               .text(additionalBounces, NamedTextColor.BLUE)
                                               .text(" additional targets within ")
                                               .blocks(bounceRange)
                                               .text(". Each time the lightning jumps, the damage is decreased by ")
                                               .percent(damageDecreasePerBounce, NamedTextColor.RED)
                                               .text(". You gain ")
                                               .percent(damageReductionPerBounce, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" damage resistance for each target hit, up to ")
                                               .percent(maxDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" damage resistance. This buff lasts ")
                                               .durationTicks(damageReductionTickDuration)
                                               .text(".")
                                               .initialRange(radius)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ChainLightningBranch(abilityTree, this);
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        damageDecreasePerBounce.tick();
        damageReductionPerBounce.tick();
        maxDamageReduction.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    public int getTickDuration() {
        return damageReductionTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.damageReductionTickDuration = tickDuration;
    }

    @Override
    public ChainLightningStats getAbilityStats() {
        return stats;
    }

    private Set<WarlordsEntity> partOfChainLightning(WarlordsEntity wp, Set<WarlordsEntity> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? additionalBounces - 1 : additionalBounces)) {
            if (hasHitTotem) {
                playersHit.add(null);
            }
            return playersHit;
        }
        boolean firstCheck = checkFrom == wp.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                Optional<CapacitorTotem.CapacitorTotemData> optionalTotem = getLookingAtTotem(wp);
                if (optionalTotem.isPresent()) {
                    CapacitorTotem.CapacitorTotemData data = optionalTotem.get();
                    ArmorStand armorStand = data.getArmorStand();
                    chain(checkFrom.getLocation(), armorStand.getLocation());
                    partOfChainLightningPulseDamage(data);
                    playersHit.add(null);
                    return partOfChainLightning(wp, playersHit, armorStand, true);
                }
                // no else
            } else {
                Optional<CapacitorTotem.CapacitorTotemData> optionalTotem = AbstractTotem.getTotemDownAndClose(wp, checkFrom, CapacitorTotem.CapacitorTotemData.class);
                if (optionalTotem.isPresent()) {
                    CapacitorTotem.CapacitorTotemData data = optionalTotem.get();
                    ArmorStand armorStand = data.getArmorStand();
                    chain(checkFrom.getLocation(), armorStand.getLocation());
                    partOfChainLightningPulseDamage(data);
                    return partOfChainLightning(wp, playersHit, armorStand, true);
                }
                // no else
            }
        }
        // no else
        float rad = radius + PacketUtils.pingCompensationAmount(wp);
        PlayerFilter filter = firstCheck ?
                              PlayerFilter
                                      .entitiesAround(checkFrom, rad, 18, rad)
                                      .filter(e -> LocationUtils.isLookingAtChain(wp, e) && LocationUtils.hasLineOfSight(wp, e)) :
                              PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange)
                                          .lookingAtFirst(wp);
        Optional<WarlordsEntity> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsEntity hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier = 1 - Math.min(playersSize, pveMasterUpgrade ? Integer.MAX_VALUE : 3) * damageDecreasePerBounce.getCalculatedValue() / 100f;
            playersHit.add(hit);
            if (hit.onHorse()) {
                stats.numberOfDismounts++;
            }
            hit.addInstance(InstanceBuilder.damage()
                                           .ability(this)
                                           .source(wp)
                                           .min(damageValues.chainDamage.getMinValue() * damageMultiplier)
                                           .max(damageValues.chainDamage.getMaxValue() * damageMultiplier)
                                           .crit(damageValues.chainDamage));
            if (pveMasterUpgrade2) {
                giveShockedEffect(wp, hit, ChainLightning.class, new ChainLightning());
            }
            return partOfChainLightning(wp, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersHit;
        }
    }

    private void partOfChainLightningPulseDamage(CapacitorTotem.CapacitorTotemData data) {
        ArmorStand armorStand = data.getArmorStand();
        data.proc();
        if (data.getTotem().isPveMasterUpgrade()) {
            data.setRadius(data.getRadius() + 0.5);
        }
        Utils.playGlobalSound(armorStand.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        data.getOwner().playSound(armorStand.getLocation(), "shaman.chainlightning.impact", 2, 1);
    }

    private Optional<CapacitorTotem.CapacitorTotemData> getLookingAtTotem(WarlordsEntity warlordsPlayer) {
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class).filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.CapacitorTotemData.class)
                                                                          .filter(totem -> totem.getArmorStand()
                                                                                                .getLocation()
                                                                                                .distanceSquared(warlordsPlayer.getLocation()) <= radius * radius && totem.isPlayerLookingAtTotem(
                                                                                  warlordsPlayer))
                                                                          .findFirst();
    }

    public FloatModifiable getDamageReductionPerBounce() {
        return damageReductionPerBounce;
    }

    public FloatModifiable getMaxDamageReduction() {
        return maxDamageReduction;
    }

    public FloatModifiable getDamageDecreasePerBounce() {
        return damageDecreasePerBounce;
    }

    public int getDamageReductionTickDuration() {
        return damageReductionTickDuration;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable chainDamage = new Value.RangedValueCritable(370, 499, 20, 175);

        private List<Value> values = List.of(chainDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.chainDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("chainDamage"), Value.RangedValueCritable.class);
            this.values = List.of(chainDamage);
        }

        public Value.RangedValueCritable getChainDamage() {
            return chainDamage;
        }

    }

    public static class ChainLightningStats extends AbstractChainStats<ChainLightning, ChainLightningStats> {

        @Field("number_of_dismounts")
        private int numberOfDismounts = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Dismounts", numberOfDismounts));
            return statsDisplay;
        }

        @Override
        public ChainLightningStats merge(ChainLightningStats other, int multiplier) {
            ChainLightningStats stats = super.merge(other, multiplier);
            stats.numberOfDismounts = this.numberOfDismounts + other.numberOfDismounts * multiplier;
            return stats;
        }

        @Override
        public Class<ChainLightningStats> getClazz() {
            return ChainLightningStats.class;
        }

        @Override
        public ChainLightningStats create() {
            return new ChainLightningStats();
        }

    }

}
