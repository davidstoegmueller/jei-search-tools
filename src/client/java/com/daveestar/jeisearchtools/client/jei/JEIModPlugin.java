package com.daveestar.jeisearchtools.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class JEIModPlugin implements IModPlugin {
  private static final Identifier ID = Identifier.fromNamespaceAndPath("jeisearchtools", "plugin");

  @Override
  public Identifier getPluginUid() {
    return ID;
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime runtime) {
    JEISearchService.setRuntime(runtime);
  }

  @Override
  public void onRuntimeUnavailable() {
    JEISearchService.clearRuntime();
    JEISearchEvents.RUNTIME_UNAVAILABLE.invoker().onRuntimeUnavailable();
  }
}
