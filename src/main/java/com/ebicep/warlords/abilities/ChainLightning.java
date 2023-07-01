package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractChain;
import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainLightning extends AbstractChain implements Comparable<ChainLightning> {

    public int numberOfDismounts = 0;

    private int damageReduction = 0;
    private float damageReductionPerBounce = 10;
    private float maxDamageReduction = 30;

    public ChainLightning() {
        super("Chain Lightning", 370, 499, 9.4f, 40, 20, 175, 20, 10, 3);
    }

    public ChainLightning(int damageReduction) {
        super("Chain Lightning", 370, 499, 9.4f, 40, 20, 175, 20, 10, 3);
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Discharge a bolt of lightning at the targeted enemy player that deals ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
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
                               .append(Component.text("4.5", NamedTextColor.GOLD))
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
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp, Player player) {
        return partOfChainLightning(wp, new HashSet<>(), wp.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsEntity wp, Player player, int hitCounter) {
        Utils.playGlobalSound(player.getLocation(), "shaman.chainlightning.activation", 3, 1);
        player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

        wp.getCooldownManager().removeCooldown(ChainLightning.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CHAIN",
                ChainLightning.class,
                new ChainLightning(hitCounter),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                4 * 20 + 10
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue;
                float multiplier = (((10 - hitCounter) / damageReductionPerBounce));
                if (multiplier > (100 - maxDamageReduction / 100f) && pveMasterUpgrade) {
                    multiplier = (100 - maxDamageReduction / 100f);
                }
                newDamageValue = currentDamageValue * multiplier;
                event.getWarlordsEntity().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.GRAY_STAINED_GLASS);
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
                Optional<CapacitorTotem> optionalTotem = getLookingAtTotem(wp);
                if (optionalTotem.isPresent()) {
                    ArmorStand totem = optionalTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, optionalTotem.get());
                    playersHit.add(null);
                    return partOfChainLightning(wp, playersHit, totem, true);
                } // no else
            } else {
                Optional<CapacitorTotem> capacitorTotem = AbstractTotem.getTotemDownAndClose(wp, checkFrom, CapacitorTotem.class);
                if (capacitorTotem.isPresent()) {
                    ArmorStand totem = capacitorTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, capacitorTotem.get());
                    return partOfChainLightning(wp, playersHit, totem, true);
                } // no else
            }
        } // no else

        PlayerFilter filter = firstCheck ? PlayerFilter.entitiesAround(checkFrom, radius, 18, radius)
                .filter(e ->
                        LocationUtils.isLookingAtChain(wp.getEntity(), e.getEntity()) &&
                                LocationUtils.hasLineOfSight(wp.getEntity(), e.getEntity())
                ) : PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange)
                .lookingAtFirst(wp);

        Optional<WarlordsEntity> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsEntity hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier = switch (playersSize) {
                case 0 ->
                    // We hit the first player
                        pveMasterUpgrade ? 1.1f : 1f;
                case 1 ->
                    // We hit the second player
                        pveMasterUpgrade ? 1.2f : .85f;
                default -> pveMasterUpgrade ? 1.3f : .7f;
            };

            playersHit.add(hit);
            if (hit.onHorse()) {
                numberOfDismounts++;
            }

            hit.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * damageMultiplier,
                    maxDamageHeal * damageMultiplier,
                    critChance,
                    critMultiplier
            );

            return partOfChainLightning(wp, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersHit;
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsEntity wp, CapacitorTotem capacitorTotem) {
        ArmorStand totem = capacitorTotem.getTotem();
        capacitorTotem.pulseDamage();
        if (capacitorTotem.isPveMasterUpgrade()) {
            capacitorTotem.setRadius(capacitorTotem.getRadius() + 0.5);
        }

        Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        wp.playSound(totem.getLocation(), "shaman.chainlightning.impact", 2, 1);

        capacitorTotem.addProc();
    }

    private Optional<CapacitorTotem> getLookingAtTotem(WarlordsEntity warlordsPlayer) {
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.class)
                .filter(totem -> totem.getTotem().getLocation().distanceSquared(warlordsPlayer.getLocation()) <= radius * radius
                        && totem.isPlayerLookingAtTotem(warlordsPlayer))
                .findFirst();
    }

    @Override
    public int compareTo(ChainLightning chainLightning) {
        return Integer.compare(this.damageReduction, chainLightning.damageReduction);
    }

    public float getDamageReductionPerBounce() {
        return damageReductionPerBounce;
    }

    public void setDamageReductionPerBounce(float damageReductionPerBounce) {
        this.damageReductionPerBounce = damageReductionPerBounce;
    }

    public float getMaxDamageReduction() {
        return maxDamageReduction;
    }

    public void setMaxDamageReduction(float maxDamageReduction) {
        this.maxDamageReduction = maxDamageReduction;
    }
}
