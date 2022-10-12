package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RighteousStrike extends AbstractStrikeBase {
    protected int silencedTargetStruck = 0;
    private boolean pveUpgrade = false;
    private int abilityReductionInTicks = 10;

    public RighteousStrike() {
        super("Righteous Strike", 412, 523, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Strike the targeted enemy for" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage. Each strike reduces the duration of your struck target's active ability timers by §6" +
                (abilityReductionInTicks / 20f) + " §7seconds." +
                "\n\nAdditionally, if your struck target is silenced, reduce the cooldown of your Prism Guard by §6" +
                ((abilityReductionInTicks * 1.6f) / 20f) + " §7seconds and reduce their active ability timers by §60.8 §7seconds instead.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Silenced Target Struck", "" + silencedTargetStruck));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false
        );
        //if (finalEvent.isPresent()) {
        if (nearPlayer.getCooldownManager().hasCooldown(SoulShackle.class)) {
            silencedTargetStruck++;
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.ABILITY, (int) (abilityReductionInTicks * 1.6f));
            wp.getBlueAbility().subtractCooldown(0.8f);
            wp.updateBlueItem(player);
        } else {
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.ABILITY, abilityReductionInTicks);
        }
        if (pveUpgrade) {
            knockbackOnHit(wp, nearPlayer, 1.15, 0.2);
            tripleHit(wp, nearPlayer);
        }

        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "rogue.vindicatorstrike.activation", 2, 0.7f);
        Utils.playGlobalSound(location, "shaman.earthenspike.impact", 2, 2);
        randomHitEffect(location, 7, 255, 255, 255);
    }

    public int getAbilityReductionInTicks() {
        return abilityReductionInTicks;
    }

    public void setAbilityReductionInTicks(int abilityReductionInTicks) {
        this.abilityReductionInTicks = abilityReductionInTicks;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
