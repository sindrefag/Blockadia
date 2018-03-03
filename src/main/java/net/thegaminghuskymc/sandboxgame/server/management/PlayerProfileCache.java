package net.thegaminghuskymc.sandboxgame.server.management;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import net.thegaminghuskymc.authlib.Agent;
import net.thegaminghuskymc.authlib.GameProfile;
import net.thegaminghuskymc.authlib.GameProfileRepository;
import net.thegaminghuskymc.authlib.ProfileLookupCallback;
import net.thegaminghuskymc.sandboxgame.util.JsonUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerProfileCache {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static final ParameterizedType TYPE = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{PlayerProfileCache.ProfileEntry.class};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };
    private static boolean onlineMode;
    /**
     * A map between player usernames and {@link ProfileEntry profile entries}
     */
    public final Map<String, ProfileEntry> usernameToProfileEntryMap = Maps.<String, PlayerProfileCache.ProfileEntry>newHashMap();
    protected final Gson gson;
    /**
     * A map between {@link UUID UUID's} and {@link ProfileEntry ProfileEntries}
     */
    private final Map<UUID, ProfileEntry> uuidToProfileEntryMap = Maps.<UUID, PlayerProfileCache.ProfileEntry>newHashMap();
    /**
     * A list of all the cached {@link GameProfile GameProfiles}
     */
    private final Deque<GameProfile> gameProfiles = Lists.<GameProfile>newLinkedList();
    private final GameProfileRepository profileRepo;
    private final File usercacheFile;

    public PlayerProfileCache(GameProfileRepository profileRepoIn, File usercacheFileIn) {
        this.profileRepo = profileRepoIn;
        this.usercacheFile = usercacheFileIn;
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
        this.gson = gsonbuilder.create();
        this.load();
    }

    private static GameProfile lookupProfile(GameProfileRepository profileRepoIn, String name) {
        final GameProfile[] agameprofile = new GameProfile[1];
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                agameprofile[0] = p_onProfileLookupSucceeded_1_;
            }

            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                agameprofile[0] = null;
            }
        };
        profileRepoIn.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profilelookupcallback);

        if (!isOnlineMode() && agameprofile[0] == null) {
            UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID) null, name));
            GameProfile gameprofile = new GameProfile(uuid, name);
            profilelookupcallback.onProfileLookupSucceeded(gameprofile);
        }

        return agameprofile[0];
    }

    private static boolean isOnlineMode() {
        return onlineMode;
    }

    public static void setOnlineMode(boolean onlineModeIn) {
        onlineMode = onlineModeIn;
    }

    /**
     * Add an entry to this cache
     */
    public void addEntry(GameProfile gameProfile) {
        this.addEntry(gameProfile, (Date) null);
    }

    /**
     * Add an entry to this cache
     */
    private void addEntry(GameProfile gameProfile, Date expirationDate) {
        UUID uuid = gameProfile.getId();

        if (expirationDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            expirationDate = calendar.getTime();
        }

        String s = gameProfile.getName().toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);

        if (this.uuidToProfileEntryMap.containsKey(uuid)) {
            PlayerProfileCache.ProfileEntry playerprofilecache$profileentry1 = this.uuidToProfileEntryMap.get(uuid);
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry1.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(gameProfile);
        }

        this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), playerprofilecache$profileentry);
        this.uuidToProfileEntryMap.put(uuid, playerprofilecache$profileentry);
        this.gameProfiles.addFirst(gameProfile);
        this.save();
    }

    /**
     * Get a player's GameProfile given their username. Mojang's server's will be contacted if the entry is not cached
     * locally.
     */
    @Nullable
    public GameProfile getGameProfileForUsername(String username) {
        String s = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);

        if (playerprofilecache$profileentry != null && (new Date()).getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(playerprofilecache$profileentry.getGameProfile());
            playerprofilecache$profileentry = null;
        }

        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        } else {
            GameProfile gameprofile1 = lookupProfile(this.profileRepo, s);

            if (gameprofile1 != null) {
                this.addEntry(gameprofile1);
                playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
            }
        }

        this.save();
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    /**
     * Get an array of the usernames that are cached in this cache
     */
    public String[] getUsernames() {
        List<String> list = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Get a player's {@link GameProfile} given their UUID
     */
    @Nullable
    public GameProfile getProfileByUUID(UUID uuid) {
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    /**
     * Get a {@link ProfileEntry} by UUID
     */
    private PlayerProfileCache.ProfileEntry getByUUID(UUID uuid) {
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);

        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        }

        return playerprofilecache$profileentry;
    }

    /**
     * Load the cached profiles from disk
     */
    public void load() {
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = Files.newReader(this.usercacheFile, StandardCharsets.UTF_8);
            List<ProfileEntry> list = (List) JsonUtils.fromJson(this.gson, bufferedreader, TYPE);
            this.usernameToProfileEntryMap.clear();
            this.uuidToProfileEntryMap.clear();
            this.gameProfiles.clear();

            if (list != null) {
                for (PlayerProfileCache.ProfileEntry playerprofilecache$profileentry : Lists.reverse(list)) {
                    if (playerprofilecache$profileentry != null) {
                        this.addEntry(playerprofilecache$profileentry.getGameProfile(), playerprofilecache$profileentry.getExpirationDate());
                    }
                }
            }
        } catch (FileNotFoundException var9) {
            ;
        } catch (JsonParseException var10) {
            ;
        } finally {
            IOUtils.closeQuietly((Reader) bufferedreader);
        }
    }

    /**
     * Save the cached profiles to disk
     */
    public void save() {
        String s = this.gson.toJson(this.getEntriesWithLimit(1000));
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.usercacheFile, StandardCharsets.UTF_8);
            bufferedwriter.write(s);
            return;
        } catch (FileNotFoundException var8) {
            ;
        } catch (IOException var9) {
            return;
        } finally {
            IOUtils.closeQuietly((Writer) bufferedwriter);
        }
    }

    /**
     * Get the {@link PlayerProfileCache.ProfileEntry entries} of this cache with a given limit
     */
    private List<ProfileEntry> getEntriesWithLimit(int limitSize) {
        List<ProfileEntry> list = Lists.<PlayerProfileCache.ProfileEntry>newArrayList();

        for (GameProfile gameprofile : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize))) {
            PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.getByUUID(gameprofile.getId());

            if (playerprofilecache$profileentry != null) {
                list.add(playerprofilecache$profileentry);
            }
        }

        return list;
    }

    public class ProfileEntry {
        /**
         * The player's GameProfile
         */
        private final GameProfile gameProfile;
        /**
         * The date that this entry will expire
         */
        private final Date expirationDate;

        private ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
            this.gameProfile = gameProfileIn;
            this.expirationDate = expirationDateIn;
        }

        /**
         * Get the player's GameProfile
         */
        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        /**
         * Get the date that this entry will expire
         */
        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }

    class Serializer implements JsonDeserializer<ProfileEntry>, JsonSerializer<ProfileEntry> {
        private Serializer() {
        }

        public JsonElement serialize(PlayerProfileCache.ProfileEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_serialize_1_.getGameProfile().getName());
            UUID uuid = p_serialize_1_.getGameProfile().getId();
            jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
            jsonobject.addProperty("expiresOn", PlayerProfileCache.DATE_FORMAT.format(p_serialize_1_.getExpirationDate()));
            return jsonobject;
        }

        public PlayerProfileCache.ProfileEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                JsonElement jsonelement = jsonobject.get("name");
                JsonElement jsonelement1 = jsonobject.get("uuid");
                JsonElement jsonelement2 = jsonobject.get("expiresOn");

                if (jsonelement != null && jsonelement1 != null) {
                    String s = jsonelement1.getAsString();
                    String s1 = jsonelement.getAsString();
                    Date date = null;

                    if (jsonelement2 != null) {
                        try {
                            date = PlayerProfileCache.DATE_FORMAT.parse(jsonelement2.getAsString());
                        } catch (ParseException var14) {
                            date = null;
                        }
                    }

                    if (s1 != null && s != null) {
                        UUID uuid;

                        try {
                            uuid = UUID.fromString(s);
                        } catch (Throwable var13) {
                            return null;
                        }

                        return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid, s1), date);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}