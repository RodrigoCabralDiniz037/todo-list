package br.edu.unifal.excepition;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ToggleChoreWithInvalidDeadlineException extends RuntimeException{

    public ToggleChoreWithInvalidDeadlineException(String message){
        super(message);
    }
}
