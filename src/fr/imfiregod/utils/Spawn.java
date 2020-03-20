package fr.imfiregod.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Spawn {

	private double x;
	private double y;
	private double z;
	private String worldName;

	public Spawn(double x, double y, double z, String worldName) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldName = worldName;
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
	}
	
}