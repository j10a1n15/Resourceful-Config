package com.teamresourceful.resourcefulconfig.api.loader;


import com.teamresourceful.resourcefulconfig.api.patching.ConfigPatchEvent;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.common.config.Configurations;
import com.teamresourceful.resourcefulconfig.common.utils.ModUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class Configurator {

    private final Map<String, ResourcefulConfig> configs = new ConcurrentHashMap<>();
    private final Map<String, Consumer<ConfigPatchEvent>> patchHandlers = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> configClasses = new ConcurrentHashMap<>();

    private final String modid;

    public Configurator(String modid) {
        this.modid = modid;
    }

    public void register(Class<?> clazz) {
        register(clazz, event -> {});
    }

    public void register(Class<?> clazz, Consumer<ConfigPatchEvent> handler) {
        var config = registerConfig(clazz, handler);
        if (config != null) {
            patchHandlers.put(config.id(), handler);
            configClasses.put(clazz, config.id());
            configs.put(config.id(), config);
            Configurations.INSTANCE.addConfig(config, modid);
        }
    }

    @ApiStatus.Internal
    private ResourcefulConfig registerConfig(Class<?> clazz, Consumer<ConfigPatchEvent> handler) {
        try {
            ResourcefulConfig config = ConfigParser.tryParse(clazz);
            config.load(handler);
            config.save();
            return config;
        }catch (Exception e) {
            ModUtils.log("Failed to create config for " + clazz.getName(), e);
        }
        return null;
    }

    public boolean saveConfig(Class<?> config) {
        if (configClasses.containsKey(config)) {
            return saveConfig(configClasses.get(config));
        }
        return false;
    }

    public boolean saveConfig(String fileName) {
        var config = getConfig(fileName);
        if (config != null) {
            config.save();
            return true;
        }
        return false;
    }

    public boolean loadConfig(Class<?> config) {
        if (configClasses.containsKey(config)) {
            return loadConfig(configClasses.get(config));
        }
        return false;
    }

    public boolean loadConfig(String fileName) {
        var config = getConfig(fileName);
        if (config != null) {
            config.load(this.patchHandlers.getOrDefault(fileName, event -> {}));
            return true;
        }
        return false;
    }

    public ResourcefulConfig getConfig(String fileName) {
        return configs.get(fileName);
    }

    public ResourcefulConfig getConfig(Class<?> config) {
        if (configClasses.containsKey(config)) {
            return getConfig(configClasses.get(config));
        }
        return null;
    }
}
