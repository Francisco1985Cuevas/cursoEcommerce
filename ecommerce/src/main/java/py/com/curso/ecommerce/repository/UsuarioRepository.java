package py.com.curso.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.curso.ecommerce.model.Usuario;

import java.util.Optional;

/*PROBAR SI FUNCIONA CON LA ANOTACION REPOSITORY O NO HACE FALTA...*/
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

}
