package br.edu.unifal.excepition;

import lombok.NoArgsConstructor;

/**
 * Exceção pode ou não ser lançada
 */

@NoArgsConstructor // pra caso não queira uma mensagem
public class InvalidDescriptionException extends RuntimeException{

    public InvalidDescriptionException(String message){
        super(message);
    }
}
