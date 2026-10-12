package com.daveestar.jeisearchtools.client;

import com.daveestar.jeisearchtools.client.tool.SearchTool;
import com.daveestar.jeisearchtools.client.tool.highlight.InventoryHighlightTool;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

public final class JEISearchToolsClient implements ClientModInitializer {
  private final List<SearchTool> tools = List.of(
      new InventoryHighlightTool());

  @Override
  public void onInitializeClient() {
    for (SearchTool tool : tools) {
      tool.register();
    }
  }
}
