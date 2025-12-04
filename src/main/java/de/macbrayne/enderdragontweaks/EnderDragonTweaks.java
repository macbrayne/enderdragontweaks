package de.macbrayne.enderdragontweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EnderDragonTweaks implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("enderdragontweaks");

    public static Config config = null;

    @Override
    public void onInitialize() {
        config = load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("enderdragonweaks")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reload").executes(commandContext -> {
                            config = load();
                            commandContext.getSource().sendSuccess(() -> Component.literal("Reloaded config. Health: " + config.health() + ", prevent bed damage:" + config.preventBedDamage()), true);
                            return 1;
                        }))));

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.addMix(
                    Potions.THICK,
                    Items.DIAMOND,
                    Potions.LUCK
            );
        });
    }

    private static Config load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("enderdragontweaks.json");
        Codec<Config> codec = Config.CODEC;
        Gson gson = new GsonBuilder().create();
        if (Files.exists(path)) {
            try {
                var gsonReader = Files.newBufferedReader(path);
                JsonElement element = gson.fromJson(gsonReader, JsonElement.class);
                var result = codec.decode(JsonOps.INSTANCE, element).resultOrPartial(LOGGER::error);
                if (result.isPresent()) {
                    return result.get().getFirst();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Config defaultConfig = new Config(400, true);
        save(defaultConfig);
        return defaultConfig;
    }

    private static void save(Config config) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("enderdragontweaks.json");
        LOGGER.info("Writing config to file {}", path.toAbsolutePath());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (var gsonWriter = Files.newBufferedWriter(path)) {
            var result = Config.CODEC.encodeStart(JsonOps.INSTANCE, config).resultOrPartial(LOGGER::error);
            gson.toJson(result.orElseThrow(), gsonWriter);
        } catch (IOException e) {
            LOGGER.error("Failed to write config to file {}", path.toAbsolutePath(), e);
        }
    }
}
