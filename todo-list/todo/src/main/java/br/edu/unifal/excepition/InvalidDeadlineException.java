package br.edu.unifal.excepition;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDeadlineException extends RuntimeException{

    public InvalidDeadlineException(String message){
        super(message);
    }
}
