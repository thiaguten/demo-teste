package com.example.demoteste.usuario;

import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
public interface UsuarioService {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> recuperar(Long id);

    Optional<Usuario> recuperar(String idpId);

    Usuario atualizar(Usuario novoUsuario, Long id);

    void deletar(Long id);

    List<Usuario> listar();

    Usuario obterReferencia(Long id);

}
