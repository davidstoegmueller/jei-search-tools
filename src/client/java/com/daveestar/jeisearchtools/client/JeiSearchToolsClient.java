package com.daveestar.jeisearchtools.client;

import com.daveestar.jeisearchtools.client.tool.SearchTool;
import com.daveestar.jeisearchtools.client.tool.SearchTools;
import net.fabricmc.api.ClientModInitializer;

public final class JEISearchToolsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    for (SearchTool tool : SearchTools.all()) {
      tool.register();
    }
  }
}
