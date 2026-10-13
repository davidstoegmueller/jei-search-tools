package com.daveestar.jeisearchtools.client.mixin.jei;

import com.daveestar.jeisearchtools.client.jei.SearchFieldLayout;
import com.daveestar.jeisearchtools.client.tool.SearchTools;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.GuiTextFieldFilter;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

  @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/input/GuiTextFieldFilter;addFormatter(Lnet/minecraft/client/gui/components/EditBox$TextFormatter;)V"), index = 0, remap = false)
  private EditBox.TextFormatter jeiSearchTools$formatCalculations(EditBox.TextFormatter original) {
    return (text, offset) -> SearchTools.calculatorResult(((GuiTextFieldFilter) (Object) this).getValue()).isPresent()
        ? FormattedCharSequence.forward(text, Style.EMPTY.withColor(0xFFFFFFFF))
        : original.format(text, offset);
  }

  @Inject(method = "setValue", at = @At("RETURN"), remap = false)
  private void jeiSearchTools$setCalculationColor(String text, CallbackInfo ci) {
    if (SearchTools.calculatorResult(text).isPresent()) {
      ((GuiTextFieldFilter) (Object) this).setTextColor(0xFFFFFFFF);
    }
  }

  @Override
  public ImmutableRect2i jeiSearchTools$getToolsButtonArea() {
    return jeiSearchTools$toolsButtonArea;
  }

}
