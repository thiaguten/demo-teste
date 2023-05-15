package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    // @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioServiceImpl(usuarioRepository);
    }

    @Test
    public void salvarTest() {
        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setIdpId("123abc");

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(1L);
        usuarioExpected.setIdpId(usuarioNovo.getIdpId());

        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioExpected);

        Usuario usuarioSalvo = usuarioService.salvar(usuarioNovo);

        verify(usuarioRepository).save(usuarioNovo);
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(usuarioSalvo).usingRecursiveComparison().isEqualTo(usuarioExpected);
    }

    @Test
    public void recuperarTest() {
        Long id = 1L;

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(id);
        usuarioExpected.setIdpId("123abc");

        given(usuarioRepository.findByIdJoinFetch(anyLong())).willReturn(Optional.of(usuarioExpected));

        Optional<Usuario> usuarioRecuperadoOptional = usuarioService.recuperar(id);

        verify(usuarioRepository).findByIdJoinFetch(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        // assertThat(usuarioRecuperadoOptional).hasValue(usuarioExpected);
        assertThat(usuarioRecuperadoOptional).hasValueSatisfying(usuarioRecuperado -> {
            assertThat(usuarioRecuperado).usingRecursiveComparison().isEqualTo(usuarioExpected);
        });
    }

    @Test
    public void recuperarIdPTest() {
        String idPId = "123abc";

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(1L);
        usuarioExpected.setIdpId(idPId);

        given(usuarioRepository.findByIdpIdJoinFetch(anyString())).willReturn(Optional.of(usuarioExpected));

        Optional<Usuario> usuarioRecuperadoOptional = usuarioService.recuperar(idPId);

        verify(usuarioRepository).findByIdpIdJoinFetch(idPId);
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        // assertThat(usuarioRecuperadoOptional).hasValue(usuarioExpected);
        assertThat(usuarioRecuperadoOptional).hasValueSatisfying(usuarioRecuperado -> {
            assertThat(usuarioRecuperado).usingRecursiveComparison().isEqualTo(usuarioExpected);
        });
    }

    @Test
    public void deletarTest() {
        Long id = 1L;
        doNothing().when(usuarioRepository).deleteById(anyLong());
        usuarioService.deletar(id);
        verify(usuarioRepository).deleteById(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // given
        Long id = 1L;
        EmptyResultDataAccessException exception = new EmptyResultDataAccessException(
                "No class com.example.demoteste.usuario.Usuario entity with id 1 exists!", 0);
        doThrow(exception).when(usuarioRepository).deleteById(anyLong());

        UsuarioNotFoundException exceptionEsperada = assertThrows(UsuarioNotFoundException.class,
                () -> usuarioService.deletar(id));

        verify(usuarioRepository).deleteById(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(exceptionEsperada)
                .isExactlyInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Não foi possível encontrar o usuário: (ID) 1")
                .hasRootCauseExactlyInstanceOf(EmptyResultDataAccessException.class)
                .hasRootCauseMessage("No class com.example.demoteste.usuario.Usuario entity with id 1 exists!");
    }

    static Stream<List<Usuario>> usuariosProvider() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setIdpId("123abc");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setIdpId("456abc");

        return Stream.of(Arrays.asList(usuario1, usuario2), null);
    }

    @ParameterizedTest
    @MethodSource("usuariosProvider")
    public void listar2Test(List<Usuario> usuariosExpected) {
        given(usuarioRepository.findAll()).willReturn(usuariosExpected);

        List<Usuario> usuarios = usuarioService.listar();

        verify(usuarioRepository).findAll();
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(usuarios).isEqualTo(usuariosExpected);
    }

    @Test
    public void obterReferenciaTest() {
        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(1L);
        usuarioExpected.setIdpId("123abc");

        given(usuarioRepository.getReferenceById(anyLong())).willReturn(usuarioExpected);

        Usuario referencia = usuarioService.obterReferencia(1L);

        verify(usuarioRepository).getReferenceById(1L);
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(referencia).isEqualTo(usuarioExpected);
    }

    @Test
    public void atualizarTest() {
        final Long id = 1L;
        final String idIdP = "123abc"; // natural_id
        final String cpf = "33333333333"; // natural_id
        final String nomeUsuario = "lalalala"; // natural_id

        Usuario usuarioRecuperado = new Usuario();
        usuarioRecuperado.setId(id);
        usuarioRecuperado.setIdpId(idIdP);
        UsuarioIdentificado usuarioRecuperadoDetalhe = new UsuarioIdentificado();
        usuarioRecuperadoDetalhe.setId(usuarioRecuperado.getId());
        usuarioRecuperadoDetalhe.setCpf(cpf);
        usuarioRecuperadoDetalhe.setNomeUsuario(nomeUsuario);
        usuarioRecuperadoDetalhe.setPrimeiroNome("thiago");
        usuarioRecuperadoDetalhe.setUltimoNome("teste");
        usuarioRecuperadoDetalhe.setEmail("lalalala@email.com");
        usuarioRecuperado.setDetalhe(usuarioRecuperadoDetalhe);

        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setId(id);
        usuarioAlteracao.setIdpId(idIdP);
        UsuarioIdentificado usuarioAlteracaoDetalhe = new UsuarioIdentificado();
        usuarioAlteracaoDetalhe.setId(usuarioAlteracao.getId());
        usuarioAlteracaoDetalhe.setCpf(cpf);
        usuarioAlteracaoDetalhe.setNomeUsuario(nomeUsuario);
        usuarioAlteracaoDetalhe.setPrimeiroNome("thiago2");
        usuarioAlteracao.setDetalhe(usuarioAlteracaoDetalhe);

        // .map
        given(usuarioRepository.findByIdJoinFetch(anyLong())).willReturn(Optional.of(usuarioRecuperado));
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioAlteracao);

        Usuario usuarioAlterado = usuarioService.atualizar(usuarioAlteracao, id);

        verify(usuarioRepository).findByIdJoinFetch(id);
        verify(usuarioRepository).save(any(Usuario.class));
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(usuarioAlterado).usingRecursiveComparison().isEqualTo(usuarioAlteracao);
    }

    @Test
    public void deveSalvarUsuarioAoAtualizarCasoNaoExistaTest() {
        final Long id = 1L;
        final String idIdP = "123abc"; // natural_id
        final String cpf = "33333333333"; // natural_id
        final String nomeUsuario = "lalalala"; // natural_id

        Usuario usuarioRecuperado = new Usuario();
        usuarioRecuperado.setId(id);
        usuarioRecuperado.setIdpId(idIdP);
        UsuarioIdentificado usuarioRecuperadoDetalhe = new UsuarioIdentificado();
        usuarioRecuperadoDetalhe.setId(usuarioRecuperado.getId());
        usuarioRecuperadoDetalhe.setCpf(cpf);
        usuarioRecuperadoDetalhe.setNomeUsuario(nomeUsuario);
        usuarioRecuperadoDetalhe.setPrimeiroNome("thiago");
        usuarioRecuperadoDetalhe.setUltimoNome("teste");
        usuarioRecuperadoDetalhe.setEmail("lalalala@email.com");
        usuarioRecuperado.setDetalhe(usuarioRecuperadoDetalhe);

        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setId(id);
        usuarioNovo.setIdpId(idIdP);
        UsuarioIdentificado usuarioNovoDetalhe = new UsuarioIdentificado();
        usuarioNovoDetalhe.setId(usuarioNovo.getId());
        usuarioNovoDetalhe.setCpf(cpf);
        usuarioNovoDetalhe.setNomeUsuario(nomeUsuario);
        usuarioNovoDetalhe.setPrimeiroNome("thiago2");
        usuarioNovo.setDetalhe(usuarioNovoDetalhe);

        // .orElseGet
        given(usuarioRepository.findByIdJoinFetch(anyLong())).willReturn(Optional.empty());
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioNovo);

        Usuario usuarioSalvo = usuarioService.atualizar(usuarioNovo, id);

        verify(usuarioRepository).findByIdJoinFetch(id);
        verify(usuarioRepository).save(any(Usuario.class));
        then(usuarioRepository).shouldHaveNoMoreInteractions();

        assertThat(usuarioSalvo).usingRecursiveComparison().isEqualTo(usuarioNovo);
    }

    @Disabled("TODO sem informar o ID/idIdP, cpf e nomeUsuario lanca erro")
    @Test
    public void atualizarTest2() {
        // TODO
    }
}
