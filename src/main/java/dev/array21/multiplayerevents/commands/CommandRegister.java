package dev.array21.multiplayerevents.commands;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.annotations.CommandInfo;
import dev.array21.multiplayerevents.annotations.RegisterMeCommandExecutor;
import dev.array21.multiplayerevents.commands.interfaces.MeCommandExecutor;
import dev.array21.multiplayerevents.exceptions.NoSuchPluginCommandException;
import dev.array21.multiplayerevents.utils.ReflectionUtils;

public class CommandRegister {

	private HashMap<String, MeCommandExecutor> pluginCommandExecutors = new HashMap<>();
	private MultiplayerEvents plugin;
	
	public CommandRegister(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	/**
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
				
				CommandInfo info = clazz.getAnnotation(CommandInfo.class);
				
				//Get the constructor and instantiate the class
				Constructor<?> constructor = ReflectionUtils.getConstructor(clazz);
				Object clazzInstance = ReflectionUtils.createInstance(constructor);
				
				//Add it to the list
				pluginCommandExecutors.put(info.name(), (MeCommandExecutor) clazzInstance);
				
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
		MeCommandExecutor mce = this.pluginCommandExecutors.get(name);
		
		if(mce == null) {
			throw new NoSuchPluginCommandException();
		}
		
		return mce;
	}
	
	/**
	 * Get all available MeCommandExecutors name's
	 */
	public Set<String> getAllCommands() {
		return this.pluginCommandExecutors.keySet();
	}
}
