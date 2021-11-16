package dev.array21.multiplayerevents.events;

public interface MultiplayerEvent {
	
	public String getEnabledConfigOptionName();
	
	public boolean fireEvent();
}
