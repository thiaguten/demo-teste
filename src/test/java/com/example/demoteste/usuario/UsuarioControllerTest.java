package com.example.demoteste.usuario;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.BDDAssumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioModelAssembler usuarioModelAssembler;

    @MockBean
    private UsuarioDTOMapper usuarioDTOMapper;

    private Usuario usuario;
    private UsuarioDTO usuarioDto;
    private EntityModel<UsuarioDTO> entityModel;

    @BeforeEach
    public void setUp() {
        final Long id = 1L;

        usuarioDto = new UsuarioDTO();
        usuarioDto.setId(id);
        usuarioDto.setIdpId("123abc");

        usuario = new Usuario();
        usuario.setId(usuarioDto.getId());
        usuario.setIdpId(usuarioDto.getIdpId());

        entityModel = EntityModel.of(usuarioDto,
                linkTo(methodOn(UsuarioController.class).recuperar(id)).withSelfRel()
                        .andAffordance(afford(methodOn(UsuarioController.class).atualizar(null, id)))
                        .andAffordance(afford(methodOn(UsuarioController.class).deletar(id))),
                linkTo(methodOn(UsuarioController.class).listar()).withRel("/api/v1/usuarios"));
    }

    @Test
    public void listarUsuariosTest() throws Exception {
        // given - condição prévia ou configuração
        List<Usuario> usuarios = Arrays.asList(usuario);
        given(usuarioService.listar()).willReturn(usuarios);
        given(usuarioModelAssembler.toModel(usuario)).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(get("/api/v1/usuarios"));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.usuarioDTOList", hasSize(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/usuarios")));

        then(usuarioService).should().listar();
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void recuperarUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        BDDAssumptions.given(usuario.getId()).isNotNull();
        given(usuarioService.recuperar(usuario.getId())).willReturn(Optional.of(usuario));
        given(usuarioModelAssembler.toModel(usuario)).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(get("/api/v1/usuarios/" + usuario.getId()));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        then(usuarioService).should().recuperar(usuario.getId());
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void recuperarIdPUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        BDDAssumptions.given(usuario.getIdpId()).isNotNull();
        Optional<Usuario> usuarioOptional = Optional.of(usuario);
        given(usuarioService.recuperar(usuario.getIdpId())).willReturn(usuarioOptional);
        given(usuarioModelAssembler.toModel(usuario)).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(get("/api/v1/usuarios/IdP/" + usuario.getIdpId()));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.idpId").value(usuario.getIdpId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        then(usuarioService).should().recuperar(usuario.getIdpId());
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deletarUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        BDDAssumptions.given(usuario.getId()).isNotNull();
        willDoNothing().given(usuarioService).deletar(usuario.getId());

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(delete("/api/v1/usuarios/" + usuario.getId()));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isNoContent());

        then(usuarioService).should().deletar(usuario.getId());
        then(usuarioService).shouldHaveNoMoreInteractions();
    }

    @Test
    public void criarUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        UsuarioDTO usuarioDtoNovo = new UsuarioDTO();
        usuarioDtoNovo.setCpf("33333333333");
        usuarioDtoNovo.setNomeUsuario("nome.usuario");

        Usuario usuarioNovo = new Usuario();
        UsuarioIdentificado usuarioDetalheNovo = new UsuarioIdentificado();
        usuarioDetalheNovo.setCpf(usuarioDtoNovo.getCpf());
        usuarioDetalheNovo.setNomeUsuario(usuarioDtoNovo.getNomeUsuario());
        usuarioNovo.setDetalhe(usuarioDetalheNovo);

        // usuario salvo esperado
        BDDAssumptions.given(usuario.getId()).isNotNull();
        BDDAssumptions.given(usuario.getIdpId()).isNotNull();

        UsuarioIdentificado usuarioDetalheSalvo = new UsuarioIdentificado();
        usuarioDetalheSalvo.setId(usuario.getId());
        usuarioDetalheSalvo.setCpf(usuarioNovo.getDetalhe().getCpf());
        usuarioDetalheSalvo.setNomeUsuario(usuarioNovo.getDetalhe().getNomeUsuario());
        usuario.setDetalhe(usuarioDetalheSalvo);

        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuarioNovo);
        given(usuarioService.salvar(usuarioNovo)).willReturn(usuario);
        given(usuarioModelAssembler.toModel(usuario)).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(post("/api/v1/usuarios")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDtoNovo)));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(header().string("location", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        then(usuarioDTOMapper).should().fromDto(any(UsuarioDTO.class));
        then(usuarioService).should().salvar(usuarioNovo);
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioDTOMapper).shouldHaveNoMoreInteractions();
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void atualizarUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        BDDAssumptions.given(usuarioDto.getId()).isNotNull();
        BDDAssumptions.given(usuarioDto.getIdpId()).isNotNull();
        BDDAssumptions.given(usuario.getId()).isNotNull();
        BDDAssumptions.given(usuario.getIdpId()).isNotNull();

        // dto alteracao
        usuarioDto.setCpf("33333333333");
        usuarioDto.setNomeUsuario("lalalala");

        // usuario alteracao
        UsuarioIdentificado usuarioDetalhe = new UsuarioIdentificado();
        usuarioDetalhe.setId(usuario.getId());
        usuarioDetalhe.setCpf(usuarioDto.getCpf());
        usuarioDetalhe.setNomeUsuario(usuarioDto.getNomeUsuario());
        usuario.setDetalhe(usuarioDetalhe);

        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuario);
        given(usuarioService.atualizar(usuario, usuarioDto.getId())).willReturn(usuario);
        given(usuarioModelAssembler.toModel(usuario)).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(put("/api/v1/usuarios/" + usuarioDto.getId())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(header().string("location", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.primeiroNome").doesNotExist())
                .andExpect(jsonPath("$.ultimoNome").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        then(usuarioDTOMapper).should().fromDto(any(UsuarioDTO.class));
        then(usuarioService).should().atualizar(usuario, usuarioDto.getId());
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioDTOMapper).shouldHaveNoMoreInteractions();
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveRetornarErroAoRecuperarUsuarioInexistente() throws Exception {
        // given - condição prévia ou configuração
        final Long id = usuarioDto.getId();
        BDDAssumptions.given(id).isNotNull();
        given(usuarioService.recuperar(id)).willThrow(new UsuarioNotFoundException(id));

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(get("/api/v1/usuarios/" + id));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        then(usuarioService).should().recuperar(id);
        then(usuarioService).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveRetornarErroAoRecuperarIdPUsuarioInexistente() throws Exception {
        // given - condição prévia ou configuração
        final String id = usuarioDto.getIdpId();
        BDDAssumptions.given(id).isNotNull();
        given(usuarioService.recuperar(id)).willThrow(new UsuarioNotFoundException(id));

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(get("/api/v1/usuarios/IdP/" + id));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (IDP_ID) 123abc")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (IDP_ID) 123abc")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/IdP/123abc")))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        then(usuarioService).should().recuperar(id);
        then(usuarioService).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveRetornarErroAoDeletarUsuarioInexistente() throws Exception {
        // given - condição prévia ou configuração
        final Long id = usuarioDto.getId();
        BDDAssumptions.given(id).isNotNull();
        willThrow(new UsuarioNotFoundException(id)).given(usuarioService).deletar(id);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(delete("/api/v1/usuarios/" + id));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        then(usuarioService).should().deletar(id);
        then(usuarioService).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveRetornarErroAoSalvarUsuarioInvalido() throws Exception {
        // given - condição prévia ou configuração
        usuarioDto = new UsuarioDTO();
        usuarioDto.setNomeUsuario("nome.usuario");
        BDDAssumptions.given(usuarioDto.getCpf()).isNull();

        UsuarioIdentificado usuarioDetalhe = new UsuarioIdentificado();
        usuarioDetalhe.setNomeUsuario(usuarioDto.getNomeUsuario());
        usuarioDetalhe.setCpf(usuarioDto.getCpf());

        usuario = new Usuario();
        usuario.setDetalhe(usuarioDetalhe);

        org.hibernate.PropertyValueException nestedException = new org.hibernate.PropertyValueException(
                "not-null property references a null or transient value", UsuarioIdentificado.class.getName(), "cpf");
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                nestedException.getMessage(), nestedException);

        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuario);
        given(usuarioService.salvar(usuario)).willThrow(exception);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(post("/api/v1/usuarios")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)));

        // then - verificar a saída
        final String titleString = "not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf";
        final String detailString = "not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf";
        response.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is(titleString)))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.detail", is(detailString)))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios")))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
