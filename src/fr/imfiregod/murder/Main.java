package fr.imfiregod.murder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import fr.imfiregod.utils.FastBoard;

public class Main extends JavaPlugin {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private GameState state;
    
    public GameState getState() {
    	return this.state;
    }
    
    public void setState(GameState statut) {
    	this.state = statut;
    }
}
