package net.thegaminghuskymc.sandboxgame.engine.util.text.event;

import com.google.common.collect.Maps;
import net.thegaminghuskymc.sandboxgame.engine.util.text.ITextComponent;

import java.util.Map;

public class HoverEvent {
    private final HoverEvent.Action action;
    private final ITextComponent value;

    public HoverEvent(HoverEvent.Action actionIn, ITextComponent valueIn) {
        this.action = actionIn;
        this.value = valueIn;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public HoverEvent.Action getAction() {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "show item",
     * this would be the item to show.
     */
    public ITextComponent getValue() {
        return this.value;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            HoverEvent hoverevent = (HoverEvent) p_equals_1_;

            if (this.action != hoverevent.action) {
                return false;
            } else {
                if (this.value != null) {
                    return this.value.equals(hoverevent.value);
                } else return hoverevent.value == null;

            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public enum Action {
        SHOW_TEXT("show_text", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        private static final Map<String, HoverEvent.Action> NAME_MAPPING = Maps.newHashMap();

        static {
            for (HoverEvent.Action hoverevent$action : values()) {
                NAME_MAPPING.put(hoverevent$action.getCanonicalName(), hoverevent$action);
            }
        }

        private final boolean allowedInChat;
        private final String canonicalName;

        Action(String canonicalNameIn, boolean allowedInChatIn) {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        /**
         * Gets a value by its canonical name.
         */
        public static HoverEvent.Action getValueByCanonicalName(String canonicalNameIn) {
            return NAME_MAPPING.get(canonicalNameIn);
        }

        /**
         * Indicates whether this event can be run from chat text.
         */
        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        /**
         * Gets the canonical name for this action (e.g., "show_achievement")
         */
        public String getCanonicalName() {
            return this.canonicalName;
        }
    }
}