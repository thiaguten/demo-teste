package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioDTOMapperTest {

    private static Usuario usuario;
    private static UsuarioDTO usuarioDTO;

    @InjectMocks
    private UsuarioDTOMapper usuarioDTOMapper;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setIdpId("1234abc");

        UsuarioIdentificado usuarioIdentificado = new UsuarioIdentificado();
        usuarioIdentificado.setCpf("123");
        usuarioIdentificado.setEmail("teste@email.com");
        usuarioIdentificado.setNomeUsuario("nome.usuario");
        usuarioIdentificado.setPrimeiroNome("Primeiro Nome");
        usuarioIdentificado.setUltimoNome("Ultimo Nome");
        usuarioIdentificado.setUsuario(usuario);
        usuarioIdentificado.setId(usuario.getId());

        usuario.setDetalhe(usuarioIdentificado);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setIdpId(usuario.getIdpId());
        usuarioDTO.setCpf(usuario.getDetalhe().getCpf());
        usuarioDTO.setEmail(usuario.getDetalhe().getEmail());
        usuarioDTO.setNomeUsuario(usuario.getDetalhe().getNomeUsuario());
        usuarioDTO.setPrimeiroNome(usuario.getDetalhe().getPrimeiroNome());
        usuarioDTO.setUltimoNome(usuario.getDetalhe().getUltimoNome());
    }

    @Test
    public void toDtoTest() {
        assertThat(usuario).isNotNull();
        UsuarioDTO usuarioDTOAtual = usuarioDTOMapper.toDto(usuario);
        assertThat(usuarioDTOAtual).usingRecursiveComparison().isEqualTo(usuarioDTO);
    }

    @Test
    public void fromDtoTest() {
        assertThat(usuarioDTO).isNotNull();
        Usuario usuarioAtual = usuarioDTOMapper.fromDto(usuarioDTO);
        assertThat(usuarioAtual).usingRecursiveComparison().isEqualTo(usuario);
    }

    @Test
    public void toDtoSemDetalheTest() {
        assertThat(usuario).isNotNull();
        usuario.setDetalhe(null);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setIdpId(usuario.getIdpId());

        UsuarioDTO usuarioDTOAtual = usuarioDTOMapper.toDto(usuario);
        assertThat(usuarioDTOAtual).usingRecursiveComparison().isEqualTo(usuarioDTO);
    }
}
