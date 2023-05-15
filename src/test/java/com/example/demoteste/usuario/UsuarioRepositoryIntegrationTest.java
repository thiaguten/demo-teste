package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@DataJpaTest(showSql = false)
public class UsuarioRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();

        UsuarioIdentificado usuarioIdentificado = new UsuarioIdentificado();
        usuarioIdentificado.setCpf("11122233344");
        usuarioIdentificado.setEmail("thiago@email.com");
        usuarioIdentificado.setNomeUsuario("thiago");
        usuarioIdentificado.setPrimeiroNome("Thiago");
        usuarioIdentificado.setUltimoNome("Bla bla bla");
        usuarioIdentificado.setUsuario(usuario);

        usuario.setIdpId(UUID.randomUUID().toString());
        usuario.setDetalhe(usuarioIdentificado);
    }

    @Test
    public void testFindByIdJoinFetch() {
        assertThat(usuario).isNotNull();
        assertThat(usuario.getDetalhe()).isNotNull();
        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getDetalhe().getId()).isNull();
        // given
        Usuario usuarioSalvo = entityManager.persist(usuario);
        // when
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdJoinFetch(usuarioSalvo.getId());
        // then
        assertThat(usuarioOptional)
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(usuarioSalvo.getId());
                    assertThat(u.getDetalhe().getId()).isEqualTo(usuarioSalvo.getId());
                    assertThat(u.getDetalhe()).usingRecursiveComparison().isEqualTo(usuario.getDetalhe());
                });
    }

    @Test
    public void testFindByIdpIdJoinFetch() {
        assertThat(usuario).isNotNull();
        assertThat(usuario.getDetalhe()).isNotNull();
        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getDetalhe().getId()).isNull();
        // given
        Usuario usuarioSalvo = entityManager.persist(usuario);
        // when
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdpIdJoinFetch(usuario.getIdpId());
        // then
        assertThat(usuarioOptional)
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(usuarioSalvo.getId());
                    assertThat(u.getDetalhe().getId()).isEqualTo(usuarioSalvo.getId());
                    assertThat(u).usingRecursiveComparison().isEqualTo(usuario);
                });
    }

    @Test
    public void testFindByUltimoNome() {
        assertThat(usuario).isNotNull();
        assertThat(usuario.getDetalhe()).isNotNull();
        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getDetalhe().getId()).isNull();

        UsuarioIdentificado usuarioIdentificado = usuario.getDetalhe();
        // given
        entityManager.persist(usuario);
        // when
        List<Usuario> usuarios = usuarioRepository.findByUltimoNomeJoinFetch(usuarioIdentificado.getUltimoNome());
        // then
        assertThat(usuarios)
                .extracting(Usuario::getDetalhe)
                .extracting(UsuarioIdentificado::getUltimoNome)
                .containsOnly(usuarioIdentificado.getUltimoNome());
    }
}
