package com.dremixam.immediatedeparture.platforms.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.dremixam.immediatedeparture.config.ImmediateDepartureConfigScreen;

/** Fabric-only: exposes the "Configuration" button ModMenu adds to this mod's entry in the "Mods" screen. */
public class ImmediateDepartureModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ImmediateDepartureConfigScreen::build;
    }
}
