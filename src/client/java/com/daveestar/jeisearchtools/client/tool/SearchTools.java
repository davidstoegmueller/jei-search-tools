package com.daveestar.jeisearchtools.client.tool;

import com.daveestar.jeisearchtools.client.tool.highlight.InventoryHighlightTool;
import com.daveestar.jeisearchtools.client.tool.calculator.CalculatorTool;

import java.util.List;
import java.util.Optional;

public final class SearchTools {
  private static final InventoryHighlightTool HIGHLIGHTER = new InventoryHighlightTool();
  private static final CalculatorTool CALCULATOR = new CalculatorTool();
  private static final List<SearchTool> TOOLS = List.of(HIGHLIGHTER, CALCULATOR);

  private SearchTools() {
  }

  public static List<SearchTool> all() {
    return TOOLS;
  }

  public static boolean isHighlightingEnabled() {
    return HIGHLIGHTER.isEnabled();
  }

  public static Optional<String> calculatorResult(String input) {
    return CALCULATOR.result(input);
  }
}
