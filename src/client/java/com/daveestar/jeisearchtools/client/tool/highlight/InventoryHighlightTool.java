package com.daveestar.jeisearchtools.client.tool.highlight;

import com.daveestar.jeisearchtools.client.jei.JEISearchEvents;
import com.daveestar.jeisearchtools.client.jei.JEISearchService;
import com.daveestar.jeisearchtools.client.tool.SearchTool;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InventoryHighlightTool implements SearchTool {
  private static final Logger LOGGER = LoggerFactory.getLogger("jei-search-tools");
  private static final int OVERLAY_COLOR = 0xB0000000;
  private static final int SLOT_SIZE = 16;

  private boolean enabled;

  @Override
  public void register() {
    JEISearchEvents.DOUBLE_CLICK.register(this::_toggle);
    JEISearchEvents.RUNTIME_UNAVAILABLE.register(() -> enabled = false);

    ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
      if (!(screen instanceof AbstractContainerScreen<?>)) {
        return;
      }

      ScreenEvents.afterExtract(screen).register(
          (currentScreen, graphics, mouseX, mouseY, tickProgress) -> {
            _render((AbstractContainerScreen<?>) currentScreen, graphics);
          });
    });
    LOGGER.debug("Inventory highlight listeners registered");
  }

  private void _toggle() {
    if (enabled) {
      enabled = false;
      LOGGER.debug("Inventory highlight disabled by double click");
      return;
    }

    if (JEISearchService.hasSearchText()) {
      JEISearchService.invalidateMatches();
      enabled = true;
      LOGGER.debug("Inventory highlight enabled by double click");
    } else {
      LOGGER.debug("Inventory highlight not enabled: JEI runtime unavailable or search text empty");
    }
  }

  private void _render(
      AbstractContainerScreen<?> screen,
      GuiGraphicsExtractor graphics) {
    if (!enabled || !JEISearchService.hasSearchText()) {
      return;
    }

    var guiProperties = JEISearchService.getGuiProperties(screen);
    if (guiProperties.isEmpty()) {
      return;
    }

    int left = guiProperties.get().guiLeft();
    int top = guiProperties.get().guiTop();

    for (Slot slot : screen.getMenu().slots) {
      if (!slot.isActive()) {
        continue;
      }

      if (slot.hasItem() && JEISearchService.matches(slot.getItem())) {
        continue;
      }

      int x = left + slot.x;
      int y = top + slot.y;

      graphics.fill(
          x,
          y,
          x + SLOT_SIZE,
          y + SLOT_SIZE,
          OVERLAY_COLOR);
    }
  }
}
