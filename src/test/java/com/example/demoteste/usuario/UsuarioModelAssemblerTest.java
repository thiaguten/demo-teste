package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioModelAssemblerTest {

    @Mock
    private UsuarioDTOMapper usuarioDTOMapper;

    @InjectMocks
    private UsuarioModelAssembler usuarioModelAssembler;

    @Test
    public void toModelTest() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(id);
        when(usuarioDTOMapper.toDto(any(Usuario.class))).thenReturn(usuarioDTO);

        EntityModel<UsuarioDTO> model = usuarioModelAssembler.toModel(usuario);

        verify(usuarioDTOMapper).toDto(usuario);

        // assertThat(model.getRequiredLink(IanaLinkRelations.SELF)).isEqualTo(Link.of("/api/v1/usuarios/1"));

        assertThat(model.getRequiredLink(IanaLinkRelations.SELF))
                .extracting(Link::getHref)
                .isEqualTo("/api/v1/usuarios/1");
    }
}
