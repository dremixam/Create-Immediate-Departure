package com.dremixam.immediatedeparture.travel;

import java.util.List;
import java.util.UUID;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;

import com.dremixam.immediatedeparture.travel.network.DestinationOption;
import com.dremixam.immediatedeparture.travel.network.TravelRequestMessage;
import com.dremixam.immediatedeparture.validator.ImmediateDepartureBlocks;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

/**
 * Client-side destination picker opened from a station. Every entry was already
 * discovery/connectivity/schedule-checked server-side before this screen was sent; clicking one
 * fires a {@link TravelRequestMessage}, which re-validates before moving the player.
 */
public class DestinationScreen extends AbstractSimiScreen {
    private static final AllGuiTextures BACKGROUND = AllGuiTextures.SCHEDULE;
    private static final int CONTENT_X = 24;
    private static final int CONTENT_Y = 22;
    private static final int CONTENT_WIDTH = 204;
    private static final int CONTENT_BOTTOM = 189;
    private static final int ROW_HEIGHT = 20;
    private static final int VISIBLE_ROWS = (CONTENT_BOTTOM - CONTENT_Y) / ROW_HEIGHT;
    private static final int TITLE_COLOR = 0x505050;
    private static final int TEXT_COLOR = 0xF2F2EE;
    private static final int FADE_COLOR = 0x77000000;
    private static final int FADE_HEIGHT = 10;

    private final UUID originId;
    private final List<DestinationOption> destinations;
    private List<FormattedCharSequence> emptyMessageLines = List.of();
    private int scrollIndex;

    public DestinationScreen(UUID originId, String originName, List<DestinationOption> destinations) {
        super(Component.translatable("immediate_departure.destination_screen.title", originName));
        this.originId = originId;
        this.destinations = destinations;
    }

    @Override
    protected void init() {
        setWindowSize(BACKGROUND.getWidth(), BACKGROUND.getHeight());
        super.init();
        clearWidgets();

        if (destinations.isEmpty())
            emptyMessageLines = font.split(Component.translatable("immediate_departure.no_destinations"), CONTENT_WIDTH);

        int maxScroll = Math.max(0, destinations.size() - VISIBLE_ROWS);
        scrollIndex = Mth.clamp(scrollIndex, 0, maxScroll);

        int rowY = guiTop + CONTENT_Y;
        int end = Math.min(destinations.size(), scrollIndex + VISIBLE_ROWS);
        for (int i = scrollIndex; i < end; i++) {
            DestinationOption destination = destinations.get(i);
            DestinationRowWidget row = new DestinationRowWidget(guiLeft + CONTENT_X, rowY, CONTENT_WIDTH, destination.name());
            row.withCallback(() -> travelTo(destination));
            addRenderableWidget(row);
            rowY += ROW_HEIGHT;
        }

        IconButton closeButton = new IconButton(
            guiLeft + BACKGROUND.getWidth() - 42, guiTop + BACKGROUND.getHeight() - 30, AllIcons.I_CONFIRM);
        closeButton.withCallback(this::onClose);
        addRenderableWidget(closeButton);
    }

    //? if >=1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (destinations.size() <= VISIBLE_ROWS)
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        scrollIndex -= (int) Math.signum(scrollY);
        init();
        return true;
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (destinations.size() <= VISIBLE_ROWS)
            return super.mouseScrolled(mouseX, mouseY, scrollAmount);

        scrollIndex -= (int) Math.signum(scrollAmount);
        init();
        return true;
    }
    *///? }

    private void travelTo(DestinationOption destination) {
        new TravelRequestMessage(originId, destination.id()).sendToServer();
        onClose();
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        BACKGROUND.render(graphics, guiLeft, guiTop);

        FormattedCharSequence titleText = title.getVisualOrderText();
        int center = guiLeft + (BACKGROUND.getWidth() - 8) / 2;
        graphics.drawString(font, titleText, center - font.width(titleText) / 2, guiTop + 4, TITLE_COLOR, false);

        if (destinations.isEmpty()) {
            int lineY = guiTop + CONTENT_Y;
            for (FormattedCharSequence line : emptyMessageLines) {
                graphics.drawCenteredString(font, line, guiLeft + BACKGROUND.getWidth() / 2, lineY, TEXT_COLOR);
                lineY += ROW_HEIGHT;
            }
        }

        if (scrollIndex > 0)
            graphics.fillGradient(guiLeft + 16, guiTop + CONTENT_Y - FADE_HEIGHT, guiLeft + 236, guiTop + CONTENT_Y,
                FADE_COLOR, 0x00000000);
        if (scrollIndex + VISIBLE_ROWS < destinations.size())
            graphics.fillGradient(guiLeft + 16, guiTop + CONTENT_BOTTOM - FADE_HEIGHT, guiLeft + 236, guiTop + CONTENT_BOTTOM,
                0x00000000, FADE_COLOR);

        renderTicketValidatorPreview(graphics);
    }

    /**
     * Renders the Ticket Validator's default block state as a preview, rotated to face the panel.
     * Known issue: doesn't render on Fabric (works on NeoForge); root cause unconfirmed.
     */
    private void renderTicketValidatorPreview(GuiGraphics graphics) {
        GuiGameElement.of(ImmediateDepartureBlocks.TICKET_VALIDATOR.get().defaultBlockState())
            .<GuiGameElement.GuiRenderBuilder>at(guiLeft + BACKGROUND.getWidth() - 5, guiTop + BACKGROUND.getHeight() - 5, -100)
            .rotateBlock(30, 135, 0)
            .scale(50)
            .render(graphics);
    }
}
