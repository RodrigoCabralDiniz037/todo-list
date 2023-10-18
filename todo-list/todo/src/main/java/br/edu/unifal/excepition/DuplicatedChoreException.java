package br.edu.unifal.excepition;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicatedChoreException extends RuntimeException{
    public DuplicatedChoreException(String message){
        super(message);
    }
}
