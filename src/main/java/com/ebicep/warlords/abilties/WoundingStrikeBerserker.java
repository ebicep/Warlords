package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WoundingStrikeBerserker extends AbstractStrikeBase {

    public WoundingStrikeBerserker() {
        super("Wounding Strike", 497, 632, 0, 100, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and §cwounding §7them for §63 §7seconds.\n" +
                "§7A wounded player receives §c40% §7less\n" +
                "§7healing for the duration of the effect.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        if (!(nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class) || nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeDefender.class))) {
            nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
        }
        nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeBerserker.class);
        nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeDefender.class);
        nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<WoundingStrikeBerserker>(
                name,
                "WND",
                WoundingStrikeBerserker.class,
                new WoundingStrikeBerserker(),
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                        nearPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
                    }
                },
                3 * 20
        ) {
            @Override
            public boolean isHealing() {
                return true;
            }

            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .6f;
            }
        });
    }
}