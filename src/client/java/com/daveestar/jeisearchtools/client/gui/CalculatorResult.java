package com.daveestar.jeisearchtools.client.gui;

import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import java.util.Optional;

public final class CalculatorResult {
  private static final int GAP = 8;
  private static final int SCREEN_MARGIN = 4;
  private static final int TEXT_HEIGHT = 8;
  private ImmutableRect2i area = ImmutableRect2i.EMPTY;
  private String fullText = "";

  public void updateBounds(ImmutableRect2i searchArea, Optional<String> result) {
    area = ImmutableRect2i.EMPTY;
    fullText = result.map(value -> "= " + value).orElse("");

    var client = Minecraft.getInstance();
    var screen = client.gui.screen();
    if (searchArea.isEmpty() || fullText.isEmpty() || screen == null) {
      return;
    }

    int desiredWidth = client.font.width(fullText);
    int minimumWidth = Math.min(desiredWidth, client.font.width("..."));
    int availableLeft = searchArea.getX() - GAP - SCREEN_MARGIN;

    if (availableLeft >= minimumWidth) {
      int width = Math.min(desiredWidth, availableLeft);
      area = new ImmutableRect2i(searchArea.getX() - GAP - width, searchArea.getY(), width, searchArea.getHeight());
    } else {
      int width = Math.min(desiredWidth, screen.width - searchArea.getX() - SCREEN_MARGIN);
      int y = searchArea.getY() - searchArea.getHeight() - GAP;

      if (y < SCREEN_MARGIN) {
        y = searchArea.getY() + searchArea.getHeight() + GAP;
      }

      if (width >= minimumWidth && y + searchArea.getHeight() <= screen.height - SCREEN_MARGIN) {
        area = new ImmutableRect2i(searchArea.getX(), y, width, searchArea.getHeight());
      }
    }
  }

  public boolean isMouseOver(double x, double y) {
    return area.contains(x, y);
  }

  public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    if (area.isEmpty()) {
      return;
    }

    var font = Minecraft.getInstance().font;
    String text = fullText;
    int textWidth = area.getWidth();

    if (font.width(text) > textWidth) {
      text = font.plainSubstrByWidth(text, Math.max(0, textWidth - font.width("..."))) + "...";

      if (area.contains(mouseX, mouseY)) {
        graphics.setTooltipForNextFrame(font, Component.literal(fullText), mouseX, mouseY);
      }
    }

    int y = area.getY() + (area.getHeight() - TEXT_HEIGHT) / 2;
    graphics.text(font, text, area.getX() + area.getWidth() - font.width(text), y, 0xFFFFFFFF, true);
  }
}
