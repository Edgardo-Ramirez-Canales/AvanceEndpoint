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