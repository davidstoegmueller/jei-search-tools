package com.daveestar.jeisearchtools.client.tool.highlight;

import com.daveestar.jeisearchtools.client.jei.SearchEvents;
import com.daveestar.jeisearchtools.client.jei.SearchService;
import com.daveestar.jeisearchtools.client.tool.SearchTool;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class InventoryHighlightTool implements SearchTool {
  private static final Logger LOGGER = LoggerFactory.getLogger("jei-search-tools");
  private static final int OVERLAY_COLOR = 0xB0000000;
  private static final int MATCH_BORDER_COLOR = 0xFF55FF55;
  private static final int SLOT_SIZE = 16;

  private boolean enabled;

  @Override
  public void register() {
    SearchEvents.DOUBLE_CLICK.register(this::toggle);
    SearchEvents.RUNTIME_UNAVAILABLE.register(() -> enabled = false);

    ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
      if (!(screen instanceof AbstractContainerScreen<?>)) {
        return;
      }

      // Draw above the slots, but before carried items and deferred tooltips.
      ScreenEvents.afterForeground(screen).register(
          (currentScreen, graphics, mouseX, mouseY, tickProgress) -> {
            render((AbstractContainerScreen<?>) currentScreen, graphics);
          });
    });
    LOGGER.debug("Inventory highlight listeners registered");
  }

  @Override
  public Component name() {
    return Component.translatable("jei-search-tools.highlight.name");
  }

  @Override
  public List<Component> help() {
    return List.of(Component.translatable("jei-search-tools.highlight.usage"));
  }

  public boolean isEnabled() {
    return enabled;
  }

  private void toggle() {
    if (!enabled && !SearchService.hasSearchText()) {
      LOGGER.debug("Inventory highlight not enabled: JEI runtime unavailable or search text empty");
      return;
    }
    enabled = !enabled;
    if (enabled) {
      SearchService.invalidateMatches();
    }
    AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
    LOGGER.debug("Inventory highlight {}", enabled ? "enabled" : "disabled");
  }

  private void render(
      AbstractContainerScreen<?> screen,
      GuiGraphicsExtractor graphics) {
    if (!enabled || !SearchService.hasSearchText()) {
      return;
    }

    var guiProperties = SearchService.getGuiProperties(screen);
    if (guiProperties.isEmpty()) {
      return;
    }

    int left = guiProperties.get().guiLeft();
    int top = guiProperties.get().guiTop();

    graphics.nextStratum();

    for (Slot slot : screen.getMenu().slots) {
      if (!slot.isActive()) {
        continue;
      }

      int x = left + slot.x;
      int y = top + slot.y;

      if (slot.hasItem() && SearchService.matches(slot.getItem())) {
        graphics.outline(x - 1, y - 1, SLOT_SIZE + 2, SLOT_SIZE + 2, MATCH_BORDER_COLOR);
      } else {
        graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, OVERLAY_COLOR);
      }
    }
  }
}
