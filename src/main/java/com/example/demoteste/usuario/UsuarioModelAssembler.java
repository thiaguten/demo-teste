package com.example.demoteste.usuario;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<UsuarioDTO>> {

    @Autowired
    private UsuarioDTOMapper dtoMapper;

    @Override
    public EntityModel<UsuarioDTO> toModel(Usuario usuario) {
        Long id = usuario.getId();
        UsuarioDTO usuarioDto = dtoMapper.toDto(usuario);
        return EntityModel.of(usuarioDto,
                linkTo(methodOn(UsuarioController.class).recuperar(id)).withSelfRel()
                        .andAffordance(afford(methodOn(UsuarioController.class).atualizar(null, id)))
                        .andAffordance(afford(methodOn(UsuarioController.class).deletar(id))),
                linkTo(methodOn(UsuarioController.class).listar()).withRel("/api/v1/usuarios"));
    }

}
