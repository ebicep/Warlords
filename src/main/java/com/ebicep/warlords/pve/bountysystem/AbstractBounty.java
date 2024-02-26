package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.bountysystem.costs.BountyCost;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.RewardSpendable;
import com.ebicep.warlords.pve.rewards.types.BountyReward;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.pve.bountysystem.BountyUtils.BOUNTY_COLLECTION_INFO;

public abstract class AbstractBounty implements Listener, RewardSpendable, BountyCost {

    protected long value;
    private boolean started = false;

    // values for bounties to know who the owner is for events
    @Transient
    protected DatabasePlayer databasePlayer;
    @Transient
    protected UUID uuid;

    public void init(DatabasePlayer databasePlayer) {
        this.databasePlayer = databasePlayer;
        this.uuid = databasePlayer.getUuid();
        log("initializing bounty");
        this.register();
    }

    protected void register() {
        log("registering bounty");
        this.unregister(); // precaution
        Bukkit.getPluginManager().registerEvents(this, Warlords.getInstance());
        log("done registering bounty");
    }

    public void unregister() {
        log("unregistering bounty");
        HandlerList.unregisterAll(this);
    }

    private void log(String message) {
        ChatUtils.MessageType.BOUNTIES.sendMessage(message + ": " + getDebugInfo());
    }

    private String getDebugInfo() {
        String name = databasePlayer == null ? "null" : databasePlayer.getName();
        return this.getClass().getSimpleName() + " for " + name + "(" + uuid + ")";
    }

    public ItemBuilder getItemWithProgress() {
        ItemBuilder itemBuilder = getItem();

        List<Component> progress = getProgress();
        itemBuilder.addLore(Component.empty());
        if (started) {
            if (progress == null) {
                itemBuilder.addLore(Component.text("Click to Claim!", NamedTextColor.GREEN));
            } else {
                itemBuilder.addLore(progress);
            }
        } else {
            itemBuilder.addLore(PvEUtils.getCostLore(getCost(), false));
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("Click to Start!", NamedTextColor.GREEN)
            );
        }

        if (started && progress == null) {
            itemBuilder.enchant(Enchantment.OXYGEN, 1);
        }
        return itemBuilder;
    }

    public ItemBuilder getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(started ? Material.PAPER : Material.MAP)
                .name(Component.text(getName(), NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.GRAY), 160));
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Rewards:", NamedTextColor.GRAY)
        );
        getCurrencyReward().forEach((currencies, aLong) -> itemBuilder.addLore(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));
        return itemBuilder;
    }

    /**
     * @return Progress display - if null, bounty is completed
     */
    @Nullable
    public List<Component> getProgress() {
        if (value >= getTarget()) {
            return null;
        }
        if (getTarget() == 1) { // for boolean bounties - if target is actually 1 then just override
            return getNoProgress();
        }
        return Collections.singletonList(Component.textOfChildren(
                Component.text("Progress: ", NamedTextColor.GRAY),
                Component.text(NumberFormat.addCommaAndRound(value), NamedTextColor.GOLD),
                Component.text("/", NamedTextColor.AQUA),
                Component.text(NumberFormat.addCommaAndRound(getTarget()), NamedTextColor.GOLD)
        ));
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getTarget();

    protected List<Component> getNoProgress() {
        return Collections.singletonList(Component.textOfChildren(
                Component.text("Progress: ", NamedTextColor.GRAY),
                Component.text("✖", NamedTextColor.RED)
        ));
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void claim(DatabasePlayer databasePlayer, PlayersCollections collection) {
        claim(databasePlayer, collection, collection.name);
    }

    public void claim(DatabasePlayer databasePlayer, PlayersCollections collection, String bountyInfoName) {
        DatabaseGameEvent gameEvent = DatabaseGameEvent.currentGameEvent;
        boolean isEventBounty = this instanceof EventCost && gameEvent != null;

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        List<AbstractBounty> activeBounties;
        Set<Bounty> excludeBounties = new HashSet<>();
        excludeBounties.add(getBounty());
        EventMode eventMode;
        EventMode generalEventMode;
        int bountiesCompleted = pveStats.getBountiesCompleted();
        if (isEventBounty) {
            GameEvents event = gameEvent.getEvent();
            DatabasePlayerPvEEventStats eventStats = pveStats.getEventStats();
            eventMode = event.eventsStatsFunction.apply(eventStats).get(gameEvent.getStartDateSecond());
            if (eventMode == null) {
                ChatUtils.MessageType.BOUNTIES.sendErrorMessage("Could not claim bounty - Event mode not found");
                return;
            }
            generalEventMode = event.generalEventFunction.apply(eventStats);

            eventMode.addBountiesCompleted();
            generalEventMode.addBountiesCompleted();

            activeBounties = eventMode.getActiveEventBounties();
            excludeBounties.addAll(eventMode.getCompletedBounties().keySet());

            bountiesCompleted = eventMode.getBountiesCompleted();
        } else {
            eventMode = null;
            generalEventMode = null;
            activeBounties = pveStats.getActiveBounties();
            excludeBounties.addAll(pveStats.getCompletedBounties().keySet());
        }
        excludeBounties.addAll(activeBounties.stream().filter(Objects::nonNull).map(AbstractBounty::getBounty).collect(Collectors.toSet()));
        pveStats.addBountiesCompleted();
        int replaceIndex = activeBounties.indexOf(this);
        int maxBounties = BOUNTY_COLLECTION_INFO.get(bountyInfoName).maxBounties();
        AbstractBounty replacementBounty = null;
        if (bountiesCompleted <= maxBounties) {
            Bounty randomBounty = BountyUtils.getRandomBounty(bountyInfoName, excludeBounties);
            if (randomBounty != null) {
                replacementBounty = randomBounty.create.get();
            }
        }
        activeBounties.set(replaceIndex, replacementBounty);

        if (collection != PlayersCollections.LIFETIME) {
            pveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
        }
        DatabaseManager.updatePlayer(databasePlayer.getUuid(), lifetimeDatabasePlayer -> {
            DatabasePlayerPvE lifetimePveStats = lifetimeDatabasePlayer.getPveStats();
            lifetimePveStats.getBountyRewards().add(new BountyReward(getCurrencyReward(), getBounty()));
            lifetimePveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
            if (isEventBounty) {
                eventMode.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
                generalEventMode.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
            }
        });
    }

    public abstract Bounty getBounty();

}
