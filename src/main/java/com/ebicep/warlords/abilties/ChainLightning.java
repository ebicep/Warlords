package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainLightning extends AbstractChainBase implements Comparable<ChainLightning> {
    private boolean pveUpgrade = false;

    protected int numberOfDismounts = 0;
    private int damageReduction = 0;

    private int radius = 20;
    private int bounceRange = 10;
    private int maxBounces = 3;
    private float damageReductionPerBounce = 10;
    private float maxDamageReduction = 30;

    public int getDamageReduction() {
        return damageReduction;
    }

    public ChainLightning() {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
    }

    public ChainLightning(int damageReduction) {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
        this.damageReduction = damageReduction;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a bolt of lightning at the\n" +
                "§7targeted enemy player that deals\n" +
                "§c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage and jumps to\n" +
                "§e" + maxBounces  + " §7additional targets within §e" + bounceRange + "\n" +
                "§7blocks. Each time the lightning jumps\n" +
                "§7the damage is decreased by §c15%§7.\n" +
                "§7You gain §e" + damageReductionPerBounce + "% §7damage resistance for\n" +
                "§7each target hit, up to §e" + maxDamageReduction + "% §7damage\n" +
                "§7resistance. This buff lasts §64.5 §7seconds." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
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
    protected int getHitCounterAndActivate(WarlordsEntity wp, Player player) {
        return partOfChainLightning(wp, new HashSet<>(), wp.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsEntity wp, Player player, int hitCounter) {
        Utils.playGlobalSound(player.getLocation(), "shaman.chainlightning.activation", 3, 1);
        player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

        wp.getCooldownManager().removeCooldown(ChainLightning.class);
        wp.getCooldownManager().addCooldown(new RegularCooldown<ChainLightning>(
                name,
                "CHAIN",
                ChainLightning.class,
                new ChainLightning(hitCounter),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                4 * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue;
                float multiplier = (((10 - hitCounter) / damageReductionPerBounce));
                if (multiplier > (100 - maxDamageReduction / 100f) && pveUpgrade) {
                    multiplier = (100 - maxDamageReduction / 100f);
                }
                newDamageValue = currentDamageValue * multiplier;
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });
        wp.getSpec().getRed().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.STAINED_GLASS, 1, (byte) 7);
    }

    private int partOfChainLightning(WarlordsEntity wp, Set<WarlordsEntity> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? maxBounces - 1 : maxBounces)) {
            return playersSize + (hasHitTotem ? 1 : 0);
        }
        /**
         * The first check has double the radius for checking, and only targets a totem when the player is looking at it.
         */
        boolean firstCheck = checkFrom == wp.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                Optional<CapacitorTotem> optionalTotem = getLookingAtTotem(wp);
                if (checkFrom instanceof LivingEntity && optionalTotem.isPresent()) {
                    ArmorStand totem = optionalTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, optionalTotem.get());
                    return partOfChainLightning(wp, playersHit, totem, true);
                } // no else
            } else {
                Optional<CapacitorTotem> capacitorTotem = AbstractTotemBase.getTotemDownAndClose(wp, checkFrom, CapacitorTotem.class);
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
                            Utils.isLookingAtChain(wp.getEntity(), e.getEntity()) &&
                            Utils.hasLineOfSight(wp.getEntity(), e.getEntity())
                        ) : PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange)
                        .lookingAtFirst(wp);

        Optional<WarlordsEntity> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsEntity hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier;

            switch (playersSize) {
                case 0:
                    // We hit the first player
                    damageMultiplier = pveUpgrade ? 1.06f : 1f;
                    break;
                case 1:
                    // We hit the second player
                    damageMultiplier = pveUpgrade ? 1.12f : .85f;
                    break;
                default:
                    damageMultiplier = pveUpgrade ? 1.18f : .7f;
                    break;
            }

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
                    critMultiplier,
                    false
            );

            return partOfChainLightning(wp, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersSize + (hasHitTotem ? 1 : 0);
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsEntity wp, CapacitorTotem capacitorTotem) {
        ArmorStand totem = capacitorTotem.getTotem();
        capacitorTotem.pulseDamage();
        if (capacitorTotem.isPveUpgrade()) {
            capacitorTotem.setRadius(capacitorTotem.getRadius() + 0.25);
        }

        Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        wp.playSound(totem.getLocation(), "shaman.chainlightning.impact", 2, 1);

        capacitorTotem.addProc();
    }

    private Optional<CapacitorTotem> getLookingAtTotem(WarlordsEntity warlordsPlayer) {
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.class)
                .filter(abstractTotemBase -> abstractTotemBase.isPlayerLookingAtTotem(warlordsPlayer))
                .findFirst();
    }

    @Override
    public int compareTo(ChainLightning chainLightning) {
        return Integer.compare(this.damageReduction, chainLightning.damageReduction);
    }

    public void setMaxBounces(int maxBounces) {
        this.maxBounces = maxBounces;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float getDamageReductionPerBounce() {
        return damageReductionPerBounce;
    }

    public void setDamageReductionPerBounce(float damageReductionPerBounce) {
        this.damageReductionPerBounce = damageReductionPerBounce;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getRadius() {
        return radius;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public int getMaxBounces() {
        return maxBounces;
    }

    public float getMaxDamageReduction() {
        return maxDamageReduction;
    }

    public void setMaxDamageReduction(float maxDamageReduction) {
        this.maxDamageReduction = maxDamageReduction;
    }
}
