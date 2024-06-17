package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.function.Consumer;

public interface CustomInstanceFlags {

    /**
     * @param floatModifiableConsumer Consumes a float modifiable object, its modifier its instantly removed after use
     * @param flag                    custom flag so implementation knows to consume or not
     */
    record Valued(Consumer<FloatModifiable> floatModifiableConsumer, CustomInstanceFlags.Valued.Flag flag) implements CustomInstanceFlags {
        public enum Flag {
            TD_PHYSICAL_RES_REDUCTION,
            TD_MAGIC_RES_REDUCTION,
        }
    }

}
