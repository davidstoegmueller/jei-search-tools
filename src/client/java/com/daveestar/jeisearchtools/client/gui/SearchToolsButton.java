package com.daveestar.jeisearchtools.client.gui;

import com.daveestar.jeisearchtools.client.tool.SearchTool;
import com.daveestar.jeisearchtools.client.tool.SearchTools;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class SearchToolsButton {
  private static final Identifier ICON = Identifier.fromNamespaceAndPath(
      "jei-search-tools", "images/magnifier_icon.png");
  private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("widget/button");
  private static final Identifier HOVER_BACKGROUND = Identifier.withDefaultNamespace("widget/button_highlighted");
  private ImmutableRect2i area = ImmutableRect2i.EMPTY;

  public void updateBounds(ImmutableRect2i area) {
    this.area = area;
  }

  public boolean isMouseOver(double x, double y) {
    return area.contains(x, y);
  }

  public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    if (area.isEmpty()) {
      return;
    }

    boolean hovered = isMouseOver(mouseX, mouseY);
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, hovered ? HOVER_BACKGROUND : BACKGROUND,
        area.getX(), area.getY(), area.getWidth(), area.getHeight());
    int iconSize = Math.min(area.getWidth(), area.getHeight()) - 6;
    int iconX = area.getX() + (area.getWidth() - iconSize) / 2;
    int iconY = area.getY() + (area.getHeight() - iconSize) / 2;

    graphics.blit(ICON, iconX, iconY, iconX + iconSize, iconY + iconSize, 0, 1, 0, 1);

    if (hovered) {
      Minecraft client = Minecraft.getInstance();
      int wrapWidth = Math.max(40, Math.min(240, graphics.guiWidth() - 24));
      var lines = tooltip().stream()
          .flatMap(line -> line.getString().isEmpty()
              ? Stream.of(FormattedCharSequence.EMPTY)
              : client.font.split(line, wrapWidth).stream())
          .toList();

      graphics.setTooltipForNextFrame(client.font, lines,
          DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, true);
    }
  }

  private List<Component> tooltip() {
    List<Component> lines = new ArrayList<>();

    for (SearchTool tool : SearchTools.all()) {
      if (!lines.isEmpty()) {
        lines.add(Component.empty());
      }

      lines.add(tool.name().copy().withStyle(ChatFormatting.GOLD));
      tool.help().forEach(line -> lines.add(line.copy().withStyle(ChatFormatting.GRAY)));
    }

    return lines;
  }
}
