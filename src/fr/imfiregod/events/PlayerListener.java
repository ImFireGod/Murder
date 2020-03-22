package fr.imfiregod.events;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.imfiregod.murder.GameManager;
import fr.imfiregod.murder.GameState;
import fr.imfiregod.murder.Main;
import fr.imfiregod.task.BowCooldown;
import fr.imfiregod.utils.FastBoard;

public class PlayerListener implements Listener {

	private Main plugin;
	
	public PlayerListener(Main main) {
		this.plugin = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		event.setJoinMessage(null);
		 
		plugin.createScoreboard(p, new FastBoard(p));
		p.getInventory().clear();
		p.setExp(0);
		p.setLevel(0);
		
		if(plugin.getState() == GameState.WAITING || plugin.getState() == GameState.STARTING) {
			Bukkit.broadcastMessage("§cMurder §8» §c" + p.getDisplayName() + "§7 a rejoint la partie (§c" + Bukkit.getOnlinePlayers().size() + "§7/§c16§7)");
			p.setGameMode(GameMode.ADVENTURE);
			this.updateOnlinePlayers();
		} else {
			if(plugin.gameIsStarted()) {
				GameManager game = plugin.getGame();
				game.addSpectator(p);
				p.teleport(Bukkit.getPlayer(game.getPlayers().get((int) (Math.random() * game.getPlayers().size()))).getLocation());
				p.sendMessage("§cMurder §8»§7 Une partie est déjà lancée vous êtes en mode §cspectateur§7.");
				p.setGameMode(GameMode.SPECTATOR);
			}
		}
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop() != null) {
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getCurrentItem() != null) {
			if(event.getWhoClicked() instanceof Player) {
				Player p = (Player) event.getWhoClicked();
				if(p.getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent event) {
		if(event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.FALLING_BLOCK) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getCause() == DamageCause.ENTITY_ATTACK) {
			event.setCancelled(true);
			if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
				if(plugin.getState() == GameState.WAITING || plugin.getState() == GameState.STARTING) {
				} else {
					if(plugin.gameIsStarted()) {
						GameManager game = plugin.getGame();
						Player p = (Player) event.getEntity();
						if(plugin.getGame().getPlayers().contains(p.getUniqueId())) {
							Player damager = (Player) event.getDamager();
							if(game.getMurder() != damager.getUniqueId()) {
								return;
							}
							ItemStack itemInHand = damager.getInventory().getItemInMainHand();
							if(itemInHand.getType() != null && itemInHand.getType() == Material.IRON_SWORD) {
								game.eliminate(p);
							}
						}
					}
				}
			}
		}
		
		if(event.getCause() == DamageCause.PROJECTILE) {
			if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
				event.setCancelled(true);
				Arrow arrow = (Arrow) event.getDamager();
				if(arrow.getShooter() instanceof Player) {
					Player damager = (Player) arrow.getShooter();
					Player p = (Player) event.getEntity();
					if(damager.equals(p)) {
						return;
					}
					if(plugin.gameIsStarted()) {
						GameManager game = plugin.getGame();
						game.eliminate(p);
						if(!p.getUniqueId().equals(game.getMurder())) {
							damager.sendMessage("§cMurder §8»§7 Vous avez §ctué§7 un §cinnocent§7.");
							damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 1, true, false));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		 
        FastBoard board = plugin.getBoards().remove(p.getUniqueId());
		this.updateOnlinePlayers();

        if(board != null) {
            board.delete();
        }
        
        if(plugin.getState() == GameState.WAITING || plugin.getState() == GameState.STARTING) {
			Bukkit.broadcastMessage("§cMurder §8» §c" + p.getDisplayName() + "§7 a quitté la partie (§c" + (Bukkit.getOnlinePlayers().size() - 1) + "§7/§c16§7)");
        }
        
        if(plugin.gameIsStarted()) {
			Bukkit.broadcastMessage("§cMurder §8» §c" + p.getDisplayName() + "§7 a quitté la partie.");
        	plugin.getGame().eliminate(p);
        }
        
	}
	
	@EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        e.getEntity().remove();
    }
	
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			ItemStack item = event.getItem().getItemStack();
			if(p.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			if(p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
				event.setCancelled(true);
			}
			if(plugin.gameIsStarted()) {
				GameManager game = plugin.getGame();
				if(game.getMurder() == p.getUniqueId() && item.getType() == Material.BOW) {
					event.setCancelled(true);
				} else {
					if(item.getType() == Material.BOW) {
						Collection<Entity> nearbyEntites = p.getLocation().getWorld().getNearbyEntities(p.getLocation(), 8, 8, 8);
						p.sendMessage("§cMurder §8»§7 Vous avez récupéré §cl'arc§7 du détective.");
						p.getInventory().setItem(9, new ItemStack(Material.ARROW));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onUseBowEvent(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player) {
			if(plugin.gameIsStarted() && plugin.getState() != GameState.ENDING) {
				new BowCooldown((Player) event.getEntity()).runTaskTimer(plugin, 0, 2);
			}
		}
	}
	
	private void updateOnlinePlayers() {
		if(plugin.getState() == GameState.WAITING || plugin.getState() == GameState.STARTING) {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			for(Player player : players) { 
				FastBoard board = plugin.getPlayerBoard(player);
				if(board != null) {
					board.updateLine(5, "§7Joueurs: §c" + players.size() + "/16");
				}
			}
		}
	}
	
}
