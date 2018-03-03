/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.thegaminghuskymc.sgf.fml.client.settings;

import net.thegaminghuskymc.sandboxgame.client.resources.I18n;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;


public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(int keyCode)
        {
            return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL|| keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isCtrlKeyDown();
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isCtrlKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            String localizationFormatKey = "forge.controlsgui.control";
            return I18n.format(localizationFormatKey, keyName);
        }
    },
    SHIFT {
        @Override
        public boolean matches(int keyCode)
        {
            return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isShiftKeyDown();
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isShiftKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return I18n.format("forge.controlsgui.shift", keyName);
        }
    },
    ALT {
        @Override
        public boolean matches(int keyCode)
        {
            return keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isAltKeyDown();
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isAltKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return I18n.format("forge.controlsgui.alt", keyName);
        }
    },
    NONE {
        @Override
        public boolean matches(int keyCode)
        {
            return false;
        }

        @Override
        public boolean isActive()
        {
            return true;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME))
            {
                for (KeyModifier keyModifier : MODIFIER_VALUES)
                {
                    if (keyModifier.isActive(conflictContext))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            return GameSettings.getKeyDisplayString(keyCode);
        }
    };

    public static final KeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static KeyModifier getActiveModifier()
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.isActive(null))
            {
                return keyModifier;
            }
        }
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode)
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.matches(keyCode))
            {
                return true;
            }
        }
        return false;
    }

    public static KeyModifier valueFromString(String stringValue)
    {
        try
        {
            return valueOf(stringValue);
        }
        catch (NullPointerException | IllegalArgumentException ignored)
        {
            return NONE;
        }
    }

    public abstract boolean matches(int keyCode);

    /**
     * @deprecated use {@link #isActive(IKeyConflictContext)}
     */
    @Deprecated
    public abstract boolean isActive();

    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    public abstract String getLocalizedComboName(int keyCode);
}