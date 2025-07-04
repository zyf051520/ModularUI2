package com.cleanroommc.modularui.api;

import com.cleanroommc.modularui.mixins.early.minecraft.GuiContainerAccessor;
import com.cleanroommc.modularui.screen.ClientScreenHandler;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Platform;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.IntConsumer;

/**
 * Implement this interface on a {@link GuiScreen} to be able to use it as a custom wrapper.
 * The GuiScreen should have final {@link ModularScreen} field, which is set from the constructor.
 * Additionally, the GuiScreen MUST call {@link ModularScreen#construct(IMuiScreen)} in its constructor.
 * See {@link com.cleanroommc.modularui.screen.GuiScreenWrapper GuiScreenWrapper} and {@link com.cleanroommc.modularui.screen.GuiContainerWrapper GuiContainerWrapper}
 * for default implementations.
 */
@SideOnly(Side.CLIENT)
public interface IMuiScreen {

    /**
     * Returns the {@link ModularScreen} that is being wrapped. This should return a final instance field.
     *
     * @return the wrapped modular screen
     */
    @NotNull
    ModularScreen getScreen();

    /**
     * This method decides how the gui background is drawn.
     * The intended usage is to override {@link GuiScreen#drawWorldBackground(int)} and call this method
     * with the super method reference as the second parameter.
     *
     * @param tint         background color tint
     * @param drawFunction a method reference to draw the world background normally with the tint as the parameter
     */
    @ApiStatus.NonExtendable
    default void handleDrawBackground(int tint, IntConsumer drawFunction) {
        if (ClientScreenHandler.shouldDrawWorldBackground()) {
            drawFunction.accept(tint);
        }
        ClientScreenHandler.drawDarkBackground(getGuiScreen(), tint);
        // after this JEI will draw itself
        // for some reason JEI is too stupid to set up opengl properly
        // without this (enableTexture2D() specifically) every item in JEI will be texture less (white)
        Platform.setupDrawTex();
    }

    /**
     * This method is called every time the {@link ModularScreen} resizes.
     * This usually only affects {@link GuiContainer GuiContainers}.
     *
     * @param area area of the main panel
     */
    default void updateGuiArea(Rectangle area) {
        if (getGuiScreen() instanceof GuiContainer container) {
            ClientScreenHandler.updateGuiArea(container, area);
        }
    }

    /**
     * @return if this wrapper is a {@link GuiContainer}
     */
    @ApiStatus.NonExtendable
    default boolean isGuiContainer() {
        return getGuiScreen() instanceof GuiContainer;
    }

    /**
     * Hovering widget is handled by {@link ModularGuiContext}.
     * If it detects a slot, this method is called. Only affects {@link GuiContainer GuiContainers}.
     *
     * @param slot hovered slot
     */
    @ApiStatus.NonExtendable
    default void setHoveredSlot(Slot slot) {
        if (getGuiScreen() instanceof GuiContainerAccessor acc) {
            acc.setHoveredSlot(slot);
        }
    }

    /**
     * Returns the {@link GuiScreen} that wraps the {@link ModularScreen}.
     * In most cases this does not need to be overridden as this interfaces should be implemented on {@link GuiScreen GuiScreens}.
     *
     * @return the wrapping gui screen
     */
    default GuiScreen getGuiScreen() {
        return (GuiScreen) this;
    }
}
