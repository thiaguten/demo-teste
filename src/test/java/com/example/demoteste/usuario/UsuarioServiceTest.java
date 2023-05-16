package com.example.demoteste.usuario;

import static org.assertj.core.api.BDDAssertions.and; // assertThat
import static org.assertj.core.api.BDDAssertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
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
        // given - condição prévia ou configuração
        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setIdpId("123abc");

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(1L);
        usuarioExpected.setIdpId(usuarioNovo.getIdpId());

        given(usuarioRepository.save(usuarioNovo)).willReturn(usuarioExpected);

        // when - ação ou o comportamento que estamos testando
        Usuario usuarioSalvo = usuarioService.salvar(usuarioNovo);

        // then - verificar a saída
        then(usuarioRepository).should().save(usuarioNovo);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarioSalvo).usingRecursiveComparison().isEqualTo(usuarioExpected);
    }

    @Test
    public void recuperarTest() {
        // given - condição prévia ou configuração
        Long id = 1L;

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(id);
        usuarioExpected.setIdpId("123abc");

        given(usuarioRepository.findByIdJoinFetch(id)).willReturn(Optional.of(usuarioExpected));

        // when - ação ou o comportamento que estamos testando
        Optional<Usuario> usuarioRecuperadoOptional = usuarioService.recuperar(id);

        // then - verificar a saída
        then(usuarioRepository).should().findByIdJoinFetch(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarioRecuperadoOptional).hasValue(usuarioExpected);
    }

    @Test
    public void recuperarIdPTest() {
        // given - condição prévia ou configuração
        String idPId = "123abc";

        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(1L);
        usuarioExpected.setIdpId(idPId);

        given(usuarioRepository.findByIdpIdJoinFetch(idPId)).willReturn(Optional.of(usuarioExpected));

        // when - ação ou o comportamento que estamos testando
        Optional<Usuario> usuarioRecuperadoOptional = usuarioService.recuperar(idPId);

        // then - verificar a saída
        then(usuarioRepository).should().findByIdpIdJoinFetch(idPId);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarioRecuperadoOptional).hasValue(usuarioExpected);
    }

    @Test
    public void deletarTest() {
        // given - condição prévia ou configuração
        Long id = 1L;
        willDoNothing().given(usuarioRepository).deleteById(id);

        // when - ação ou o comportamento que estamos testando
        usuarioService.deletar(id);

        // then - verificar a saída
        then(usuarioRepository).should().deleteById(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // given - condição prévia ou configuração
        Long id = 1L;
        EmptyResultDataAccessException exception = new EmptyResultDataAccessException(
                "No class com.example.demoteste.usuario.Usuario entity with id 1 exists!", 0);
        willThrow(exception).given(usuarioRepository).deleteById(id);

        // when - ação ou o comportamento que estamos testando
        ThrowingCallable deletarPorId = () -> usuarioService.deletar(id);

        // then - verificar a saída
        Exception exceptionEsperada = catchException(deletarPorId);
        then(usuarioRepository).should().deleteById(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(exceptionEsperada)
                .isExactlyInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Não foi possível encontrar o usuário: (ID) " + id)
                .hasRootCauseExactlyInstanceOf(EmptyResultDataAccessException.class)
                .hasRootCauseMessage("No class com.example.demoteste.usuario.Usuario entity with id 1 exists!");
    }

    static Stream<List<Usuario>> usuarioListProvider() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setIdpId("123abc");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setIdpId("456abc");

        return Stream.of(Arrays.asList(usuario1, usuario2), null);
    }

    @ParameterizedTest
    @MethodSource("usuarioListProvider")
    public void listar2Test(List<Usuario> usuariosExpected) {
        // given - condição prévia ou configuração
        given(usuarioRepository.findAll()).willReturn(usuariosExpected);

        // when - ação ou o comportamento que estamos testando
        List<Usuario> usuarios = usuarioService.listar();

        // then - verificar a saída
        then(usuarioRepository).should().findAll();
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarios).isEqualTo(usuariosExpected);
    }

    @Test
    public void obterReferenciaTest() {
        // given - condição prévia ou configuração
        Long id = 1L;
        Usuario usuarioExpected = new Usuario();
        usuarioExpected.setId(id);
        usuarioExpected.setIdpId("123abc");

        given(usuarioRepository.getReferenceById(id)).willReturn(usuarioExpected);

        // when - ação ou o comportamento que estamos testando
        Usuario referencia = usuarioService.obterReferencia(id);

        // then - verificar a saída
        then(usuarioRepository).should().getReferenceById(id);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(referencia).isEqualTo(usuarioExpected);
    }

    @Test
    public void atualizarTest() {
        // given - condição prévia ou configuração
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

        // mockando comportamento do .map(...)
        given(usuarioRepository.findByIdJoinFetch(id)).willReturn(Optional.of(usuarioRecuperado));
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioAlteracao);

        // when - ação ou o comportamento que estamos testando
        Usuario usuarioAlterado = usuarioService.atualizar(usuarioAlteracao, id);

        // then - verificar a saída
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        then(usuarioRepository).should().findByIdJoinFetch(id);
        then(usuarioRepository).should().save(usuarioCaptor.capture());
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarioAlterado).usingRecursiveComparison().isEqualTo(usuarioAlteracao);
        and.then(usuarioAlterado).usingRecursiveComparison().isEqualTo(usuarioCaptor.getValue());
    }

    @Test
    public void deveSalvarUsuarioAoAtualizarCasoNaoExistaTest() {
        // given - condição prévia ou configuração
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

        // mockando comportamento do .orElseGet(...)
        given(usuarioRepository.findByIdJoinFetch(id)).willReturn(Optional.empty());
        given(usuarioRepository.save(usuarioNovo)).willReturn(usuarioNovo);

        // when - ação ou o comportamento que estamos testando
        Usuario usuarioSalvo = usuarioService.atualizar(usuarioNovo, id);

        // then - verificar a saída
        then(usuarioRepository).should().findByIdJoinFetch(id);
        then(usuarioRepository).should().save(usuarioNovo);
        then(usuarioRepository).shouldHaveNoMoreInteractions();
        and.then(usuarioSalvo).usingRecursiveComparison().isEqualTo(usuarioNovo);
    }
}
