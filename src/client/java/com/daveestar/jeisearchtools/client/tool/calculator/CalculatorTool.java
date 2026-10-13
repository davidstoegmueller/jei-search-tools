package com.daveestar.jeisearchtools.client.tool.calculator;

import com.daveestar.jeisearchtools.client.tool.SearchTool;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public final class CalculatorTool implements SearchTool {
  private String cachedInput;
  private Optional<String> cachedResult = Optional.empty();

  @Override
  public void register() {
  }

  @Override
  public Component name() {
    return Component.translatable("jei-search-tools.calculator.name");
  }

  @Override
  public List<Component> help() {
    return List.of(
        Component.translatable("jei-search-tools.calculator.usage"),
        Component.translatable("jei-search-tools.calculator.functions"),
        Component.translatable("jei-search-tools.calculator.percent"));
  }

  public Optional<String> result(String input) {
    if (!input.equals(cachedInput)) {
      cachedInput = input;
      cachedResult = CalculatorExpression.evaluate(input);
    }

    return cachedResult;
  }
}
