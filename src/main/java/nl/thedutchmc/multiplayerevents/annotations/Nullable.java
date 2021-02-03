package nl.thedutchmc.multiplayerevents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This indicates that you should do a null check on the returned value
 */
@Documented
@Target(ElementType.METHOD)
public @interface Nullable {

}
