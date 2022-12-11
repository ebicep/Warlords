package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
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

    protected static final int DESCRIPTION_WIDTH = 165;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    static {
        DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(false);
    }

    //Sneak ability
    protected final List<SecondaryAbility> secondaryAbilities = new ArrayList<>();
    protected int timesUsed = 0;
    protected String name;
    protected float minDamageHeal;
    protected float maxDamageHeal;
    protected float currentCooldown;
    protected float cooldown;
    protected float energyCost;
    protected float critChance;
    protected float critMultiplier;
    protected String description = "";
    protected boolean boosted;
    //pve
    protected boolean pveUpgrade = false;

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost) {
        this(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, 0, 0);
    }

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
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

    public abstract List<Pair<String, String>> getAbilityInfo();

    /**
     * @return whether the ability has to go on cooldown after activation.
     */
    public abstract boolean onActivate(@Nonnull WarlordsEntity wp, Player player);

    public void updateCustomStats(AbstractPlayerClass apc) {

    }

    public void boostSkill(SkillBoosts skillBoost, AbstractPlayerClass abstractPlayerClass) {
        if (!boosted) {
            boosted = true;
            skillBoost.applyBoost.accept(this);
            updateCustomStats(abstractPlayerClass);
        }
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

    public void subtractCooldown(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
            } else {
                currentCooldown -= cooldown;
            }
        }
    }

    public void addCooldown(float cooldown) {
        if (currentCooldown != 0) {
            currentCooldown += cooldown;
        }
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
                                ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + NumberFormat.formatOptionalTenths(getCooldown()) + " seconds",
                        getEnergyCost() == 0 ? null :
                                ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + NumberFormat.formatOptionalTenths(getEnergyCost()),
                        getCritChance() == 0 || getCritChance() == -1 || getCritMultiplier() == 100 ? null :
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritChance()) + "%" + "\n" +
                                        ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%",
                        "",
                        getDescription()
                )
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }

    public String getName() {
        return name;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(float energyCost) {
        this.energyCost = energyCost;
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

    public String getDescription() {
        return WordWrap.wrapWithNewline(ChatColor.GRAY + description, DESCRIPTION_WIDTH);
    }

    public String formatRangeDamage(float min, float max) {
        return formatRange(min, max, ChatColor.RED);
    }

    public String formatRange(float min, float max, ChatColor chatColor) {
        return " " +
                chatColor +
                format(min) +
                ChatColor.GRAY +
                " - " +
                chatColor +
                format(max) +
                ChatColor.GRAY +
                " ";
    }

    public String format(double input) {
        return DECIMAL_FORMAT.format(input);
    }

    public String formatRangeHealing(float min, float max) {
        return formatRange(min, max, ChatColor.GREEN);
    }

    /**
     * @return returns the input divided by 100
     */
    public float convertToPercent(float input) {
        return input / 100f;
    }

    public void runEverySecond() {
    }

    public void runEveryTick() {
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public static class SecondaryAbility {

        public final Predicate<SecondaryAbility> shouldRemove;
        private final Runnable runnable;
        private final boolean hasInfiniteUses;

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
