package fr.imfiregod.murder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.imfiregod.events.PlayerListener;
import fr.imfiregod.task.GameWaiting;
import fr.imfiregod.utils.FastBoard;
import fr.imfiregod.utils.Spawn;

public class Main extends JavaPlugin {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private List<Spawn> spawns = new ArrayList<>();
    private GameState state;
    private GameManager game;
    
    @Override
    public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("§cMurder §7| Chargement du plugin cree par §cTheStorm§7 et §cImFireGod§7.");
		this.state = GameState.WAITING;
		new GameWaiting(this).runTaskTimer(this, 0, 20);
		this.saveDefaultConfig();
		this.loadConfig();
		this.loadEvents();
    }
    
    public GameState getState() {
    	return this.state;
    }
    
    public void setState(GameState statut) {
    	this.state = statut;
    }
    
    public GameManager getGame() {
    	return this.game;
    }
    
    public void setGame(GameManager game) {
    	this.game = game;
    }
    
    public Boolean gameIsStarted() {
    	return this.game != null;
    }
        
    public FastBoard getPlayerBoard(Player p) {
    	return this.boards.get(p.getUniqueId());
    }
    
    public void createScoreboard(Player p, FastBoard board) {
    	board.updateTitle("§c§lMurder");
    	boards.put(p.getUniqueId(), board);
    	this.updateScoreboard(p);
    }
    
    public Map<UUID, FastBoard> getBoards() {
    	return this.boards;
    }
    
    public void updateScoreboard(Player p) {
    	if(this.boards.containsKey(p.getUniqueId())) {
    		DateFormat df = new SimpleDateFormat("dd/MM/yy");
    		FastBoard board = this.boards.get(p.getUniqueId());
        	if(this.state == GameState.WAITING || this.state == GameState.STARTING) {
        		board.updateLines(
            			"§7" + df.format(new Date()),
            			"",
            			"§7Carte: §cManoir",
            			"",
            			"§7Status: §cAttente",
            			"§7Joueurs: §c" + Bukkit.getOnlinePlayers().size() + "/16",
            			"",
            			"§egamers-france.ga"
            	);
        	} else if(this.state == GameState.GAMING) {
        		int innocentSize = this.game.getPlayers().size() - 1;
        		board.updateLines(
            			"§7" + df.format(new Date()),
            			"",
            			"§7Rôle: §c" + this.game.getPlayerRoleName(p),
            			"",
            			"§7Innocent" + (innocentSize > 1 ? "s" : "") + ": §c" + innocentSize, 
            			"§7Détective: §c" + (this.game.getDetective() != null ? "En vie" : "Mort"),
            			"",
            			"§egamers-france.ga"
            	);
        	}
    	}
    }
    
    private void loadEvents() {
    	PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
    }
    
    private void loadConfig() {
    	ConfigurationSection section = getConfig().getConfigurationSection("spawnLocations");
		for(String spawnLocation : section.getKeys(false)) {
			spawns.add(
				new Spawn(section.getDouble(spawnLocation + ".x"), 
					section.getDouble(spawnLocation + ".y"), 
					section.getDouble(spawnLocation + ".z"),
					section.getString(spawnLocation + ".world"))
				);
		}
    }
}
