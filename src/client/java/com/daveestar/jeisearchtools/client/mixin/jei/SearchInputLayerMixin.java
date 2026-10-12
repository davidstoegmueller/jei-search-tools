package com.daveestar.jeisearchtools.client.mixin.jei;

import com.daveestar.jeisearchtools.client.jei.JEISearchEvents;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.InputPhase;
import mezz.jei.common.input.UserInput;
import mezz.jei.common.input.interaction.IInputInteraction;
import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.input.handlers.SearchInputLayer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@Mixin(value = SearchInputLayer.class, remap = false)
public abstract class SearchInputLayerMixin {
  @Unique
  private static final Logger jeiSearchTools$LOGGER = LoggerFactory.getLogger("jei-search-tools");

  @Unique
  private static final long jeiSearchTools$DOUBLE_CLICK_NANOS = 250_000_000L;

  @Unique
  private Screen jeiSearchTools$lastClickScreen;

  @Unique
  private long jeiSearchTools$lastClickTime;

  @Unique
  private double jeiSearchTools$lastClickX;

  @Unique
  private double jeiSearchTools$lastClickY;

  @Shadow
  @Final
  private GuiTextFieldFilter textFieldFilter;

  @Shadow
  @Final
  private BooleanSupplier active;

  @Inject(method = "beginInput", at = @At("HEAD"), remap = false)
  private void _onBeginInput(
      Screen screen,
      UserInput input,
      IInternalKeyMappings keys,
      CallbackInfoReturnable<Optional<IInputInteraction>> cir) {
    if (!(screen instanceof AbstractContainerScreen<?>)) {
      return;
    }

    if (!active.getAsBoolean() || input.getPhase() != InputPhase.PRESS) {
      return;
    }

    input.ifMouseEvent((event, doubleClick) -> {
      boolean overSearch = textFieldFilter.isMouseOver(event.x(), event.y());
      if (event.button() != InputConstants.MOUSE_BUTTON_LEFT ||
          !textFieldFilter.isActive() || !overSearch) {
        jeiSearchTools$lastClickScreen = null;
        return false;
      }

      // JEI's Fabric input bridge supplies doubleClick=false. Detect click pairs
      // here.
      long now = System.nanoTime();
      long elapsed = now - jeiSearchTools$lastClickTime;
      double dx = event.x() - jeiSearchTools$lastClickX;
      double dy = event.y() - jeiSearchTools$lastClickY;
      boolean detectedDoubleClick = jeiSearchTools$lastClickScreen == screen &&
          elapsed >= 0 && elapsed < jeiSearchTools$DOUBLE_CLICK_NANOS &&
          dx * dx + dy * dy <= 16;

      jeiSearchTools$LOGGER.debug(
          "Search click: jeiDoubleClick={}, detectedDoubleClick={}, intervalMs={}, screen={}",
          doubleClick, detectedDoubleClick,
          jeiSearchTools$lastClickScreen == screen ? elapsed / 1_000_000 : -1,
          screen.getClass().getSimpleName());

      if (detectedDoubleClick) {
        // Start a fresh pair after toggling, so a triple click only toggles once.
        jeiSearchTools$lastClickScreen = null;
        JEISearchEvents.DOUBLE_CLICK.invoker().onDoubleClick();
      } else {
        jeiSearchTools$lastClickScreen = screen;
        jeiSearchTools$lastClickTime = now;
        jeiSearchTools$lastClickX = event.x();
        jeiSearchTools$lastClickY = event.y();
      }

      return false;
    });
  }

  @Inject(method = "resetInput", at = @At("HEAD"), remap = false)
  private void jeiSearchTools$resetClick(CallbackInfo ci) {
    jeiSearchTools$lastClickScreen = null;
  }
}
