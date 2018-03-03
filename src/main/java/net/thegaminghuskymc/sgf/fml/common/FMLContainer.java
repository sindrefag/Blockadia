package net.thegaminghuskymc.sgf.fml.common;

import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import net.thegaminghuskymc.sandboxgame.GameEngine;
import net.thegaminghuskymc.sgf.fml.common.event.FMLConstructionEvent;
import net.thegaminghuskymc.sgf.fml.common.eventhandler.EventBus;
import net.thegaminghuskymc.sandboxgame.nbt.NBTBase;
import net.thegaminghuskymc.sandboxgame.nbt.NBTTagCompound;
import net.thegaminghuskymc.sandboxgame.nbt.NBTTagList;
import net.thegaminghuskymc.sandboxgame.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.File;
import java.security.Certificate;
import java.util.Arrays;
import java.util.Map;

public final class FMLContainer extends DummyModContainer implements WorldAccessContainer
{
    public FMLContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId="FML";
        meta.name="Forge Mod Loader";
        meta.version=Loader.instance().getFMLVersionString();
        meta.credits="Made possible with help from many people";
        meta.authorList=Arrays.asList("cpw", "LexManos", "Player");
        meta.description="The Forge Mod Loader provides the ability for systems to load mods " +
                    "from the file system. It also provides key capabilities for mods to be able " +
                    "to cooperate and provide a good modding environment. ";
        meta.url="https://github.com/MinecraftForge/FML/wiki";
        meta.screenshots=new String[0];
        meta.logoFile="";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent evt)
    {
        NetworkRegistry.INSTANCE.register(this, this.getClass(), null, evt.getASMHarvestedData());
        FMLNetworkHandler.registerChannel(this, evt.getSide());
    }

    @Subscribe
    public void modPreinitialization(FMLPreInitializationEvent evt)
    {
        // Initialize all Forge/Vanilla registries {invoke the static init)
        if (ForgeRegistries.ITEMS == null)
            throw new RuntimeException("Something horrible went wrong in init, ForgeRegistres didn't create...");
    }

    @NetworkCheckHandler
    public boolean checkModLists(Map<String,String> modList, GameEngine.Side side)
    {
        return Loader.instance().checkRemoteModList(modList,side);
    }
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound fmlData = new NBTTagCompound();
        NBTTagList modList = new NBTTagList();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            NBTTagCompound mod = new NBTTagCompound();
            mod.setString("ModId", mc.getModId());
            mod.setString("ModVersion", mc.getVersion());
            modList.appendTag(mod);
        }
        fmlData.setTag("ModList", modList);

        NBTTagCompound registries = new NBTTagCompound();
        fmlData.setTag("Registries", registries);
        FMLLog.log.debug("Gathering id map for writing to world save {}", info.getWorldName());

        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet())
        {
            NBTTagCompound data = new NBTTagCompound();
            registries.setTag(e.getKey().toString(), data);

            NBTTagList ids = new NBTTagList();
            for (Map.Entry<ResourceLocation, Integer> item : e.getValue().ids.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", item.getKey().toString());
                tag.setInteger("V", item.getValue());
                ids.appendTag(tag);
            }
            data.setTag("ids", ids);

            NBTTagList aliases = new NBTTagList();
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : e.getValue().aliases.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.getKey().toString());
                tag.setString("V", entry.getValue().toString());
                aliases.appendTag(tag);
            }
            data.setTag("aliases", aliases);

            NBTTagList overrides = new NBTTagList();
            for (Map.Entry<ResourceLocation, String> entry : e.getValue().overrides.entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.getKey().toString());
                tag.setString("V", entry.getValue().toString());
                aliases.appendTag(tag);
            }
            data.setTag("overrides", overrides);

            int[] blocked = new int[e.getValue().blocked.size()];
            int idx = 0;
            for (Integer i : e.getValue().blocked)
            {
                blocked[idx++] = i;
            }
            data.setIntArray("blocked", blocked);
            NBTTagList dummied = new NBTTagList();
            for (ResourceLocation entry : e.getValue().dummied)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("K", entry.toString());
                dummied.appendTag(tag);
            }
            data.setTag("dummied", dummied);
        }
        return fmlData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        if (tag.hasKey("ModList"))
        {
            NBTTagList modList = tag.getTagList("ModList", (byte)10);
            for (int i = 0; i < modList.tagCount(); i++)
            {
                NBTTagCompound mod = modList.getCompoundTagAt(i);
                String modId = mod.getString("ModId");
                String modVersion = mod.getString("ModVersion");
                ModContainer container = Loader.instance().getIndexedModList().get(modId);
                if (container == null)
                {
                    LogManager.getLogger("fml.ModTracker").error("This world was saved with mod {} which appears to be missing, things may not work well", modId);
                    continue;
                }
                if (!modVersion.equals(container.getVersion()))
                {
                    LogManager.getLogger("fml.ModTracker").info("This world was saved with mod {} version {} and it is now at version {}, things may not work well", modId, modVersion, container.getVersion());
                }
            }
        }

        Multimap<ResourceLocation, ResourceLocation> failedElements = null;

        if (tag.hasKey("ModItemData") || tag.hasKey("ItemData")) // Pre 1.7
        {
            StartupQuery.notify("This save predates 1.7.10, it can no longer be loaded here. Please load in 1.7.10 or 1.8 first");
            StartupQuery.abort();
        }
        else if (tag.hasKey("Registries")) // 1.8, genericed out the 'registries' list
        {
            Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = Maps.newHashMap();
            NBTTagCompound regs = tag.getCompoundTag("Registries");
            for (String key : regs.getKeySet())
            {
                ForgeRegistry.Snapshot entry = new ForgeRegistry.Snapshot();
                NBTTagCompound ent = regs.getCompoundTag(key);
                snapshot.put(new ResourceLocation(key), entry);

                NBTTagList list = ent.getTagList("ids", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.ids.put(new ResourceLocation(e.getString("K")), e.getInteger("V"));
                }

                list = ent.getTagList("aliases", 10);
                for (int x = 0; x < list.tagCount(); x++)
                {
                    NBTTagCompound e = list.getCompoundTagAt(x);
                    entry.aliases.put(new ResourceLocation(e.getString("K")), new ResourceLocation(e.getString("V")));
                }

                int[] blocked = regs.getCompoundTag(key).getIntArray("blocked");
                for (int i : blocked)
                {
                    entry.blocked.add(i);
                }

                if (regs.getCompoundTag(key).hasKey("dummied")) // Added in 1.8.9 dev, some worlds may not have it.
                {
                    list = regs.getCompoundTag(key).getTagList("dummied",10);
                    for (int x = 0; x < list.tagCount(); x++)
                    {
                        NBTTagCompound e = list.getCompoundTagAt(x);
                        entry.dummied.add(new ResourceLocation(e.getString("K")));
                    }
                }
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty())
        {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n")
               .append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n")
               .append("You will not be able to load until they are present again.\n\n");

            failedElements.asMap().forEach((name, entries) ->
            {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
            });

            StartupQuery.notify(buf.toString());
            StartupQuery.abort();
        }
    }


    @Override
    @Nullable
    public Certificate getSigningCertificate()
    {
        Certificate[] certificates = getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

    @Override
    public File getSource()
    {
        return FMLSanityChecker.fmlLocation;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public String getGuiClassName()
    {
        return "net.minecraftforge.fml.client.FMLConfigGuiFactory";
    }

    @Override
    public Object getMod()
    {
        return this;
    }
}