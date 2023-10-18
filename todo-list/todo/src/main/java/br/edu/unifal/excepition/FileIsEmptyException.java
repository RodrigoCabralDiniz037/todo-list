package br.edu.unifal.excepition;

public class FileIsEmptyException extends RuntimeException{
    public FileIsEmptyException(String message){
        super(message);
    }
}
