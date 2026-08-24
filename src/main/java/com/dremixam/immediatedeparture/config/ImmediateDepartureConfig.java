package com.dremixam.immediatedeparture.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.dremixam.immediatedeparture.ImmediateDeparture;

/**
 * Hand-rolled JSON config, shared identically between Fabric and NeoForge. Lives at
 * {@code config/create_immediate_departure.json}.
 */
public final class ImmediateDepartureConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ImmediateDepartureConfig INSTANCE = new ImmediateDepartureConfig();

    // Remembered from load() so save() doesn't need a config directory passed in.
    private static Path configDir;

    /** Connectivity Condition: whether a currently active Create Schedule must also link the two stations. */
    public boolean requireActiveSchedule = true;

    /** Radius (blocks) around a station within which a player is marked as having discovered it. */
    public double discoveryRadius = 32.0;

    /** Max distance (blocks) a Ticket Validator can be placed from the station it links to. */
    public double ticketValidatorRange = 28.0;

    public static void load(Path configDir) {
        ImmediateDepartureConfig.configDir = configDir;
        Path file = configFile(configDir);
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                ImmediateDepartureConfig loaded = GSON.fromJson(reader, ImmediateDepartureConfig.class);
                if (loaded != null)
                    INSTANCE = loaded;
            } catch (IOException e) {
                ImmediateDeparture.LOGGER.error("Failed to read {}, falling back to defaults", file, e);
            }
        }
        // Re-saved so a freshly created or outdated file ends up with every current key on disk.
        save();
    }

    /** Also called by the in-game config screen's saving runnable. */
    public static void save() {
        Path file = configFile(configDir);
        try {
            Files.createDirectories(configDir);
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            ImmediateDeparture.LOGGER.error("Failed to write {}", file, e);
        }
    }

    private static Path configFile(Path configDir) {
        return configDir.resolve(ImmediateDeparture.MOD_ID + ".json");
    }
}
