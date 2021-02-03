package nl.thedutchmc.multiplayerevents.annotations;

import java.lang.annotation.Documented;

import org.atteo.classindex.IndexAnnotated;

/**
 * This will add the annotated class to the list of MeCommandExecutor's
 * The annotated class <strong>must</strong> implement MeCommandExecutor
 */
@Documented
@IndexAnnotated
public @interface RegisterMeCommandExecutor {}