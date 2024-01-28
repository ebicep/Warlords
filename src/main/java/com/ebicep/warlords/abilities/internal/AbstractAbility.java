package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.icon.AbilityIcon;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractAbility implements AbilityIcon {

    protected static final int DESCRIPTION_WIDTH = 165;

    protected static void playCooldownReductionEffect(WarlordsEntity warlordsEntity) {
        new GameRunnable(warlordsEntity.getGame()) {
            @Override
            public void run() {
                EffectUtils.displayParticle(
                        Particle.BUBBLE_POP,
                        warlordsEntity.getLocation().add(0, 1.5, 0),
                        10,
                        .5,
                        .25,
                        .5,
                        0
                );
            }
        }.runTaskLater(2);

    }

    //Sneak ability
    protected final List<SecondaryAbility> secondaryAbilities = new ArrayList<>();
    protected int timesUsed = 0;
    protected String name;
    protected float minDamageHeal;
    protected float maxDamageHeal;
    protected float currentCooldown;
    protected FloatModifiable cooldown;
    protected FloatModifiable energyCost;
    protected float critChance;
    protected float critMultiplier;
    protected TextComponent description = Component.empty();
    protected boolean boosted;
    //pve
    protected boolean inPve = false;
    protected boolean pveMasterUpgrade = false;
    protected boolean pveMasterUpgrade2 = false;

    private float rawEnergyCost; // for testing TODO remove

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost) {
        this(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, 0, 0);
    }

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        this(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 0);
    }

    public AbstractAbility(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            float startCooldown
    ) {
        this.name = name;
        this.minDamageHeal = minDamageHeal;
        this.maxDamageHeal = maxDamageHeal;
        this.cooldown = new FloatModifiable(cooldown);
        this.currentCooldown = startCooldown;
        this.energyCost = new FloatModifiable(energyCost);
        this.rawEnergyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        boosted = false;
    }

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, boolean startNoCooldown) {
        this(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, 0, 0, startNoCooldown ? 0 : cooldown);
    }

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float startCooldown) {
        this(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, 0, 0, startCooldown);
    }

    public AbstractAbility(String name, float cooldown, float energyCost) {
        this(name, 0, 0, cooldown, energyCost, 0, 0);
    }

    public AbstractAbility(String name, float cooldown, float energyCost, boolean startNoCooldown) {
        this(name, 0, 0, cooldown, energyCost, 0, 0, startNoCooldown ? 0 : cooldown);
    }

    public AbstractAbility(String name, float cooldown, float energyCost, float startCooldown) {
        this(name, 0, 0, cooldown, energyCost, 0, 0, startCooldown);
    }

    public abstract void updateDescription(Player player);

    public abstract List<Pair<String, String>> getAbilityInfo();

    /**
     * @return whether the ability has to go on cooldown after activation.
     */
    public abstract boolean onActivate(@Nonnull WarlordsEntity wp);

    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return null;
    }

    public void boostSkill(SkillBoosts skillBoost, AbstractPlayerClass abstractPlayerClass) {
        if (!boosted) {
            boosted = true;
            skillBoost.applyBoost.accept(this);
            updateCustomStats(abstractPlayerClass);
        }
    }

    public void updateCustomStats(AbstractPlayerClass apc) {

    }

    public void addTimesUsed() {
        this.timesUsed++;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public float getMinDamageHeal() {
        return minDamageHeal;
    }

    public void setMinDamageHeal(float minDamageHeal) {
        this.minDamageHeal = minDamageHeal;
    }

    public float getMaxDamageHeal() {
        return maxDamageHeal;
    }

    public void setMaxDamageHeal(float maxDamageHeal) {
        this.maxDamageHeal = maxDamageHeal;
    }

    public void multiplyMinMax(float amount) {
        this.minDamageHeal *= amount;
        this.maxDamageHeal *= amount;
    }

    public int getCurrentCooldownItem() {
        return (int) Math.round(currentCooldown + .5);
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(float currentCooldown) {
        if (currentCooldown < 0) {
            this.currentCooldown = 0;
        } else {
            this.currentCooldown = currentCooldown;
        }
    }

    public void addCurrentCooldown(float cooldown) {
        if (currentCooldown != 0) {
            currentCooldown += cooldown;
        }
    }

    public List<SecondaryAbility> getSecondaryAbilities() {
        return secondaryAbilities;
    }

    /**
     * @param ticksDelay   how many ticks before it allows you to activate the ability
     * @param runnable     secondary ability runnable
     * @param infiniteUses should the ability have infinite uses
     * @param shouldRemove remove condition
     */
    public void addSecondaryAbility(
            int ticksDelay,
            Runnable runnable,
            boolean infiniteUses,
            Predicate<SecondaryAbility> shouldRemove
    ) {
        new BukkitRunnable() {
            @Override
            public void run() {
                secondaryAbilities.add(new SecondaryAbility(runnable, infiniteUses, shouldRemove));
            }
        }.runTaskLater(Warlords.getInstance(), ticksDelay);
    }

    public void runSecondAbilities() {
        for (int i = 0; i < secondaryAbilities.size(); i++) {
            SecondaryAbility secondaryAbility = secondaryAbilities.get(i);

            secondaryAbility.runnable().run();
            if (!secondaryAbility.hasInfiniteUses()) {
                secondaryAbilities.remove(i);
                i--;
            }
        }
    }

    public ItemStack getItem() {
        return getItem(getAbilityIcon());
    }

    public ItemStack getItem(ItemStack item) {
        ItemBuilder itemBuilder = new ItemBuilder(item)
                .name(Component.text(getName(), NamedTextColor.GOLD))
                .unbreakable();

        if (getCooldownValue() != 0) {
            itemBuilder.addLore(Component.text("Cooldown: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalTenths(getCooldownValue()) + " seconds", NamedTextColor.AQUA)));
        }
        if (getEnergyCostValue() != 0) {
            itemBuilder.addLore(Component.text("Energy Cost: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalTenths(getEnergyCostValue()), NamedTextColor.YELLOW)));
        }
        if (getCritChance() != 0 && getCritChance() != -1 && getCritMultiplier() != 100) {
            itemBuilder.addLore(Component.text("Crit Chance: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritChance()) + "%", NamedTextColor.RED)));
            itemBuilder.addLore(Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%", NamedTextColor.RED)));
        }
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(getDescription());

        return itemBuilder.get();
    }

    public String getName() {
        return name;
    }

    public float getCooldownValue() {
        return cooldown.getCalculatedValue();
    }

    public float getEnergyCostValue() {
        float calculatedValue = energyCost.getCalculatedValue();
        if (calculatedValue < 0) {
            if (calculatedValue < -50) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("NEGATIVE ENERGY COST - " + getName() + " - " + calculatedValue);
                for (FloatModifiable.FloatModifier modifier : energyCost.getOverridingModifier()) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Override: " + modifier.getLog() + " - " + modifier.getModifier());
                }
                for (FloatModifiable.FloatModifier modifier : energyCost.getAdditiveModifier()) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Additive: " + modifier.getLog() + " - " + modifier.getModifier());
                }
                for (FloatModifiable.FloatModifier modifier : energyCost.getMultiplicativeModifierAdditive()) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Multi Additive: " + modifier.getLog() + " - " + modifier.getModifier());
                }
                for (FloatModifiable.FloatModifier modifier : energyCost.getMultiplicativeModifierMultiplicative()) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Multi Multi: " + modifier.getLog() + " - " + modifier.getModifier());
                }
            }
            return 0;
        }
        if (calculatedValue > 300) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("HIGH ENERGY COST - " + getName() + " - " + calculatedValue);
            for (FloatModifiable.FloatModifier modifier : energyCost.getOverridingModifier()) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Override: " + modifier.getLog() + " - " + modifier.getModifier());
            }
            for (FloatModifiable.FloatModifier modifier : energyCost.getAdditiveModifier()) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Additive: " + modifier.getLog() + " - " + modifier.getModifier());
            }
            for (FloatModifiable.FloatModifier modifier : energyCost.getMultiplicativeModifierAdditive()) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Multi Additive: " + modifier.getLog() + " - " + modifier.getModifier());
            }
            for (FloatModifiable.FloatModifier modifier : energyCost.getMultiplicativeModifierMultiplicative()) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Multi Multi: " + modifier.getLog() + " - " + modifier.getModifier());
            }
            energyCost = new FloatModifiable(rawEnergyCost);
            calculatedValue = energyCost.getCalculatedValue();
        }
        return calculatedValue;
    }

    public float getCritChance() {
        return critChance;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(float critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    public List<Component> getDescription() {
        return WordWrap.wrap(Component.empty().color(NamedTextColor.GRAY).append(description), DESCRIPTION_WIDTH);
    }

    public FloatModifiable getCooldown() {
        return cooldown;
    }

    public FloatModifiable getEnergyCost() {
        return energyCost;
    }

    @Deprecated
    public void setEnergyCost(float energyCost) {
        this.energyCost.setBaseValue(energyCost);
    }

    public Component formatRangeDamage(float min, float max) {
        return formatRange(min, max, NamedTextColor.RED);
    }

    public Component formatRange(float min, float max, NamedTextColor textColor) {
        return Component.text(" ", NamedTextColor.GRAY)
                        .append(Component.text(format(min), textColor))
                        .append(Component.text(" - "))
                        .append(Component.text(format(max), textColor))
                        .append(Component.text(" "));
    }

    public String format(double input) {
        return NumberFormat.formatOptionalTenths(input);
    }

    public String formatHundredths(double input) {
        return NumberFormat.formatOptionalHundredths(input);
    }

    public Component formatRangeHealing(float min, float max) {
        return formatRange(min, max, NamedTextColor.GREEN);
    }

    /**
     * @return returns the input divided by 100
     */
    public float convertToPercent(float input) {
        return input / 100f;
    }

    /**
     * @return returns the input subtracted from 100 and then divided by 100
     */
    public float convertToDivisionDecimal(float input) {
        return (100 - input) / 100f;
    }

    /**
     * @return returns the input divided by 100 and then added on 1
     */
    public float convertToMultiplicationDecimal(float input) {
        return 1 + (input / 100f);
    }

    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
    }

    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        cooldown.tick();
        energyCost.tick();
        if (getCooldownValue() > 0) {
            subtractCurrentCooldownForce(.05f);
        }
        checkSecondaryAbilities();
    }

    public void subtractCurrentCooldownForce(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
            } else {
                currentCooldown -= cooldown;
            }
        }
    }

    public void checkSecondaryAbilities() {
        secondaryAbilities.removeIf(secondaryAbility -> secondaryAbility.shouldRemove().test(secondaryAbility));
    }

    public void subtractCurrentCooldown(float cooldown) {
        if (inPve && this instanceof CanReduceCooldowns canReduceCooldowns && canReduceCooldowns.canReduceCooldowns()) {
            return;
        }
        subtractCurrentCooldownForce(cooldown);
    }

    public boolean isInPve() {
        return inPve;
    }

    public void setInPve(boolean inPve) {
        this.inPve = inPve;
    }

    public boolean isPveMasterUpgrade() {
        return pveMasterUpgrade;
    }

    public void setPveMasterUpgrade(boolean pveMasterUpgrade) {
        this.pveMasterUpgrade = pveMasterUpgrade;
    }

    public boolean isPveMasterUpgrade2() {
        return pveMasterUpgrade2;
    }

    public void setPveMasterUpgrade2(boolean pveMasterUpgrade2) {
        this.pveMasterUpgrade2 = pveMasterUpgrade2;
    }

    public record SecondaryAbility(Runnable runnable, boolean hasInfiniteUses, Predicate<SecondaryAbility> shouldRemove) {

    }
}
