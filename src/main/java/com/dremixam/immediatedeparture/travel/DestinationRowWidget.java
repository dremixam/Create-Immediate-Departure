package com.dremixam.immediatedeparture.travel;

import com.simibubi.create.foundation.gui.widget.IconButton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** One clickable destination row: an icon button with the station name alongside it. */
public class DestinationRowWidget extends IconButton {
    private static final int TEXT_COLOR = 0xF2F2EE;

    private final String label;

    public DestinationRowWidget(int x, int y, int width, String label) {
        super(x, y, width, 18, ModIcon.TICKET);
        this.label = label;
    }

    @Override
    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(Minecraft.getInstance().font, label, getX() + 23, getY() + 5, TEXT_COLOR, false);
    }
}
