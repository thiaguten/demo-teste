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
        // given - condição prévia ou configuração
        assertThat(usuario).isNotNull();
        // when - ação ou o comportamento que estamos testando
        UsuarioDTO usuarioDTOAtual = usuarioDTOMapper.toDto(usuario);
        // then - verificar a saída
        assertThat(usuarioDTOAtual).usingRecursiveComparison().isEqualTo(usuarioDTO);
    }

    @Test
    public void fromDtoTest() {
        // given - condição prévia ou configuração
        assertThat(usuarioDTO).isNotNull();
        // when - ação ou o comportamento que estamos testando
        Usuario usuarioAtual = usuarioDTOMapper.fromDto(usuarioDTO);
        // then - verificar a saída
        assertThat(usuarioAtual).usingRecursiveComparison().isEqualTo(usuario);
    }

    @Test
    public void toDtoSemDetalheTest() {
        // given - condição prévia ou configuração
        assertThat(usuario).isNotNull();
        usuario.setDetalhe(null);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setIdpId(usuario.getIdpId());

        // when - ação ou o comportamento que estamos testando
        UsuarioDTO usuarioDTOAtual = usuarioDTOMapper.toDto(usuario);

        // then - verificar a saída
        assertThat(usuarioDTOAtual).usingRecursiveComparison().isEqualTo(usuarioDTO);
    }
}
