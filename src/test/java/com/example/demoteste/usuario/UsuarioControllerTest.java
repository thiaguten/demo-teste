package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
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
        Long id = 1L;

        usuario = new Usuario();
        usuario.setId(id);
        usuario.setIdpId("123abc");

        usuarioDto = new UsuarioDTO();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setIdpId(usuario.getIdpId());

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
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

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
        assertThat(usuario.getId()).isNotNull();
        given(usuarioService.recuperar(anyLong())).willReturn(Optional.of(usuario));
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

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
        assertThat(usuario.getIdpId()).isNotNull();
        Optional<Usuario> usuarioOptional = Optional.of(usuario);
        given(usuarioService.recuperar(anyString())).willReturn(usuarioOptional);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

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
        assertThat(usuario.getId()).isNotNull();
        doNothing().when(usuarioService).deletar(anyLong());

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
        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(new Usuario());
        given(usuarioService.salvar(any(Usuario.class))).willReturn(usuario);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(post("/api/v1/usuarios")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)));

        // then - verificar a saída
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(header().string("location", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        then(usuarioDTOMapper).should().fromDto(any(UsuarioDTO.class));
        then(usuarioService).should().salvar(any(Usuario.class));
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioDTOMapper).shouldHaveNoMoreInteractions();
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void atualizarUsuarioTest() throws Exception {
        // given - condição prévia ou configuração
        final Long id = 1L;
        final String idIdP = "123abc"; // natural_id
        final String cpf = "33333333333"; // natural_id
        final String nomeUsuario = "lalalala"; // natural_id

        usuarioDto.setId(id);
        usuarioDto.setIdpId(idIdP);
        usuarioDto.setCpf(cpf);
        usuarioDto.setNomeUsuario(nomeUsuario);
        usuarioDto.setPrimeiroNome(null);
        usuarioDto.setUltimoNome(null);
        usuarioDto.setEmail(null);

        usuario.setId(usuarioDto.getId());
        usuario.setIdpId(usuarioDto.getIdpId());
        UsuarioIdentificado usuarioDetalhe = new UsuarioIdentificado();
        usuarioDetalhe.setId(usuario.getId());
        usuarioDetalhe.setCpf(usuarioDto.getCpf());
        usuarioDetalhe.setNomeUsuario(usuarioDto.getNomeUsuario());
        usuarioDetalhe.setPrimeiroNome(usuarioDto.getPrimeiroNome());
        usuarioDetalhe.setUltimoNome(usuarioDto.getUltimoNome());
        usuarioDetalhe.setEmail(usuarioDto.getEmail());
        usuario.setDetalhe(usuarioDetalhe);

        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuario);
        given(usuarioService.atualizar(any(Usuario.class), anyLong())).willReturn(usuario);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(put("/api/v1/usuarios/" + id)
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
        then(usuarioService).should().atualizar(any(Usuario.class), anyLong());
        then(usuarioModelAssembler).should().toModel(usuario);
        then(usuarioDTOMapper).shouldHaveNoMoreInteractions();
        then(usuarioService).shouldHaveNoMoreInteractions();
        then(usuarioModelAssembler).shouldHaveNoMoreInteractions();
    }

    @Test
    public void deveRetornarErroAoRecuperarUsuarioInexistente() throws Exception {
        // given - condição prévia ou configuração
        Long id = 1L;
        given(usuarioService.recuperar(anyLong())).willThrow(new UsuarioNotFoundException(id));

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
        String id = "123abc";
        given(usuarioService.recuperar(anyString())).willThrow(new UsuarioNotFoundException(id));

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
        Long id = 1L;
        willThrow(new UsuarioNotFoundException(id)).given(usuarioService).deletar(anyLong());

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
        usuarioDto.setCpf(null);
        usuarioDto.setNomeUsuario("nome.usuario");

        UsuarioIdentificado usuarioDetalhe = new UsuarioIdentificado();
        usuarioDetalhe.setNomeUsuario(usuarioDto.getNomeUsuario());
        usuarioDetalhe.setCpf(usuarioDto.getCpf());

        usuario = new Usuario();
        usuario.setDetalhe(usuarioDetalhe);

        org.hibernate.PropertyValueException nestedException = new org.hibernate.PropertyValueException(
                "not-null property references a null or transient value", UsuarioIdentificado.class.getName(), "cpf");
        DataIntegrityViolationException exception = new DataIntegrityViolationException(nestedException.getMessage(),
                nestedException);
        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuario);
        given(usuarioService.salvar(any(Usuario.class))).willThrow(exception);

        // when - ação ou o comportamento que estamos testando
        ResultActions response = this.mvc.perform(post("/api/v1/usuarios")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)));

        // then - verificar a saída
        assertThat(usuarioDto.getCpf()).isNull();
        assertThat(usuarioDto.getNomeUsuario()).isNotNull();
        assertThat(usuario.getDetalhe().getCpf()).isNull();
        assertThat(usuario.getDetalhe().getNomeUsuario()).isNotNull();

        String titleString = "not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf";
        String detailString = "not-null property references a null or transient value : com.example.demoteste.usuario.UsuarioIdentificado.cpf";
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
