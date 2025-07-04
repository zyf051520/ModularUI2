package com.cleanroommc.modularui.api.widget;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.layout.IResizeable;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.drawable.Stencil;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widget.sizer.Area;
import com.cleanroommc.modularui.widget.sizer.Flex;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A widget in a Gui
 */
public interface IWidget extends IGuiElement {

    /**
     * Validates and initialises this element.
     * This element now becomes valid
     *
     * @param parent the parent this element belongs to
     */
    void initialise(@NotNull IWidget parent);

    /**
     * Invalidates this element.
     */
    void dispose();

    /**
     * Determines if this element exist in an active gui.
     *
     * @return if this is in a valid gui
     */
    boolean isValid();

    /**
     * Draws the background of this widget.
     *
     * @param context     gui context
     * @param widgetTheme widget theme of this widget
     */
    void drawBackground(ModularGuiContext context, WidgetTheme widgetTheme);

    /**
     * Draws additional stuff in this widget.
     * x = 0 and y = 0 is now in the top left corner of this widget.
     * Do NOT override this method, it is never called. Use {@link #draw(ModularGuiContext, WidgetTheme)} instead.
     *
     * @param context gui context
     */
    @ApiStatus.NonExtendable
    @Deprecated
    @Override
    default void draw(ModularGuiContext context) {
        draw(context, getWidgetTheme(context.getTheme()));
    }

    /**
     * Draws extra elements of this widget. Called after {@link #drawBackground(ModularGuiContext, WidgetTheme)} and before
     * {@link #drawOverlay(ModularGuiContext, WidgetTheme)}
     *
     * @param context     gui context
     * @param widgetTheme widget theme
     */
    void draw(ModularGuiContext context, WidgetTheme widgetTheme);

    /**
     * Draws the overlay of this theme.
     *
     * @param context     gui context
     * @param widgetTheme widget theme
     */
    void drawOverlay(ModularGuiContext context, WidgetTheme widgetTheme);

    /**
     * Draws foreground elements of this widget. For example tooltips.
     * No transformations are applied here.
     *
     * @param context gui context
     */
    void drawForeground(ModularGuiContext context);

    default void transform(IViewportStack stack) {
        stack.translate(getArea().rx, getArea().ry, getArea().getPanelLayer() * 20);
    }

    default WidgetTheme getWidgetTheme(ITheme theme) {
        return theme.getFallback();
    }

    /**
     * Called 20 times per second.
     */
    void onUpdate();

    /**
     * @return the area this widget occupies
     */
    @Override
    Area getArea();

    /**
     * Calculates if a given pos is inside this widgets area.
     * This should be used over {@link Area#isInside(int, int)}, since this accounts for transformations.
     *
     * @param stack viewport stack
     * @param mx    x pos
     * @param my    y pos
     * @return if pos is inside this widgets area
     */
    default boolean isInside(IViewportStack stack, int mx, int my) {
        int x = stack.unTransformX(mx, my);
        int y = stack.unTransformY(mx, my);
        return x >= 0 && x < getArea().w() && y >= 0 && y < getArea().h();
    }

    /**
     * @return all children of this widget
     */
    @NotNull
    default List<IWidget> getChildren() {
        return Collections.emptyList();
    }

    /**
     * @return if this widget has any children
     */
    default boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    /**
     * @return the panel this widget is in
     */
    @NotNull
    ModularPanel getPanel();

    /**
     * Returns if this element is enabled. Disabled elements are not drawn and can not be interacted with.
     *
     * @return if this element is enabled
     */
    @Override
    boolean isEnabled();

    void setEnabled(boolean enabled);

    default boolean areAncestorsEnabled() {
        IWidget parent = this;
        do {
            if (!parent.isEnabled()) return false;
            parent = parent.getParent();
        } while (parent.hasParent());
        return true;
    }

    default boolean canBeSeen(IViewportStack stack) {
        return Stencil.isInsideScissorArea(getArea(), stack);
    }

    default boolean canHover() {
        return true;
    }

    default boolean canClickThrough() {
        return true;
    }

    /**
     * Marks tooltip for this widget as dirty.
     */
    void markTooltipDirty();

    /**
     * @return the parent of this widget
     */
    @NotNull
    IWidget getParent();

    @Override
    default boolean hasParent() {
        return isValid();
    }

    /**
     * @return the context the current screen
     */
    ModularGuiContext getContext();

    /**
     * @return flex of this widget. Creates a new one if it doesn't already have one.
     */
    Flex flex();

    /**
     * Does the same as {@link IPositioned#flex(Consumer)}
     *
     * @param builder function to build flex
     * @return this
     */
    default IWidget flexBuilder(Consumer<Flex> builder) {
        builder.accept(flex());
        return this;
    }

    /**
     * @return resizer of this widget
     */
    @NotNull
    @Override
    IResizeable resizer();

    /**
     * Sets the resizer of this widget.
     *
     * @param resizer resizer
     */
    void resizer(IResizeable resizer);

    /**
     * Called before a widget is resized.
     */
    default void beforeResize() {
    }

    /**
     * Called after a widget is fully resized.
     */
    default void onResized() {
    }

    /**
     * Called after the full widget tree is resized and the absolute positions are calculated.
     */
    default void postResize() {
    }

    /**
     * @return flex of this widget
     */
    Flex getFlex();

    default boolean isExpanded() {
        Flex flex = getFlex();
        return flex != null && flex.isExpanded();
    }
}
