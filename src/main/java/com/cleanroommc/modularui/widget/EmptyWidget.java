package com.cleanroommc.modularui.widget;

import com.cleanroommc.modularui.api.layout.IResizeable;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widget.sizer.Area;
import com.cleanroommc.modularui.widget.sizer.Flex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyWidget implements IWidget {

    private final Area area = new Area();
    private final Flex flex = new Flex(this);
    private IWidget parent;

    @Override
    public ModularScreen getScreen() {
        return null;
    }

    @Override
    public void initialise(@NotNull IWidget parent) {
        this.parent = parent;
    }

    @Override
    public void dispose() {
        this.parent = null;
    }

    @Override
    public boolean isValid() {
        return this.parent != null;
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetTheme widgetTheme) {
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetTheme widgetTheme) {
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public Area getArea() {
        return this.area;
    }

    @Override
    public @NotNull ModularPanel getPanel() {
        return this.parent.getPanel();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean canBeSeen(IViewportStack stack) {
        return false;
    }

    @Override
    public void markTooltipDirty() {
    }

    @Override
    public @NotNull IWidget getParent() {
        return this.parent;
    }

    @Override
    public ModularGuiContext getContext() {
        return this.parent.getContext();
    }

    @Override
    public Flex flex() {
        return this.flex;
    }

    @Override
    public @NotNull IResizeable resizer() {
        return this.flex;
    }

    @Override
    public void resizer(IResizeable resizer) {
    }

    @Override
    public Flex getFlex() {
        return this.flex;
    }
}
