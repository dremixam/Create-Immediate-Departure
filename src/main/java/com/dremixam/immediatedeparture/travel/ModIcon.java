package com.dremixam.immediatedeparture.travel;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import com.dremixam.immediatedeparture.ImmediateDeparture;

/**
 * Our own 16x16 icons for Create-styled widgets. Lives at
 * assets/create_immediate_departure/textures/gui/&lt;name&gt;.png.
 */
public enum ModIcon implements ScreenElement {
    TICKET("icon_ticket");

    private final ResourceLocation texture;

    ModIcon(String name) {
        //? if >=1.21.1 {
        this.texture = ResourceLocation.fromNamespaceAndPath(ImmediateDeparture.MOD_ID, "textures/gui/" + name + ".png");
        //? } else {
        /*this.texture = ResourceLocation.tryBuild(ImmediateDeparture.MOD_ID, "textures/gui/" + name + ".png");
        *///? }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
    }
}
