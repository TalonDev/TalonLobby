package me.talondev.lobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.talondev.lobby.api.HumanAPI;
import me.talondev.lobby.api.ServerAPI;
import me.talondev.lobby.holograms.Hologram;
import me.talondev.lobby.holograms.HologramLibrary;
import me.talondev.lobby.listeners.Listeners;
import me.talondev.lobby.npc.NPC;
import me.talondev.lobby.npc.NPCLibrary;
import me.talondev.lobby.utilities.ModuleLogger;

/**
 * Plugin de Lobby criado em SpeedCode no canal TalonDev:<br>
 * Primeira parte: <link>https://www.youtube.com/watch?v=WNRry_ukTeI</link><br>
 * Segunda parte (Final): <link>https://www.youtube.com/watch?v=CLY63AsxoJE<link>
 * <br><br>
 * Se voc� est� usando essa SourceCode, n�o � pedir muito deixar essa anota��o.
 * ela n�o ir� aparecer em lugar nenhum, n�o se preocupe. Mas se n�o quiser
 * apenas delete :)
 * 
 * @author TalonDev
 */
public class TalonLobby extends JavaPlugin implements PluginMessageListener {

  private static TalonLobby instance;
  public static final ModuleLogger LOGGER = new ModuleLogger("TalonLobby");

  public static int all, skywars;

  public TalonLobby() {
    instance = this;
  }

  @Override
  public void onEnable() {
    Listeners.makeListeners();

    HumanAPI.makeNPCs();
    ServerAPI.makeMenu();
    /**
     * IREI BLOQUEAR O USO DO WORLD DOWNLOADER. PARA PROTEGER O SEU LOBBY :)
     */
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
    getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", this);

    LOGGER.info("O plugin foi ativado.");
  }

  @Override
  public void onDisable() {
    instance = null;
    HandlerList.unregisterAll(this);
    Bukkit.getScheduler().cancelTasks(this);
    for (NPC npc : NPCLibrary.listNPCs()) {
      if (npc.isSpawned()) {
        npc.despawn();
      }
    }
    for (Hologram hologram : HologramLibrary.listHolograms()) {
      hologram.despawn();
    }

    LOGGER.info("O plugin foi desativado.");
  }

  public static TalonLobby getInstance() {
    return instance;
  }

  public static void sendServer(Player player, String serverName) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(serverName);
    player.sendPluginMessage(getInstance(), "BungeeCord", out.toByteArray());
  }
  
  int count = 1;

  @Override
  public void onPluginMessageReceived(String channel, Player receiver, byte[] msg) {
    if (channel.equals("WDL|INIT")) {
      receiver.kickPlayer(
          "�a�lTalon�f�lMC\n \n�cVoc� est� usando um mod que n�o �\n�cPermitido na nossa rede de servidores.\n \n�cModifica��o detectada: �7World Downloader");
    } else if (channel.equals("BungeeCord")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(msg);

      String subChannel = in.readUTF();
      if (subChannel.equals("PlayerCount")) {
        try {
          String server = in.readUTF();
          if (server.equals("ALL")) {
            all = in.readInt();
          } else if (server.equals("skywars")) {
            skywars = in.readInt();
          }
        } catch (Exception e) {
        }
      }
    }
  }
}
