package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.ThunderLord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;

import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.util.ClassesSkillBoosts.*;
import java.util.function.Supplier;
import org.bukkit.OfflinePlayer;

public enum Classes {
    PYROMANCER("Pyromancer", Pyromancer::new, "§7A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.", FIREBALL, FLAME_BURST),
    CRYOMANCER("Cryomancer", Cryomancer::new, "§7A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.", FROST_BOLT, FREEZING_BREATH),
    AQUAMANCER("Aquamancer", Aquamancer::new, "§7A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies.", WATER_BOLT, WATER_BREATH),

    BERSERKER("Berserker", Berserker::new, "§7A damage-oriented Warrior specialization with a lust for blood and anger issues.", WOUNDING_STRKE, SEISMIC_WAVE, GROUND_SLAM),
    DEFENDER("Defender", Defender::new, "§7A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.", WOUNDING_STRKE, SEISMIC_WAVE, GROUND_SLAM),
    REVENANT("Revenant", Revenant::new, "§7A support-oriented Warrior specialization that can give allies a second chance of life.", CRIPPLING_STRIKE, ORBS_OF_LIFE),

    AVENGER("Avenger", Avenger::new, "§7A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.", AVENGER_STRIKE, CONSECRATE),
    CRUSADER("Crusader", Crusader::new, "§7A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.", CRUSADER_STRIKE, CONSECRATE),
    PROTECTOR("Protector", Protector::new, "§7A healing-oriented Paladin specialization that converts damage into healing for his allies and has access to greater healing abilities.", PROTECTOR_STRIKE, HOLY_RADIANCE, HAMMER_OF_LIGHT),

    THUNDERLORD("Thunderlord", ThunderLord::new, "§7A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.", LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON),
    SPIRITGUARD("Spiritguard", Spiritguard::new, "§7A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.", FALLEN_SOULS, SPIRIT_LINK),
    EARTHWARDEN("Earthwarden", Earthwarden::new, "§7A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.", EARTHEN_SPIKE, BOULDER, CHAIN_HEAL),

    ;

    public final String name;
    public final Supplier<PlayerClass> create;
    public final String description;
    public final List<ClassesSkillBoosts> skillBoosts;

    Classes(String name, Supplier<PlayerClass> create, String description, ClassesSkillBoosts... skillBoosts) {
        this.name = name;
        this.create = create;
        this.description = description;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    /**
     * 
     * @param player
     * @return
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass()} instead
     */
    @Deprecated
    public static Classes getSelected(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).selectedClass();
    }

    /**
     * 
     * @param player
     * @param selectedClass
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(selectedClass)} instead
     */
    @Deprecated
    public static void setSelected(OfflinePlayer player, Classes selectedClass) {
        Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(selectedClass);
    }
    
    /**
     * 
     * @param player
     * @return
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts()} instead
     */
    @Deprecated
    public static ClassesSkillBoosts getSelectedBoost(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts();
    }

    /**
     * 
     * @param player
     * @param selectedBoost
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts(selectedBoost)} instead
     */
    @Deprecated
    public static void setSelectedBoost(OfflinePlayer player, ClassesSkillBoosts selectedBoost) {
        Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts(selectedBoost);
    }
}