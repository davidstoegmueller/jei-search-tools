package com.daveestar.jeisearchtools.client.jei;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class JEISearchEvents {
  public static final Event<SearchDoubleClick> DOUBLE_CLICK = EventFactory.createArrayBacked(
      SearchDoubleClick.class,
      listeners -> () -> {
        for (SearchDoubleClick listener : listeners) {
          listener.onDoubleClick();
        }
      });

  public static final Event<RuntimeUnavailable> RUNTIME_UNAVAILABLE = EventFactory.createArrayBacked(
      RuntimeUnavailable.class,
      listeners -> () -> {
        for (RuntimeUnavailable listener : listeners) {
          listener.onRuntimeUnavailable();
        }
      });

  private JEISearchEvents() {
  }

  @FunctionalInterface
  public interface SearchDoubleClick {
    void onDoubleClick();
  }

  @FunctionalInterface
  public interface RuntimeUnavailable {
    void onRuntimeUnavailable();
  }
}
