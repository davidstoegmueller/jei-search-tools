package com.daveestar.jeisearchtools.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class ModPlugin implements IModPlugin {
  private static final Identifier ID = Identifier.fromNamespaceAndPath("jeisearchtools", "plugin");

  @Override
  public Identifier getPluginUid() {
    return ID;
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime runtime) {
    SearchService.setRuntime(runtime);
  }

  @Override
  public void onRuntimeUnavailable() {
    SearchService.clearRuntime();
    SearchEvents.RUNTIME_UNAVAILABLE.invoker().onRuntimeUnavailable();
  }
}
