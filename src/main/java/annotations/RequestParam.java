package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME) // Pour qu'elle soit accessible à l'exécution via la réflexion
@Target(ElementType.PARAMETER) 
public @interface RequestParam {
    String name(); 
}
