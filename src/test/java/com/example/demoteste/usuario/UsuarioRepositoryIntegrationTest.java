package com.example.demoteste.usuario;

import static org.assertj.core.api.BDDAssertions.then; // assertThat
import static org.assertj.core.api.BDDAssumptions.given; // assumeThat

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
        // given - condição prévia ou configuração
        given(usuario).isNotNull();
        given(usuario.getDetalhe()).isNotNull();
        given(usuario.getId()).isNull();
        given(usuario.getDetalhe().getId()).isNull();
        given(usuario.getIdpId()).isNotNull();

        Usuario usuarioSalvo = entityManager.persist(usuario);

        // when - ação ou o comportamento que estamos testando
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdJoinFetch(usuarioSalvo.getId());

        // then - verificar a saída
        then(usuarioOptional)
                .as("Verifica se o usuário pesquisado satisfaz as condições abaixo")
                .hasValueSatisfying(u -> {
                    then(u.getId())
                            .as("Verifica se ID do usuário pesquisado é igual ao do persistido")
                            .isEqualTo(usuarioSalvo.getId());
                    then(u.getDetalhe().getId())
                            .as("Verifica se detalhe ID do usuário pesquisado é igual ao do persistido")
                            .isEqualTo(usuarioSalvo.getId());
                    then(u.getDetalhe()).usingRecursiveComparison()
                            .as("Verifica se dados do detalhe do usuário pesquisado são iguals aos do persistido")
                            .isEqualTo(usuarioSalvo.getDetalhe());
                });
    }

    @Test
    public void testFindByIdpIdJoinFetch() {
        // given - condição prévia ou configuração
        given(usuario).isNotNull();
        given(usuario.getDetalhe()).isNotNull();
        given(usuario.getId()).isNull();
        given(usuario.getDetalhe().getId()).isNull();
        given(usuario.getIdpId()).isNotNull();

        Usuario usuarioSalvo = entityManager.persist(usuario);

        // when - ação ou o comportamento que estamos testando
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdpIdJoinFetch(usuarioSalvo.getIdpId());

        // then - verificar a saída
        then(usuarioOptional)
                .hasValueSatisfying(u -> {
                    then(u.getId()).isEqualTo(usuarioSalvo.getId());
                    then(u.getDetalhe().getId()).isEqualTo(usuarioSalvo.getId());
                    then(u).usingRecursiveComparison().isEqualTo(usuarioSalvo);
                });
    }

    @Test
    public void testFindByUltimoNome() {
        // given - condição prévia ou configuração
        given(usuario).isNotNull();
        given(usuario.getDetalhe()).isNotNull();
        given(usuario.getId()).isNull();
        given(usuario.getDetalhe().getId()).isNull();
        given(usuario.getIdpId()).isNotNull();

        Usuario usuarioSalvo = entityManager.persist(usuario);

        // when - ação ou o comportamento que estamos testando
        UsuarioIdentificado usuarioSalvoDetalhe = usuarioSalvo.getDetalhe();
        List<Usuario> usuarios = usuarioRepository.findByUltimoNomeJoinFetch(usuarioSalvoDetalhe.getUltimoNome());

        // then - verificar a saída
        then(usuarios)
                .extracting(Usuario::getDetalhe)
                .extracting(UsuarioIdentificado::getUltimoNome)
                .containsOnly(usuarioSalvoDetalhe.getUltimoNome());
    }
}
