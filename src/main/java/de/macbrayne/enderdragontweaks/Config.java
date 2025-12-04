package de.macbrayne.enderdragontweaks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Config(int health, boolean preventBedDamage) {
    public static final Codec<Config> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("health").forGetter(Config::health),
                    Codec.BOOL.fieldOf("preventBedDamage").forGetter(Config::preventBedDamage)
            ).apply(instance, Config::new)
    );
}
