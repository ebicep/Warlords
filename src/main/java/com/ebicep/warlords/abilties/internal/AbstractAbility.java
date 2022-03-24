package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.SkillBoosts;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractAbility {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    static {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    protected String name;
    protected float minDamageHeal;
    protected float maxDamageHeal;
    protected float currentCooldown;
    protected float cooldown;
    protected int energyCost;
    protected int critChance;
    protected int critMultiplier;
    protected String description;
    protected boolean boosted;

    //Sneak ability
    protected final List<SecondaryAbility> secondaryAbilities = new ArrayList<>();

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        this.name = name;
        this.minDamageHeal = minDamageHeal;
        this.maxDamageHeal = maxDamageHeal;
        this.cooldown = cooldown;
        this.energyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        boosted = false;
    }

    public abstract void updateDescription(Player player);

    /**
     * @param wp
     * @param player
     * @return whether the ability has to go on cooldown after activation.
     */
    public abstract boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player);

    public void boostSkill(SkillBoosts skillBoost, AbstractPlayerClass abstractPlayerClass) {
        if (!boosted) {
            boosted = true;
            skillBoost.applyBoost.accept(this);
            if (abstractPlayerClass != null && this instanceof ArcaneShield) {
                ArcaneShield arcaneShield = ((ArcaneShield) this);
                arcaneShield.setMaxShieldHealth((int) (abstractPlayerClass.getMaxHealth() * (arcaneShield.getShieldPercentage() / 100f)));
            }
        }
    }

    public String getName() {
        return name;
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

    public int getCurrentCooldownItem() {
        return (int) Math.round(currentCooldown + .5);
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(float currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public void subtractCooldown(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
            } else {
                currentCooldown -= cooldown;
            }
        }
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(int energyCost) {
        this.energyCost = energyCost;
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(int critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public List<SecondaryAbility> getSecondaryAbilities() {
        return secondaryAbilities;
    }

    public void addSecondaryAbility(Runnable runnable, boolean infiniteUses, Predicate<SecondaryAbility> shouldRemove) {
        secondaryAbilities.add(new SecondaryAbility(runnable, infiniteUses, shouldRemove));
    }

    public void runSecondAbilities() {
        for (int i = 0; i < secondaryAbilities.size(); i++) {
            SecondaryAbility secondaryAbility = secondaryAbilities.get(i);

            secondaryAbility.getRunnable().run();
            if (!secondaryAbility.isHasInfiniteUses()) {
                secondaryAbilities.remove(i);
                i--;
            }
        }
    }

    public void checkSecondaryAbilities() {
        secondaryAbilities.removeIf(secondaryAbility -> secondaryAbility.getShouldRemove().test(secondaryAbility));
    }

    public ItemStack getItem(ItemStack baseItem) {
        return new ItemBuilder(baseItem)
                .name(ChatColor.GOLD + getName())
                .lore(
                        getCooldown() == 0 ? null :
                                ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + NumberFormat.formatOptionalHundredths(getCooldown()) + " seconds",
                        getEnergyCost() == 0 || getEnergyCost() == -120 ? null :
                                ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + getEnergyCost(),
                        getCritChance() == 0 || getCritChance() == -1 || getCritMultiplier() == 100 ? null :
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + getCritChance() + "%" + "\n" +
                                        ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + getCritMultiplier() + "%",
                        "",
                        getDescription()
                )
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }

    public String format(double input) {
        return decimalFormat.format(input);
    }

    public void runEverySecond() {
    }

    public static class SecondaryAbility {

        private final Runnable runnable;
        private final boolean hasInfiniteUses;
        public final Predicate<SecondaryAbility> shouldRemove;

        public SecondaryAbility(Runnable runnable, boolean hasInfiniteUses, Predicate<SecondaryAbility> shouldRemove) {
            this.runnable = runnable;
            this.hasInfiniteUses = hasInfiniteUses;
            this.shouldRemove = shouldRemove;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public boolean isHasInfiniteUses() {
            return hasInfiniteUses;
        }

        public Predicate<SecondaryAbility> getShouldRemove() {
            return shouldRemove;
        }
    }
}
