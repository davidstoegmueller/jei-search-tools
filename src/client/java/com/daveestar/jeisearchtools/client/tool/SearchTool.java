
package com.daveestar.jeisearchtools.client.tool;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface SearchTool {
  void register();

  Component name();

  List<Component> help();
}
