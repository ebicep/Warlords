package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.sets.*;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.java.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public enum NewItemsSetBonus implements SetBonus {

    AMBULANCE(new Ambulance()),
    BATTERY(new Battery()),
    BELLICOSE(new Bellicose()),
    BLOODLETTING_BLADE(new BloodlettingBlade()),
    BRITTLE_CROWN(new BrittleCrown()),
    BULWARK(new Bulwark()),
    CENTURION(new Centurion()),
    CHARM(new Charm()),
    CROSSFIRE(new Crossfire()),
    CROWN_OF_THORNS(new CrownOfThorns()),
    CELL(new Cell()),
    DETONATOR(new Detonator()),
    DURABLE(new Durable()),
    DECAY(new Decay()),
    DIABOLICAL(new Diabolical()),
    ECHO_OF_RUIN(new EchoOfRuin()),
    ENERGIZE(new Energize()),
    ENCUMBER(new Encumber()),
    EXERGIES(new Exergis()),
    FORSAKEN_FLUX(new ForsakenFlux()),
    FROSTVEIL(new Frostveil()),
    GAMBLER(new Gambler()),
    GHOSTLY(new Ghostly()),
    GENESIS(new Genesis()),
    GOLD_DIGGER(new GoldDigger()),
    GRAVEMIND(new Gravemind()),
    HAND_OF_THE_CORPSE(new HandOfTheCorpse()),
    HEART_OF_GLASS(new HeartOfGlass()),
    HOURGLASS(new Hourglass()),
    ILLUMINATED_PRISM(new IlluminatedPrism()),
    INNER_FLAME(new InnerFlame()),
    IRON_CHAINS(new IronChains()),
    IMMUTABLE_WILL(new ImmutableWill()),
    LEAN(new Lean()),
    MADRAKAN(new Madrakan()),
    MOONVEIL(new Moonveil()),
    MOURNSONG_VIAL(new MournsongVial()),
    MULTIPLY(new Multiply()),
    OATHKEEPER(new Oathkeeper()),
    OBELISK(new Obelisk()),
    OCEAN_LIGHT(new OceanLight()),
    OLYMPIC(new Olympic()),
    OMAMORI(new Omamori()),
    OMEN(new Omen()),
    OVERFLOW(new Overflow()),
    PHOENIX(new Phoenix()),
    PULSE_OF_AEONS(new PulseOfAeons()),
    PILLAGE(new Pillage()),
    REGENERATE(new Regenerate()),
    RANDOM_EPIC(new RandomEpic()),
    RANDOM_RARE(new RandomRare()),
    RANDOM_COMMON(new RandomCommon()),
    SACRIFICE(new Sacrifice()),
    SANGUINEOUS(new Sanguineous()),
    SHARPSHOOTER(new Sharpshooter()),
    SHIELD_GATE(new ShieldGate()),
    SOOTHSAYER(new Soothsayer()),
    SOULFLAME(new Soulflame()),
    SOULFORGED(new Soulforged()),
    SPELUNKER(new Spelunker()),
    STONELASH(new Stonelash()),
    STRETCH(new Stretch()),
    SUMMONER(new Summoner()),
    SWIFT(new Swift()),
    SYNAPTIC_OVERLOAD(new SynapticOverload()),
    THRONE_OF_THE_UNDEAD(new ThroneOfTheUndead()),
    TRANSFERENCE(new Transference()),
    TRUE_PURPOSE(new TruePurpose()),
    VIAL(new Vial()),
    VOIDCARVER(new Voidcarver()),
    WELLSPRING(new Wellspring()),

    ;

    public static final NewItemsSetBonus[] VALUES = values();
    public static Map<NewItemTier, Set<NewItemsSetBonus>> BY_TIER;
    private final SetBonus setBonus;

    NewItemsSetBonus(SetBonus setBonus) {
        this.setBonus = setBonus;
    }

    @Override
    public boolean isNoBonus() {
        return setBonus.isNoBonus();
    }

    @Override
    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    @Override
    public String getName() {
        return setBonus.getName();
    }

    @Override
    public List<NewItemsSlot> getSlots() {
        return setBonus.getSlots();
    }

    @Override
    public Map<NewItemAttribute, Float> getAttributes() {
        return setBonus.getAttributes();
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges() {
        return setBonus.getBonusAttributeRanges();
    }

    @Override
    public void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges) {
        setBonus.setBonusAttributeRanges(bonusAttributeRanges);
    }

    @Override
    public void init() {
        setBonus.init();
    }

    @Override
    public Bonus create() {
        return setBonus.create();
    }

    @Override
    public String getConfigFieldName() {
        return setBonus.getConfigFieldName();
    }

    @Override
    public List<Object> getVariables() {
        return setBonus.getVariables();
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getRerollCost() {
        return setBonus.getRerollCost();
    }

    @Override
    public void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost) {
        setBonus.setRerollCost(rerollCost);
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getLockScrollRerollCost() {
        return setBonus.getLockScrollRerollCost();
    }

    @Override
    public void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost) {
        setBonus.setLockScrollRerollCost(lockScrollRerollCost);
    }

}
