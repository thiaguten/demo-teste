package com.example.demoteste.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.detalhe WHERE u.id = :id")
    Optional<Usuario> findByIdJoinFetch(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.detalhe WHERE u.idpId = :idpId")
    Optional<Usuario> findByIdpIdJoinFetch(@Param("idpId") String idpId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.detalhe WHERE u.detalhe.ultimoNome = :ultimoNome")
    List<Usuario> findByUltimoNomeJoinFetch(@Param("ultimoNome") String ultimoNome);

}
