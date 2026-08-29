package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.abilities.internal.Ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AbilityCombinationBans {

    private static final List<Set<Ability<?>>> BANNED_COMBINATIONS = List.of(
            Set.of(Ability.ORBS_OF_LIFE, Ability.FALLEN_SOULS)
    );

    private AbilityCombinationBans() {
    }

    public static boolean violates(Collection<Ability<?>> loadout) {
        for (Set<Ability<?>> bannedCombination : BANNED_COMBINATIONS) {
            if (loadout.containsAll(bannedCombination)) {
                return true;
            }
        }
        return false;
    }

    public static List<Ability<?>> filterCandidates(List<Ability<?>> candidates, Collection<Ability<?>> loadoutSoFar) {
        if (candidates.isEmpty() || loadoutSoFar.isEmpty()) {
            return candidates;
        }
        List<Ability<?>> filtered = new ArrayList<>(candidates.size());
        for (Ability<?> candidate : candidates) {
            Set<Ability<?>> withCandidate = new HashSet<>(loadoutSoFar);
            withCandidate.add(candidate);
            if (!violates(withCandidate)) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

}
