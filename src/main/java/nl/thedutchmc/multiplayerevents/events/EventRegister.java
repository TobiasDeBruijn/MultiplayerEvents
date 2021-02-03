package nl.thedutchmc.multiplayerevents.events;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.annotations.RegisterMultiplayerEvent;
import nl.thedutchmc.multiplayerevents.utils.ReflectionUtils;
import nl.thedutchmc.multiplayerevents.utils.Utils;

public class EventRegister {

	private List<MultiplayerEvent> events = new ArrayList<>();
	
	private MultiplayerEvents plugin;	
	
	public EventRegister(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Find all events annotated with {@link RegisterMultiplayerEvent} and register them if they are enabled
	 */
	public void discoverAndRegisterEvents() {
		
		List<String> annotatedClasses = plugin.getAnnotatedClasses(RegisterMultiplayerEvent.class);
		if(annotatedClasses != null) {
			
			ConfigurationHandler config = new ConfigurationHandler(plugin);
			
			//Loop over all classes found
			for(String s : annotatedClasses) {
				Class<?> clazz = ReflectionUtils.getClass(s);
				
				//Get all interfaces that the class implements, and check that it implements MultiplayerEvent
				List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
				if(!interfaces.contains(MultiplayerEvent.class)) {
					throw new RuntimeException("Class annotated with RegisterMultiplayerEvent but does not implement MultiplayerEvent");
				}
				
				//Get the constructor and instantiate the class
				Constructor<?> constructor = ReflectionUtils.getConstructor(clazz, MultiplayerEvents.class);
				MultiplayerEvent clazzInstance = (MultiplayerEvent) ReflectionUtils.createInstance(constructor, this.plugin);
				
				//Get if we should enable the event
				Boolean shouldEnable = (Boolean) config.getConfigOption(clazzInstance.getEnabledConfigOptionName());
				if(shouldEnable == null) {
					throw new RuntimeException("Configuration option " + clazzInstance.getEnabledConfigOptionName() + " for " + clazz.getCanonicalName() + " not found!");
				}
				
				MultiplayerEvents.logDebug("Discovered " + clazz.getCanonicalName() + ". " + ((shouldEnable) ? "Enabling event" : "Not enabling because it is not enabled in the config."));
				
				//If we should enable it, add it to the list of events
				if(shouldEnable) {
					this.events.add(clazzInstance);
				}
			}
		}
	}
	
	/**
	 * This method will register the provided event
	 * @param multiplayerEvent The even to register
	 */
	public void registerNewEvent(MultiplayerEvent multiplayerEvent) {
		this.events.add(multiplayerEvent);
	}
	
	/**
	 * Get a random event from the list of registered events
	 * @return Returns a random event
	 */
	public MultiplayerEvent getRandomEvent() {
		int randomIndex = Utils.getRandomInt(0, events.size() -1);
		return events.get(randomIndex);
	}
}
