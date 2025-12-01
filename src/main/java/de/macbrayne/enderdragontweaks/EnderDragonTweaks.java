package de.macbrayne.enderdragontweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

public class EnderDragonTweaks implements ModInitializer {
    public static final GameRules.Key<GameRules.IntegerValue> HEALTH =
            GameRuleRegistry.register("enderDragonHealth", GameRules.Category.MOBS, GameRuleFactory.createIntRule(400));

    @Override
    public void onInitialize() {

    }
}
