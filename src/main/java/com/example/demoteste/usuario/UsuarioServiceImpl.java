package com.example.demoteste.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@Service("usuarioService")
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public Optional<Usuario> recuperar(Long id) {
        // return repository.findById(id);
        return repository.findByIdJoinFetch(id);
    }

    @Override
    public Optional<Usuario> recuperar(String idpId) {
        return repository.findByIdpIdJoinFetch(idpId);
    }

    @Override
    public Usuario atualizar(Usuario novoUsuario, Long id) {
        return recuperar(id)
                .map(usuario -> {
                    usuario.setDetalhe(novoUsuario.getDetalhe());
                    return salvar(usuario);
                })
                .orElseGet(() -> {
                    novoUsuario.setId(id);
                    return salvar(novoUsuario);
                });
    }

    @Override
    public void deletar(Long id) {
        // repository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));
        try {
            // TODO - Dúvida
            //
            // Deletar somente as informações na tabela usuario_identificado por questoes de
            // LGPD, mas não deletar na tabela usuario por causa de referencias com a
            // ocorrencia.
            //
            // Fazer query na mão ou criar um Repository dedicado a entidade
            // UsuarioIdentificado e remover mapeamento @OneToOne e controlar o
            // relacionamento na mão?
            //
            // Ou remover essa funcionalidade da API e deixar que essa limpeza de
            // informações identificaveis da LGPD seja feita diretamente na base de dados
            // por um DBA?
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UsuarioNotFoundException(id, e);
        }
    }

    @Override
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Override
    public Usuario obterReferencia(Long id) {
        return repository.getReferenceById(id);
    }

}
