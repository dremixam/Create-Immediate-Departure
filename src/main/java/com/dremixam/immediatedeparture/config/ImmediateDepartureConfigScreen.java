package com.dremixam.immediatedeparture.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

//? if fabric {
/*import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
*///? }
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Builds the in-game config screen, shared by both loaders' entry points: {@code
 * ImmediateDepartureModMenu} on Fabric and {@code ImmediateDepartureNeoForgeClient} on NeoForge.
 */
//? if fabric {
/*@Environment(EnvType.CLIENT)
*///? }
public final class ImmediateDepartureConfigScreen {
    private ImmediateDepartureConfigScreen() {
    }

    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("immediate_departure.config.title"))
            .setSavingRunnable(ImmediateDepartureConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("immediate_departure.config.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
            .startBooleanToggle(Component.translatable("immediate_departure.config.require_active_schedule"),
                ImmediateDepartureConfig.INSTANCE.requireActiveSchedule)
            .setDefaultValue(true)
            .setTooltip(Component.translatable("immediate_departure.config.require_active_schedule.tooltip"))
            .setSaveConsumer(value -> ImmediateDepartureConfig.INSTANCE.requireActiveSchedule = value)
            .build());

        general.addEntry(entryBuilder
            .startDoubleField(Component.translatable("immediate_departure.config.discovery_radius"),
                ImmediateDepartureConfig.INSTANCE.discoveryRadius)
            .setDefaultValue(32.0)
            .setMin(0.0)
            .setTooltip(Component.translatable("immediate_departure.config.discovery_radius.tooltip"))
            .setSaveConsumer(value -> ImmediateDepartureConfig.INSTANCE.discoveryRadius = value)
            .build());

        general.addEntry(entryBuilder
            .startDoubleField(Component.translatable("immediate_departure.config.ticket_validator_range"),
                ImmediateDepartureConfig.INSTANCE.ticketValidatorRange)
            .setDefaultValue(28.0)
            .setMin(0.0)
            .setTooltip(Component.translatable("immediate_departure.config.ticket_validator_range.tooltip"))
            .setSaveConsumer(value -> ImmediateDepartureConfig.INSTANCE.ticketValidatorRange = value)
            .build());

        return builder.build();
    }
}
