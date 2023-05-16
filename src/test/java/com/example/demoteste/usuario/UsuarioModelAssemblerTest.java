package com.example.demoteste.usuario;

import static org.assertj.core.api.BDDAssertions.and; // assertThat
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        // given - condição prévia ou configuração
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(id);
        given(usuarioDTOMapper.toDto(any(Usuario.class))).willReturn(usuarioDTO);

        // when - ação ou o comportamento que estamos testando
        EntityModel<UsuarioDTO> model = usuarioModelAssembler.toModel(usuario);

        // then - verificar a saída
        then(usuarioDTOMapper).should().toDto(usuario);
        then(usuarioDTOMapper).shouldHaveNoMoreInteractions();

        // and.then(model.getRequiredLink(IanaLinkRelations.SELF))
        // .isEqualTo(Link.of("/api/v1/usuarios/1"));
        and.then(model.getRequiredLink(IanaLinkRelations.SELF))
                .extracting(Link::getHref)
                .isEqualTo("/api/v1/usuarios/1");
    }
}
