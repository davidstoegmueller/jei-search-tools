package com.daveestar.jeisearchtools.client.mixin.jei;

import com.daveestar.jeisearchtools.client.jei.SearchFieldLayout;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.GuiTextFieldFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiTextFieldFilter.class, remap = false)
public abstract class GuiTextFieldFilterMixin implements SearchFieldLayout {
  @Unique
  private static final int jeiSearchTools$BUTTON_WIDTH = 18;

  @Unique
  private static final int jeiSearchTools$BUTTON_GAP = 2;

  @Unique
  private static final int jeiSearchTools$CONFIG_GAP = 4;

  @Unique
  private ImmutableRect2i jeiSearchTools$toolsButtonArea = ImmutableRect2i.EMPTY;

  @ModifyVariable(method = "updateBounds", at = @At("HEAD"), argsOnly = true, remap = false)
  private ImmutableRect2i jeiSearchTools$reserveButton(ImmutableRect2i area) {
    int reservedWidth = jeiSearchTools$BUTTON_WIDTH + jeiSearchTools$BUTTON_GAP + jeiSearchTools$CONFIG_GAP;
    if (area.getWidth() < reservedWidth + 40 || area.getHeight() == 0) {
      jeiSearchTools$toolsButtonArea = ImmutableRect2i.EMPTY;
      return area;
    }
    jeiSearchTools$toolsButtonArea = new ImmutableRect2i(
        area.getX() + area.getWidth() - jeiSearchTools$CONFIG_GAP - jeiSearchTools$BUTTON_WIDTH,
        area.getY(), jeiSearchTools$BUTTON_WIDTH, area.getHeight());
    return area.cropRight(reservedWidth);
  }

  @Override
  public ImmutableRect2i jeiSearchTools$getToolsButtonArea() {
    return jeiSearchTools$toolsButtonArea;
  }
}
