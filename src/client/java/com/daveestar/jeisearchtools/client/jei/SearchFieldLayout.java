package com.daveestar.jeisearchtools.client.jei;

import mezz.jei.common.util.ImmutableRect2i;

/** Layout information added to JEI's search field by the client mixin. */
public interface SearchFieldLayout {
  ImmutableRect2i jeiSearchTools$getToolsButtonArea();
}
