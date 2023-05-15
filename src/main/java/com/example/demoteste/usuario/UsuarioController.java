package com.example.demoteste.usuario;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HATEOAS RESTFul API
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioDTOMapper dtoMapper;

    // @Autowired
    // private KeycloakService keycloakService;

    private final UsuarioService service;
    private final UsuarioModelAssembler assembler;

    public UsuarioController(UsuarioService service, UsuarioModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(value = "/v1/usuarios", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioDTO>> listar() {
        List<Usuario> usuarios = service.listar();
        List<EntityModel<UsuarioDTO>> models = usuarios.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(models, linkTo(methodOn(UsuarioController.class).listar()).withSelfRel());
    }

    @PostMapping(value = "/v1/usuarios", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioDTO>> criar(@RequestBody UsuarioDTO usuarioDto) {
        // primeiro salvar o usuário no IdP (keycloak).
        String identityProviderUserId = UUID.randomUUID().toString(); // keycloakService.criarUsuario(usuarioDto);

        Usuario usuario = dtoMapper.fromDto(usuarioDto);
        usuario.setIdpId(identityProviderUserId);

        Usuario usuarioSalvo = service.salvar(usuario);
        EntityModel<UsuarioDTO> entityModel = assembler.toModel(usuarioSalvo);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping(value = "/v1/usuarios/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDTO> recuperar(@PathVariable Long id) {
        return service.recuperar(id)
                .map(assembler::toModel)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    @GetMapping(value = "/v1/usuarios/IdP/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDTO> recuperarIdP(@PathVariable String id) {
        return service.recuperar(id)
                .map(assembler::toModel)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    @PutMapping(value = "/v1/usuarios/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioDTO>> atualizar(
            @RequestBody UsuarioDTO usuarioDto, @PathVariable Long id) {
        Usuario novoUsuario = dtoMapper.fromDto(usuarioDto);
        Usuario usuarioAtualizado = service.atualizar(novoUsuario, id);
        EntityModel<UsuarioDTO> entityModel = assembler.toModel(usuarioAtualizado);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping(value = "/v1/usuarios/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
