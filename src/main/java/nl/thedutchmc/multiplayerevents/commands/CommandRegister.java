package nl.thedutchmc.multiplayerevents.commands;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.annotations.RegisterMeCommandExecutor;
import nl.thedutchmc.multiplayerevents.commands.interfaces.MeCommandExecutor;
import nl.thedutchmc.multiplayerevents.exceptions.NoSuchPluginCommandException;
import nl.thedutchmc.multiplayerevents.utils.ReflectionUtils;

public class CommandRegister {

	private List<MeCommandExecutor> pluginCommandExecutors = new ArrayList<>();
	private MultiplayerEvents plugin;
	
	public CommandRegister(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	/**|
	 * Get all MeCommandExecutor's
	 */
	public void discoverPluginCommands() {
		
		List<String> annotatedClasses = plugin.getAnnotatedClasses(RegisterMeCommandExecutor.class);
		if(annotatedClasses != null) {
			
			//Loop over every class found
			for(String s : annotatedClasses) {
				Class<?> clazz = ReflectionUtils.getClass(s);
				
				//Get all interfaces that the class implements and check if it implements the MeCommandExecutor
				List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
				if(!interfaces.contains(MeCommandExecutor.class)) {
					throw new RuntimeException("Class annotated with RegisterMeCommandExecutor but does not implement MeCommandExecutor");
				}
				
				//Get the constructor and instantiate the class
				Constructor<?> constructor = ReflectionUtils.getConstructor(clazz);
				Object clazzInstance = ReflectionUtils.createInstance(constructor);
				
				//Add it to the list
				pluginCommandExecutors.add((MeCommandExecutor) clazzInstance);
				
				MultiplayerEvents.logDebug("Discovered MeCommandExecutor: " + clazz.getCanonicalName());
			}
		}
	}
	
	/**
	 * Get a MeCommandExecutor
	 * @param name The name of the command
	 * @throws NoSuchPluginCommandException Thrown when no MeCommandExecutor was found for the provided name
	 */
	public MeCommandExecutor getPluginCommand(String name) throws NoSuchPluginCommandException {
		for(MeCommandExecutor mce : this.pluginCommandExecutors) {
			if(mce.getName().equalsIgnoreCase(name)) {
				return mce;
			}
		}
		
		throw new NoSuchPluginCommandException();
	}
	
	/**
	 * Get all available MeCommandExecutors name's
	 */
	public List<String> getAllCommands() {
		List<String> commands = new ArrayList<>();
		for(MeCommandExecutor mce : this.pluginCommandExecutors) {
			commands.add(mce.getName());
		}
		
		return commands;
	}
}
