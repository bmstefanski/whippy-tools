/*
 MIT License

 Copyright (c) 2018 Whippy ToolsImpl

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package pl.bmstefanski.tools.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.diorite.config.ConfigManager;
import pl.bmstefanski.commands.BukkitCommands;
import pl.bmstefanski.commands.CommandExecutor;
import pl.bmstefanski.tools.Tools;
import pl.bmstefanski.tools.command.*;
import pl.bmstefanski.tools.impl.manager.UserManagerImpl;
import pl.bmstefanski.tools.impl.storage.DatabaseFactory;
import pl.bmstefanski.tools.impl.storage.resource.UserResourceImpl;
import pl.bmstefanski.tools.listener.*;
import pl.bmstefanski.tools.manager.UserManager;
import pl.bmstefanski.tools.storage.Database;
import pl.bmstefanski.tools.storage.Resource;
import pl.bmstefanski.tools.storage.configuration.Messages;
import pl.bmstefanski.tools.storage.configuration.PluginConfig;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToolsImpl extends JavaPlugin implements Tools {

  private final File pluginConfigFile = new File(getDataFolder(), "config.yml");
  private final File messagesFile = new File(getDataFolder(), "messages.yml");

  private static ToolsImpl instance;

  private ExecutorService executorService;
  private BukkitCommands bukkitCommands;
  private PluginConfig pluginConfig;
  private UserManager userManager;
  private Messages messages;
  private Database database;
  private Resource resource;

  public ToolsImpl() {
    instance = this;
  }

  @Override
  public void onEnable() {

    this.pluginConfig = ConfigManager.createInstance(PluginConfig.class);
    this.messages = ConfigManager.createInstance(Messages.class);


    this.pluginConfig.bindFile(pluginConfigFile);
    this.messages.bindFile(messagesFile);

    this.pluginConfig.load();
    this.pluginConfig.save();
    this.messages.load();
    this.messages.save();

    this.database = DatabaseFactory.getDatabase("mysql");
    this.executorService = Executors.newCachedThreadPool();

    this.userManager = new UserManagerImpl(this);
    this.bukkitCommands = new BukkitCommands(this);

    this.resource = new UserResourceImpl(this);
    this.resource.checkTable();
    this.resource.load();

    Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|CPack", new BlazingPackMessageReceivedListener(this));

    this.registerListeners(
      new PlayerCommandPreprocessListener(this),
      new PlayerJoinListener(this),
      new PlayerQuitListener(this),
      new PlayerMoveListener(this),
      new EntityDamageListener(this),
      new PlayerDeathListener(this),
      new PlayerLoginListener(this),
      new PlayerInteractListener(this),
      new EntityPickupItemListener(this)
    );

    this.registerCommands(
      new ToolsCommand(this),
      new WhoisCommand(this),
      new WorkbenchCommand(this),
      new ReloadCommand(this),
      new ListCommand(this),
      new HealCommand(this),
      new GodCommand(this),
      new GamemodeCommand(this),
      new FlyCommand(this),
      new FeedCommand(this),
      new EnderchestCommand(this),
      new DisableCommand(this),
      new ClearCommand(this),
      new BroadcastCommand(this),
      new BackCommand(this),
      new AfkCommand(this),
      new HatCommand(this),
      new SkullCommand(this),
      new TpCommand(this),
      new TpHereCommand(this),
      new TpPosCommand(this),
      new DayCommand(this),
      new NightCommand(this),
      new RepairCommand(this),
      new KickCommand(this),
      new KickAllCommand(this),
      new DayCommand(this),
      new NightCommand(this),
      new LightningCommand(this),
      new NicknameCommand(this),
      new RealnameCommand(this),
      new TpAllCommand(this),
      new MarkCommand(this)
    );

  }

  @Override
  public void onDisable() {
    this.pluginConfig.save();
    this.messages.save();
  }

  private void registerCommands(CommandExecutor... executors) {

    for (CommandExecutor commandExecutor : executors) {
      this.bukkitCommands.register(commandExecutor);
      this.bukkitCommands.unregisterBlockedCommands(commandExecutor, this.pluginConfig.getBlockedCommands());
    }

  }

  private void registerListeners(Listener... listeners) {

    for (Listener listener : listeners) {
      Bukkit.getPluginManager().registerEvents(listener, this);
    }
  }

  @Override
  public PluginConfig getConfiguration() {
    return this.pluginConfig;
  }

  @Override
  public Database getDatabase() {
    return this.database;
  }

  @Override
  public UserManager getUserManager() {
    return this.userManager;
  }

  @Override
  public Messages getMessages() {
    return this.messages;
  }

  @Override
  public BukkitCommands getBukkitCommands() {
    return this.bukkitCommands;
  }

  @Override
  public ExecutorService getExecutorService() {
    return this.executorService;
  }

  @Override
  public Resource getResource() {
    return this.resource;
  }

  public static ToolsImpl getInstance() {
    return instance;
  }

}