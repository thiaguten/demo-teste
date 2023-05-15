package com.example.demoteste.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("Não foi possível encontrar o usuário: (ID) " + id);
    }

    public UsuarioNotFoundException(Long id, Throwable cause) {
        super("Não foi possível encontrar o usuário: (ID) " + id, cause);
    }

    public UsuarioNotFoundException(String idpId) {
        super("Não foi possível encontrar o usuário: (IDP_ID) " + idpId);
    }

}
