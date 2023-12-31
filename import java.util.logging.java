import java.util.logging.Logger;
import java.util.logging.Level;

public class LoginDAO {

    private TokenGenerator tokenGenerator = new TokenGenerator();
    private static final Logger LOGGER = Logger.getLogger(LoginDAO.class.getName());

    public String autenticar(String user, String pass) {
        try {
            Ldap ldap = new Ldap();
            if (!ldap.autenticar(user, pass)) {
                LOGGER.info("Error en la autenticación en Active Directory: Credenciales Inválidas");
                return "Credenciales Inválidas en el Active Directory";
            }

            if (!existeUsuario(user)) {
                LOGGER.info("El usuario no existe en la base de datos");
                return "Usuario no existe en la base de datos";
            }

            LOGGER.info("Autenticación exitosa en Active Directory y Base de Datos");
            String[] roles = obtenerRolesUsuario(user);
            return tokenGenerator.generateToken(user, roles);

        } catch (LDAPException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en la autenticación: ", e);
            throw new AuthenticationException("Error en la autenticación: " + e.getMessage());
        }
    }

    // ... resto del código ...
}

------------------------000000000000000000000----------------------------------
------------------------000000000000000000000----------------------------------

public String autenticar(String user, String pass) {
        try {
            Ldap ldap = new Ldap();
            boolean validoAD = ldap.autenticar(user, pass);

            if (validoAD){
                System.out.println("Autenticado exitosamente en el active Directory");
            
                // Segunda Verificacion: BD
                if (existeUsuario(user)){
                    System.out.println("El usuario Existe en la Base de Datos");
                    //Generar Token si es necesario
                    String[] roles = obtenerRolesUsuario(user);
                    String token = tokenGenerator.generateToken(user, roles);
                    return token;
                } else {
                System.out.println("El usuario no existe en la base de datos");
                return "Usuario no exite en la base de datos";
                }
            }else{ 
             System.out.println("Eroor en la autenticacion en Active Directory: Credenciales Invalidas");
             return "Credenciales Invalidas en el Active Directory";   
            }
        } catch (Exception ex){
        System.out.println("Error en la autenticacion:" + ex.getMessage());
        return "Error en la autenticacion:" + ex.getMessage();
            
        }
    }
------------------------000000000000000000000----------------------------------
2222222222222222222222222222222222222222222222222222

package dao;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Collections;

//@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {
    
    private TokenGenerator tokenGenerator = new TokenGenerator();

    @Context
    private ResourceInfo resourceInfo;
    
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(AuthorizationRequired.class)) {
            // Obtener los roles de la anotación
            String[] roles = method.getAnnotation(AuthorizationRequired.class).roles();

            // Obtener el token del encabezado
            String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            token = token.substring(7); // Eliminar "Bearer "

            // Verificar el token y los roles
            if (!verifyTokenAndRoles(token, roles)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            }
        }
    }

    private boolean verifyTokenAndRoles(String token, String[] requiredRoles) {
        DecodedJWT decodedJWT = tokenGenerator.verifyToken(token);
            if (decodedJWT == null) {
                return false;
            }
            List<String> tokenRoles = getRolesFromToken(decodedJWT);
            for (String requiredRole : requiredRoles) {
                if (tokenRoles.contains(requiredRole)) {
                return true;
                }
            }
        return false;
    }

    private List<String> getRolesFromToken(DecodedJWT jwt) {
        Claim rolesClaim = jwt.getClaim("roles"); // Asume que los roles están en un claim llamado "roles"
        if (!rolesClaim.isNull()) {
            return rolesClaim.asList(String.class);
        }
        return Collections.emptyList();
    }
    
    
    public static void main(String[] args) {
        TokenGenerator tokenGenerator = new TokenGenerator();
        AuthorizationFilter filter = new AuthorizationFilter();

        String usuarioDePrueba = "edgardo";
        String[] rolesDePrueba = {"admin", "usuario"};
        String token = tokenGenerator.generateToken(usuarioDePrueba, rolesDePrueba);

        // Asumiendo que el token contiene los roles "admin" y "usuario"
        String[] requiredRoles = {"admin"};
        boolean accesoPermitido = filter.verifyTokenAndRoles(token, requiredRoles);

        System.out.println("Acceso Permitido: " + accesoPermitido);
        
        
        DecodedJWT decodedJWT = tokenGenerator.verifyToken(token);
            if (decodedJWT != null) {
                List<String> roles = filter.getRolesFromToken(decodedJWT);
                System.out.println("Roles en el token: " + roles);
            } else {
            System.out.println("El token no es válido.");
        }
    }
    
    

}



222222222222222222222222222222222222222222222222222222