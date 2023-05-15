package com.example.demoteste.suporte;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
public interface DTOMapper<DTO, ENTITY> {

    DTO toDto(ENTITY entity);

    ENTITY fromDto(DTO dto);

}
