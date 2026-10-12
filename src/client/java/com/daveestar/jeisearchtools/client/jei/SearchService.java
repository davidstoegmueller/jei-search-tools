package com.daveestar.jeisearchtools.client.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SearchService {
  private static final Logger LOGGER = LoggerFactory.getLogger("jei-search-tools");
  private static IJeiRuntime runtime;
  private static IIngredientHelper<ItemStack> itemHelper;
  private static String cachedFilter;
  private static Set<Object> matchingUids = Set.of();

  private SearchService() {
  }

  public static void setRuntime(IJeiRuntime jeiRuntime) {
    Objects.requireNonNull(jeiRuntime, "jeiRuntime");
    if (runtime == jeiRuntime) {
      return;
    }
    runtime = jeiRuntime;
    itemHelper = runtime.getIngredientManager().getIngredientHelper(VanillaTypes.ITEM_STACK);
    var listener = new MatchInvalidator();
    runtime.getIngredientManager().registerIngredientListener(listener);
    runtime.getJeiHelpers().getIngredientVisibility().registerListener(listener);
    invalidateMatches();
    LOGGER.debug("JEI search runtime available");
  }

  public static void clearRuntime() {
    runtime = null;
    itemHelper = null;
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
      refreshMatches(filter);
    }

    Object uid = itemHelper.getUid(stack, UidContext.Ingredient);

    return matchingUids.contains(uid);
  }

  public static void invalidateMatches() {
    cachedFilter = null;
    matchingUids = Set.of();
  }

  private static void refreshMatches(String filter) {
    Set<Object> uids = new HashSet<>();

    for (ItemStack stack : runtime.getIngredientFilter().getFilteredItemStacks()) {
      uids.add(itemHelper.getUid(stack, UidContext.Ingredient));
    }

    matchingUids = uids;
    cachedFilter = filter;
  }

  /**
   * JEI owns these listeners for the lifetime of its runtime; no screen is
   * retained.
   */
  private static final class MatchInvalidator
      implements IIngredientManager.IIngredientListener, IIngredientVisibility.IListener {
    @Override
    public <V> void onIngredientsAdded(IIngredientHelper<V> helper, Collection<ITypedIngredient<V>> ingredients) {
      invalidateMatches();
    }

    @Override
    public <V> void onIngredientsRemoved(IIngredientHelper<V> helper, Collection<ITypedIngredient<V>> ingredients) {
      invalidateMatches();
    }

    @Override
    public <V> void onIngredientVisibilityChanged(ITypedIngredient<V> ingredient, boolean visible) {
      invalidateMatches();
    }

    @Override
    public <V> void onIngredientsVisibilityChanged(Collection<ITypedIngredient<V>> ingredients, boolean visible) {
      invalidateMatches();
    }
  }
}
