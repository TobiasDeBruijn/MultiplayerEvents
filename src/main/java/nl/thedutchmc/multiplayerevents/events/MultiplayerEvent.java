package nl.thedutchmc.multiplayerevents.events;

public interface MultiplayerEvent {
	
	public String getEnabledConfigOptionName();
	
	public boolean fireEvent();
}
