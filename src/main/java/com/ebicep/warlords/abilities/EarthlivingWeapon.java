package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.EarthlivingWeaponBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EarthlivingWeapon extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<EarthlivingWeapon.HealingValues>, AbilityStats<EarthlivingWeapon, EarthlivingWeapon.EarthlivingWeaponStats> {

    private final EarthlivingWeaponStats stats = new EarthlivingWeaponStats();
    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 160;
    private float procChance = 40;
    private int maxAllies = 2;
    private int weaponDamage = 240;
    private int maxHits = 1;
    private int guaranteedHits = 1;

    public EarthlivingWeapon() {
        super(AbstractAbilityBuilder.create("earthlivingWeapon").pvp());
    }

    public EarthlivingWeapon(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.procChance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("procChance"), float.class);
        this.maxAllies = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAllies"), int.class);
        this.weaponDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("weaponDamage"), int.class);
        this.maxHits = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHits"), int.class);
        this.guaranteedHits = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("guaranteedHits"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
        wp.getCooldownManager()
          .addCooldown(new RegularCooldown<>(name, "EARTH", EarthlivingData.class, new EarthlivingData(guaranteedHits), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed % 4 == 0) {
                EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.3, 0.3, 0.1);
            }
        })
        ) {

            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getCause().isEmpty()) {
                    return;
                }
                WarlordsEntity victim = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getSource();
                activateEarthliving(victim, attacker, cooldownObject);
            }

            @Override
            public float addEnergyPerHit(WarlordsEntity we, float energyPerHit) {
                return energyPerHit + 10f;
            }
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Imbue your weapon with the power of the Earth, causing each of your melee attacks to have a ")
                                               .percent(procChance, NamedTextColor.BLUE)
                                               .text(" chance to heal you and ")
                                               .text(maxAllies, NamedTextColor.BLUE)
                                               .text(" nearby allies for ")
                                               .percent(weaponDamage, NamedTextColor.GREEN)
                                               .text(" weapon damage. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .emptyLine()
                                               .text("The first hit is guaranteed to activate Earthliving.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EarthlivingWeaponBranch(abilityTree, this);
    }

    public void activateEarthliving(WarlordsEntity victim, WarlordsEntity attacker, EarthlivingData data) {
        double earthlivingActivate = ThreadLocalRandom.current().nextDouble(100);
        if (data.guaranteedHitsLeft > 0) {
            data.guaranteedHitsLeft--;
            earthlivingActivate = 0;
        }
        if (!(earthlivingActivate < procChance)) {
            return;
        }
        boolean previosulyProcd = data.alreadyProcd.contains(victim);
        if (pveMasterUpgrade) {
            energyPulseOnHit(attacker, victim);
        } else if (pveMasterUpgrade2) {
            data.alreadyProcd.add(victim);
        }
        new GameRunnable(victim.getGame()) {

            final float minDamage = attacker instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                    warlordsPlayer.getWeapon().getMeleeDamageMin() :
                                    healingValues.earthlivingHealing.getMinValue();

            final float maxDamage = attacker instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                    warlordsPlayer.getWeapon().getMeleeDamageMax() :
                                    healingValues.earthlivingHealing.getMaxValue();

            int counter = 0;

            @Override
            public void run() {
                stats.timesProcd++;
                Utils.playGlobalSound(victim.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                float cc = pveMasterUpgrade2 && !previosulyProcd ? 100 : healingValues.earthlivingHealing.getCritChanceValue();
                List<WarlordsEntity> healedPlayers = PlayerFilter.entitiesAround(attacker, 6, 6, 6)
                                                                 .aliveTeammatesOfExcludingSelf(attacker)
                                                                 .limit(maxAllies)
                                                                 .toList();
                attacker.addInstance(InstanceBuilder
                        .healing()
                        .ability(EarthlivingWeapon.this)
                        .source(attacker)
                        .min(minDamage * convertToPercent(weaponDamage))
                        .max(maxDamage * convertToPercent(weaponDamage))
                        .critChance(cc)
                        .critMultiplier(healingValues.earthlivingHealing.getCritMultiplierValue())
                        .customFlags(new CustomInstanceFlags.PlayersEffectedInstanceFlag(healedPlayers))
                );
                for (WarlordsEntity nearPlayer : healedPlayers) {
                    stats.targetsHealed++;
                    nearPlayer.addInstance(InstanceBuilder
                            .healing()
                            .ability(EarthlivingWeapon.this)
                            .source(attacker)
                            .min(minDamage * convertToPercent(weaponDamage))
                            .max(maxDamage * convertToPercent(weaponDamage))
                            .critChance(cc)
                            .critMultiplier(healingValues.earthlivingHealing.getCritMultiplierValue())
                            .customFlags(new CustomInstanceFlags.PlayersEffectedInstanceFlag(healedPlayers))
                    );
                }
                counter++;
                if (counter == maxHits) {
                    this.cancel();
                }
            }
        }.runTaskTimer(3, 8);
    }

    private void energyPulseOnHit(WarlordsEntity giver, WarlordsEntity target) {
        target.getCooldownManager().addRegularCooldown("Earthliving PvE", "", EarthlivingWeapon.class, null, giver, CooldownTypes.LOW_LEVEL_DEBUFF, cooldownManager -> {
                    Utils.playGlobalSound(target.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.2f);
                    new FallingBlockWaveEffect(target.getLocation(), 6, 1, Material.BIRCH_SAPLING).play();
                    for (WarlordsEntity ally : PlayerFilter.entitiesAround(target, 10, 10, 10).aliveTeammatesOf(giver).closestFirst(target)) {
                        float missingHealth = (ally.getMaxHealth() - ally.getCurrentHealth()) * 0.1f;
                        if (missingHealth <= 0) {
                            continue;
                        }
                        ally.addInstance(InstanceBuilder.healing().cause("Loamliving Weapon").source(giver).value(missingHealth));
                        ally.addEnergy(giver, "Loamliving Weapon", missingHealth / 20);
                    }
                }, 2 * 20, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            target.setStunTicks(2);
                    if (ticksElapsed % 5 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1.05, Particle.HAPPY_VILLAGER, 1);
                    }
                })
        );
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
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public EarthlivingWeaponStats getAbilityStats() {
        return stats;
    }

    public int getGuaranteedHits() {
        return guaranteedHits;
    }

    public void setGuaranteedHits(int guaranteedHits) {
        this.guaranteedHits = guaranteedHits;
    }

    public float getProcChance() {
        return procChance;
    }

    public void setProcChance(float procChance) {
        this.procChance = procChance;
    }

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(int weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public static class EarthlivingData {

        private final Set<WarlordsEntity> alreadyProcd = new HashSet<>();

        private int guaranteedHitsLeft;

        public EarthlivingData(int guaranteedHitsLeft) {
            this.guaranteedHitsLeft = guaranteedHitsLeft;
        }

        public int getGuaranteedHitsLeft() {
            return guaranteedHitsLeft;
        }

        public void setGuaranteedHitsLeft(int guaranteedHitsLeft) {
            this.guaranteedHitsLeft = guaranteedHitsLeft;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable earthlivingHealing = new Value.RangedValueCritable(132, 179, 25, 200);

        private List<Value> values = List.of(earthlivingHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.earthlivingHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("earthlivingHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(earthlivingHealing);
        }

    }

    public static class EarthlivingWeaponStats extends AbstractAbilityStats<EarthlivingWeapon, EarthlivingWeaponStats> {

        @Field("times_procd")
        private int timesProcd = 0;

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Override
        public Class<EarthlivingWeaponStats> getClazz() {
            return EarthlivingWeaponStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Proc'd", timesProcd));
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            return statsDisplay;
        }

        @Override
        public EarthlivingWeaponStats merge(EarthlivingWeaponStats other, int multiplier) {
            EarthlivingWeaponStats stats = super.merge(other, multiplier);
            stats.timesProcd = this.timesProcd + other.timesProcd * multiplier;
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            return stats;
        }

        @Override
        public EarthlivingWeaponStats create() {
            return new EarthlivingWeaponStats();
        }

    }

}
