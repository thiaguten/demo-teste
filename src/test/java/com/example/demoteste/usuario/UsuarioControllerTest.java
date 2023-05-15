package com.example.demoteste.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        List<Usuario> usuarios = Arrays.asList(usuario);

        given(usuarioService.listar()).willReturn(usuarios);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        this.mvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.usuarioDTOList", hasSize(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/usuarios")));

        verify(usuarioService).listar();
        verify(usuarioModelAssembler).toModel(usuario);
    }

    @Test
    public void recuperarUsuarioTest() throws Exception {
        assertThat(usuario.getId()).isNotNull();
        Optional<Usuario> usuarioOptional = Optional.of(usuario);

        given(usuarioService.recuperar(anyLong())).willReturn(usuarioOptional);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        this.mvc.perform(get("/api/v1/usuarios/" + usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        verify(usuarioService).recuperar(usuario.getId());
        verify(usuarioModelAssembler).toModel(usuario);
    }

    @Test
    public void recuperarIdPUsuarioTest() throws Exception {
        assertThat(usuario.getIdpId()).isNotNull();
        Optional<Usuario> usuarioOptional = Optional.of(usuario);

        given(usuarioService.recuperar(anyString())).willReturn(usuarioOptional);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        this.mvc.perform(get("/api/v1/usuarios/IdP/" + usuario.getIdpId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.idpId").value(usuario.getIdpId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")));

        verify(usuarioService).recuperar(usuario.getIdpId());
        verify(usuarioModelAssembler).toModel(usuario);
    }

    @Test
    public void deletarUsuarioTest() throws Exception {
        assertThat(usuario.getId()).isNotNull();
        doNothing().when(usuarioService).deletar(anyLong());

        this.mvc.perform(delete("/api/v1/usuarios/" + usuario.getId()))
                .andExpect(status().isNoContent());

        verify(usuarioService).deletar(usuario.getId());
    }

    @Test
    public void criarUsuarioTest() throws Exception {
        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(new Usuario());
        given(usuarioService.salvar(any(Usuario.class))).willReturn(usuario);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        this.mvc.perform(post("/api/v1/usuarios/")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(header().string("location", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")))
                .andDo(print());

        verify(usuarioDTOMapper).fromDto(any(UsuarioDTO.class));
        verify(usuarioService).salvar(any(Usuario.class));
        verify(usuarioModelAssembler).toModel(usuario);
    }

    @Test
    public void atualizarUsuarioTest() throws Exception {
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

        // Usuario novoUsuario = dtoMapper.fromDto(usuarioDto);
        // Usuario usuarioAtualizado = service.atualizar(novoUsuario, id);
        // EntityModel<UsuarioDTO> entityModel = assembler.toModel(usuarioAtualizado);

        given(usuarioDTOMapper.fromDto(any(UsuarioDTO.class))).willReturn(usuario);
        given(usuarioService.atualizar(any(Usuario.class), anyLong())).willReturn(usuario);
        given(usuarioModelAssembler.toModel(any(Usuario.class))).willReturn(entityModel);

        this.mvc.perform(put("/api/v1/usuarios/" + id)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(header().string("location", is("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.primeiroNome").doesNotExist())
                .andExpect(jsonPath("$.ultimoNome").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", is("/api/v1/usuarios/1")))
                .andDo(print());

        verify(usuarioDTOMapper).fromDto(any(UsuarioDTO.class));
        verify(usuarioService).atualizar(any(Usuario.class), anyLong());
        verify(usuarioModelAssembler).toModel(usuario);
    }

    @Test
    public void deveRetornarErroAoRecuperarUsuarioInexistente() throws Exception {
        Long id = 1L;
        given(usuarioService.recuperar(anyLong())).willThrow(new UsuarioNotFoundException(id));

        this.mvc.perform(get("/api/v1/usuarios/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/1")));

        verify(usuarioService).recuperar(id);
    }

    @Test
    public void deveRetornarErroAoRecuperarIdPUsuarioInexistente() throws Exception {
        String id = "123abc";
        given(usuarioService.recuperar(anyString())).willThrow(new UsuarioNotFoundException(id));

        this.mvc.perform(get("/api/v1/usuarios/IdP/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (IDP_ID) 123abc")))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (IDP_ID) 123abc")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/IdP/123abc")));

        verify(usuarioService).recuperar(id);
    }

    @Test
    public void deveRetornarErroAoDeletarUsuarioInexistente() throws Exception {
        Long id = 1L;
        doThrow(new UsuarioNotFoundException(id)).when(usuarioService).deletar(anyLong());

        this.mvc.perform(delete("/api/v1/usuarios/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail",
                        is("UsuarioNotFoundException: Não foi possível encontrar o usuário: (ID) 1")))
                .andExpect(jsonPath("$.instance", is("/api/v1/usuarios/1")));

        verify(usuarioService).deletar(id);
    }
}
