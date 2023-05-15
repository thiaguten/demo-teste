package com.example.demoteste.usuario;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID criado para o usuario no provedor de identidade (keycloak)
    // @NaturalId
    @Column(nullable = false, unique = true, updatable = false)
    private String idpId;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UsuarioIdentificado detalhe;

    // @formatter:off
    /*
     * @OneToMany mapping with PRIMARY KEY bidirectional association
     * is the best way to map a one-to-many database relationship when we really
     * need the collection on the parent side of the association.
     * 
     * The down side of this bidirectional approach using collection is that we can
     * only use them when the number of child records is rather limited.
     * In reality, @OneToMany is practical only when many means few.
     * Maybe @OneToFew would have been a more suggestive name for this annotation.
     * The problem is that you cannot limit the size of a @OneToMany collection
     * like it would be the case if you used query-level pagination.
     * 
     * The cascading allow us to delete the all associated Children entities when
     * the Parent entity is deleted.
     * You should only cascade from Parent entities to Children and not the other
     * way around.
     * You should always use only the casacde operations that are demanded by your
     * business logic requirements, and not turn the CascadeType.ALL into a default
     * Parent – Child association entity state propagation configuration.
     * 
     * The orphan-removal allows us to delete the Child entity whenever it's no
     * longer referenced by its Parent.
     * 
     * Avoid the MultipleBagFetchException by using a Set instead of a List.
     */
    // @JsonIgnore
    // @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Ocorrencia> ocorrencias = new HashSet<>();
    // @formatter:on

    public Usuario() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdpId() {
        return idpId;
    }

    public void setIdpId(String idpId) {
        this.idpId = idpId;
    }

    public UsuarioIdentificado getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(UsuarioIdentificado detalhe) {
        if (detalhe == null) {
            if (this.detalhe != null) {
                this.detalhe.setUsuario(null);
            }
        } else {
            detalhe.setUsuario(this);
        }
        this.detalhe = detalhe;
    }

    // @formatter:off
    // Métodos utilitários (add..., remove...) usados para sincronizar os dois lados
    // da associação bidirecional entre Usuario e Ocorrencia
    // =============================================================================

    // public Set<Ocorrencia> getOcorrencias() {
    //     return ocorrencias;
    // }

    // public void addOcorrencia(Ocorrencia ocorrencia) {
    //     ocorrencias.add(ocorrencia);
    //     ocorrencia.setPessoa(this);
    // }

    // public void removeOcorrencia(Ocorrencia ocorrencia) {
    //     ocorrencias.remove(ocorrencia);
    //     ocorrencia.setPessoa(null);
    // }
    // @formatter:on
}
