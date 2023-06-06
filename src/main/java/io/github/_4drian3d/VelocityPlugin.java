package io.github._4drian3d;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
	id = "velocityplugin",
	name = "VelocityPlugin",
	description = "A Velocity Plugin Template",
	version = Constants.VERSION,
	authors = { "4drian3d" }
)
public final class VelocityPlugin {
	private final ProxyServer proxyServer;
	private final Logger logger;
	private final Path path;
	private final PluginManager pluginManager;
	private final EventManager eventManager;
	private final CommandManager commandManager;

	@Inject
	public VelocityPlugin(
			final ProxyServer proxyServer,
			final Logger logger,
			final @DataDirectory Path path,
			final PluginManager pluginManager,
			final EventManager eventManager,
			final CommandManager commandManager
	) {
		this.proxyServer = proxyServer;
		this.logger = logger;
		this.path = path;
		this.pluginManager = pluginManager;
		this.eventManager = eventManager;
		this.commandManager = commandManager;
	}
	
	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		// do stuff here
		logger.info("Hello World");
	}
}