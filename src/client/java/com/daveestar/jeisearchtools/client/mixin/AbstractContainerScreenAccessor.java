package com.daveestar.jeisearchtools.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
  @Accessor("leftPos")
  int jeisearchtools$getLeftPos();

  @Accessor("topPos")
  int jeisearchtools$getTopPos();
}
