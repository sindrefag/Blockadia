package net.thegaminghuskymc.sandboxgame.server.management;

import com.google.gson.JsonObject;
import net.thegaminghuskymc.authlib.GameProfile;

import java.util.UUID;

public class UserListWhitelistEntry extends UserListEntry<GameProfile> {
    public UserListWhitelistEntry(GameProfile profile) {
        super(profile);
    }

    public UserListWhitelistEntry(JsonObject json) {
        super(gameProfileFromJsonObject(json), json);
    }

    private static GameProfile gameProfileFromJsonObject(JsonObject json) {
        if (json.has("uuid") && json.has("name")) {
            String s = json.get("uuid").getAsString();
            UUID uuid;

            try {
                uuid = UUID.fromString(s);
            } catch (Throwable var4) {
                return null;
            }

            return new GameProfile(uuid, json.get("name").getAsString());
        } else {
            return null;
        }
    }

    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile) this.getValue()).getId() == null ? "" : ((GameProfile) this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile) this.getValue()).getName());
            super.onSerialization(data);
        }
    }
}