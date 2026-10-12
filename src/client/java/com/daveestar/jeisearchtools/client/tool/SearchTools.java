package com.daveestar.jeisearchtools.client.tool;

import com.daveestar.jeisearchtools.client.tool.highlight.InventoryHighlightTool;

import java.util.List;

/** The single source of truth for tool registration, state, and help. */
public final class SearchTools {
  private static final InventoryHighlightTool HIGHLIGHTER = new InventoryHighlightTool();
  private static final List<SearchTool> TOOLS = List.of(HIGHLIGHTER);

  private SearchTools() {
  }

  public static List<SearchTool> all() {
    return TOOLS;
  }

  public static boolean isHighlightingEnabled() {
    return HIGHLIGHTER.isEnabled();
  }
}
