package net.realdarkstudios.minecachingbm;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.realdarkstudios.minecaching.api.MinecachingAPI;
import net.realdarkstudios.minecaching.api.event.minecache.MinecacheCreatedEvent;
import net.realdarkstudios.minecaching.api.event.minecache.MinecacheDeletedEvent;
import net.realdarkstudios.minecaching.api.event.minecache.MinecacheEditedEvent;
import net.realdarkstudios.minecaching.api.minecache.MinecacheType;
import net.realdarkstudios.minecachingbm.commands.ReloadCommand;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;

public final class MinecachingBlueMap extends JavaPlugin implements Listener {
    private static MinecachingBlueMap plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("mbmreload").setExecutor(new ReloadCommand());

        getServer().getPluginManager().registerEvents(this, this);

        BlueMapAPI.onEnable(api -> {

            api.getMaps().forEach(m -> {
                m.getMarkerSets().put("traditional_caches", m.getMarkerSets().getOrDefault("traditional_caches", MarkerSet.builder().label("MBM >> Traditionals").build()));
                m.getMarkerSets().put("mystery_caches", m.getMarkerSets().getOrDefault("mystery_caches", MarkerSet.builder().label("MBM >> Mysteries").build()));
                m.getMarkerSets().put("multi_caches", m.getMarkerSets().getOrDefault("multi_caches", MarkerSet.builder().label("MBM >> Multis").build()));

                try (OutputStream out = m.getAssetStorage().writeAsset("traditional")) {
                    ImageIO.write(ImageIO.read(getResource("assets/traditional.png")), "png", out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (OutputStream out = m.getAssetStorage().writeAsset("mystery")) {
                    ImageIO.write(ImageIO.read(getResource("assets/mystery.png")), "png", out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (OutputStream out = m.getAssetStorage().writeAsset("multi")) {
                    ImageIO.write(ImageIO.read(getResource("assets/multi.png")), "png", out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                reloadMarkers(api);
            });
        });
    }

    @Override
    public void onDisable() {
    }

    public void reloadMarkers(BlueMapAPI api) {
        getLogger().info("Updating Markers");
        api.getMaps().forEach(m -> {
            // Marker Sets
            MarkerSet tradms = m.getMarkerSets().getOrDefault("traditional_caches", MarkerSet.builder().label("MBM >> Traditionals").build());
            MarkerSet mystms = m.getMarkerSets().getOrDefault("mystery_caches", MarkerSet.builder().label("MBM >> Mysteries").build());
            MarkerSet multms = m.getMarkerSets().getOrDefault("multi_caches", MarkerSet.builder().label("MBM >> Multis").build());

            // Markers
            MinecachingAPI.getCacheStorage().getMinecaches().stream().filter(c -> c.type().equals(MinecacheType.TRADITIONAL)).forEach(c -> {
                tradms.put(c.id(), POIMarker.builder().label(c.id() + ": " + c.name())
                        .icon(m.getAssetStorage().getAssetUrl("traditional"), 0, 0)
                        .detail(c.id())
                        .position(locToVec3d(c.navLocation()))
                        .build());
            });
            MinecachingAPI.getCacheStorage().getMinecaches().stream().filter(c -> c.type().equals(MinecacheType.MYSTERY)).forEach(c -> {
                mystms.put(c.id(), POIMarker.builder().label(c.id() + ": " + c.name())
                        .icon(m.getAssetStorage().getAssetUrl("mystery"), 0, 0)
                        .detail(c.id())
                        .position(locToVec3d(c.navLocation()))
                        .build());
            });
            MinecachingAPI.getCacheStorage().getMinecaches().stream().filter(c -> c.type().equals(MinecacheType.MULTI)).forEach(c -> {
                multms.put(c.id(), POIMarker.builder().label(c.id() + ": " + c.name())
                        .icon(m.getAssetStorage().getAssetUrl("multi"), 0, 0)
                        .detail(c.id())
                        .position(locToVec3d(c.navLocation()))
                        .build());
            });
        });
        getLogger().info("Updated Markers");
    }

    public static MinecachingBlueMap getInstance() {
        return plugin;
    }

    private static Vector3d locToVec3d(Location in) {
        return new Vector3d(in.getBlockX(), in.getBlockY(), in.getBlockZ());
    }

    @EventHandler
    private void onCacheCreated(MinecacheCreatedEvent event) {
        BlueMapAPI.onEnable(MinecachingBlueMap.getInstance()::reloadMarkers);
    }

    @EventHandler
    private void onCacheEdited(MinecacheEditedEvent event) {
        BlueMapAPI.onEnable(MinecachingBlueMap.getInstance()::reloadMarkers);
    }

    @EventHandler
    private void onCacheDeleted(MinecacheDeletedEvent event) {
        BlueMapAPI.onEnable(MinecachingBlueMap.getInstance()::reloadMarkers);
    }
}
