package com.daveestar.jeisearchtools.client.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class JEISearchService {
  private static final Logger LOGGER = LoggerFactory.getLogger("jei-search-tools");
  private static IJeiRuntime runtime;
  private static String cachedFilter;
  private static Set<Object> matchingUids = Set.of();

  private JEISearchService() {
  }

  public static void setRuntime(IJeiRuntime jeiRuntime) {
    runtime = jeiRuntime;
    invalidateMatches();
    LOGGER.debug("JEI search runtime available");
  }

  public static void clearRuntime() {
    runtime = null;
    invalidateMatches();
    LOGGER.debug("JEI search runtime unavailable");
  }

  public static boolean hasSearchText() {
    return runtime != null &&
        !runtime.getIngredientFilter().getFilterText().isBlank();
  }

  public static Optional<IGuiProperties> getGuiProperties(Screen screen) {
    if (runtime == null) {
      return Optional.empty();
    }

    return runtime.getScreenHelper().getGuiProperties(screen);
  }

  public static boolean matches(ItemStack stack) {
    if (runtime == null || stack.isEmpty()) {
      return false;
    }

    String filter = runtime.getIngredientFilter().getFilterText();

    if (filter.isBlank()) {
      return false;
    }

    if (!filter.equals(cachedFilter)) {
      _refreshMatches(filter);
    }

    IIngredientHelper<ItemStack> helper = runtime.getIngredientManager().getIngredientHelper(VanillaTypes.ITEM_STACK);

    Object uid = helper.getUid(stack, UidContext.Ingredient);

    return matchingUids.contains(uid);
  }

  public static void invalidateMatches() {
    cachedFilter = null;
    matchingUids = Set.of();
  }

  private static void _refreshMatches(String filter) {
    IIngredientHelper<ItemStack> helper = runtime.getIngredientManager().getIngredientHelper(VanillaTypes.ITEM_STACK);

    Set<Object> uids = new HashSet<>();

    for (ItemStack stack : runtime.getIngredientFilter().getFilteredItemStacks()) {
      uids.add(helper.getUid(stack, UidContext.Ingredient));
    }

    matchingUids = uids;
    cachedFilter = filter;
  }
}
