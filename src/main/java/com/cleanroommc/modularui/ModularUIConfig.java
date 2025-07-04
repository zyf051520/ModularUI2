package com.cleanroommc.modularui;

import com.cleanroommc.modularui.screen.RichTooltip;

import com.gtnewhorizon.gtnhlib.config.Config;


@Config(modid = ModularUI.ID)
public class ModularUIConfig {

    @Config.Comment("Amount of pixels scrolled")
    @Config.RangeInt(min = 1, max = 100)
    public static int defaultScrollSpeed = 30;

    @Config.Comment("If progress bar should step in texture pixels or screen pixels. (Screen pixels are way smaller and therefore smoother)")
    public static boolean smoothProgressBar = true;

    @Config.Comment("Time in 1/60 sec to open and close panels.")
    public static int panelOpenCloseAnimationTime = 0;

    // Default direction
    @Config.Comment("Default tooltip position around the widget or its panel.")
    public static RichTooltip.Pos tooltipPos = RichTooltip.Pos.NEXT_TO_MOUSE;

    @Config.Comment("If true, pressing ESC key in the text field will restore the last text instead of confirming current one.")
    public static boolean escRestoreLastText = false;

    @Config.Comment("If true, widget outlines and widget information will be drawn.")
    public static boolean guiDebugMode = ModularUI.isDevEnv;

    @Config.Comment("If true and not specified otherwise, screens will try to use the 'vanilla_dark' theme.")
    public static boolean useDarkThemeByDefault = false;

    @Config.RequiresMcRestart
    @Config.Comment("Enables a test block, test item with a test gui and opening a gui by right clicking a diamond.")
    public static boolean enableTestGuis = ModularUI.isDevEnv;

    @Config.RequiresMcRestart
    @Config.Comment("Enables a test overlay shown on title screen and watermark shown on every GuiContainer.")
    public static boolean enableTestOverlays = false;

    //@Config.Comment("If true, vanilla tooltip will be replaced with MUI's RichTooltip")
    //public static boolean replaceVanillaTooltips = false;
}
