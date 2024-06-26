package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.ChainLightningBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainLightning extends AbstractChain implements RedAbilityIcon, Duration, Damages<ChainLightning.DamageValues> {

    public static final ItemStack CHAIN_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS);

    public static <T> void giveShockedEffect(WarlordsEntity giver, WarlordsEntity receiver, Class<T> clazz, T object) {
        receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Aftershock",
                "SHOCKED",
                clazz,
                object,
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                3 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        EffectUtils.displayParticle(
                                Particle.ELECTRIC_SPARK,
                                receiver.getLocation().add(0, 1.2, 0),
                                5,
                                .25,
                                .25,
                                .25,
                                0
                        );
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return event.getSource().equals(giver) ? currentDamageValue * 1.3f : currentDamageValue;
            }

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
        });
    }
    public int numberOfDismounts = 0;
    private final DamageValues damageValues = new DamageValues();
    private float damageReductionPerBounce = 10;
    private float maxDamageReduction = 25;
    private int damageReductionTickDuration = 90;

    public ChainLightning() {
        super("Chain Lightning", 9.4f, 40, 20, 10, 3);
    }

    public ChainLightning(float cooldown, float startCooldown) {
        super("Chain Lightning", cooldown, 40, 20, 10, 3, startCooldown);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Discharge a bolt of lightning at the targeted enemy player that deals ")
                               .append(Damages.formatDamage(damageValues.chainDamage))
                               .append(Component.text(" damage and jumps to "))
                               .append(Component.text(additionalBounces, NamedTextColor.YELLOW))
                               .append(Component.text(" additional targets within "))
                               .append(Component.text(bounceRange, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks. Each time the lightning jumps, the damage is decreased by "))
                               .append(Component.text("15%", NamedTextColor.RED))
                               .append(Component.text(". You gain "))
                               .append(Component.text(format(damageReductionPerBounce) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage resistance for each target hit, up to "))
                               .append(Component.text(format(maxDamageReduction) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage resistance. This buff lasts "))
                               .append(Component.text(format(damageReductionTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds.\n\nHas an initial cast range of "))
                               .append(Component.text(radius, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
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
        return new ChainLightningBranch(abilityTree, this);
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
                cooldownManager -> {
                },
                damageReductionTickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue;
                float reduction = convertToDivisionDecimal(maxDamageReduction);
                float multiplier = (((10 - hitCounter) / damageReductionPerBounce));
                if (multiplier > reduction && pveMasterUpgrade) {
                    multiplier = reduction;
                }
                newDamageValue = currentDamageValue * multiplier;
                return newDamageValue;
            }
        });
    }

    @Override
    protected ItemStack getChainItem() {
        return CHAIN_ITEM;
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
                } // no else
            } else {
                Optional<CapacitorTotem.CapacitorTotemData> optionalTotem = AbstractTotem.getTotemDownAndClose(wp, checkFrom, CapacitorTotem.CapacitorTotemData.class);
                if (optionalTotem.isPresent()) {
                    CapacitorTotem.CapacitorTotemData data = optionalTotem.get();
                    ArmorStand armorStand = data.getArmorStand();
                    chain(checkFrom.getLocation(), armorStand.getLocation());
                    partOfChainLightningPulseDamage(data);
                    return partOfChainLightning(wp, playersHit, armorStand, true);
                } // no else
            }
        } // no else

        PlayerFilter filter = firstCheck ?
                              PlayerFilter.entitiesAround(checkFrom, radius, 18, radius)
                                          .filter(e -> LocationUtils.isLookingAtChain(wp, e) && LocationUtils.hasLineOfSight(wp, e)) :
                              PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange)
                                          .lookingAtFirst(wp);

        Optional<WarlordsEntity> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsEntity hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier = switch (playersSize) {
                case 0 -> pveMasterUpgrade ? 1.1f : 1f;
                case 1 -> pveMasterUpgrade ? 1.2f : .85f;
                default -> pveMasterUpgrade ? 1.3f : .7f;
            };

            playersHit.add(hit);
            if (hit.onHorse()) {
                numberOfDismounts++;
            }

            hit.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(damageValues.chainDamage.getMinValue() * damageMultiplier)
                    .max(damageValues.chainDamage.getMaxValue() * damageMultiplier)
                    .crit(damageValues.chainDamage)
            );

            if (pveMasterUpgrade2) {
                giveShockedEffect(
                        wp,
                        hit,
                        ChainLightning.class,
                        new ChainLightning()
                );
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
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.CapacitorTotemData.class)
                .filter(totem -> totem.getArmorStand().getLocation().distanceSquared(warlordsPlayer.getLocation()) <= radius * radius
                        && totem.isPlayerLookingAtTotem(warlordsPlayer))
                .findFirst();
    }

    @Override
    public int getTickDuration() {
        return damageReductionTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.damageReductionTickDuration = tickDuration;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable chainDamage = new Value.RangedValueCritable(370, 499, 20, 175);
        private final List<Value> values = List.of(chainDamage);

        public Value.RangedValueCritable getChainDamage() {
            return chainDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
