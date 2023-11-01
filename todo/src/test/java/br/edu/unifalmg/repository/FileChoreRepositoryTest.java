package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.exception.EmptyChoreListException;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static javax.management.Query.eq;
import static jdk.dynalink.linker.support.Guards.isNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#load > When the file is found > When the content is empty > Return empty list")
    void loadWhenTheFileIsFoundWhenTheContentIsEmptyReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is not found (or path is invalid) > Return an empty list")
    void loadWhenTheFileIsNotFoundOrPathIsInvalidReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is loaded > Return a chores' list")
    void loadWhenTheFileIsLoadedReturnAChoresList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();
        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );
    }

    @Test
    @DisplayName("#save > When the file is not found or the path is invalid > Throw an IOException")
    void saveWhenFileNotFoundOrPathInvalidThrowsIOException() throws IOException {
        List<Chore> chores = new ArrayList<>(List.of(new Chore("Chore 1", false, LocalDate.now())));
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("chores.json"), chores);
        Assertions.assertFalse(
            repository.save(chores)
        );
    }

    @Test
    @DisplayName("#save > When the chore list is null > Throw EmptyChoreListException")
    void saveWhenTheChoreListIsNullThrowException() throws IOException {
        // Arrange
        Mockito.doThrow(EmptyChoreListException.class).when(mapper).writeValue(Mockito.any(File.class), Mockito.isNull());

        // Act & Assert
        Assertions.assertThrows(EmptyChoreListException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    @DisplayName("#save > When the chore list is empty > Return true")
    void saveWhenTheChoreListIsEmptyReturnTrue() throws IOException {
        List<Chore> emptyChoreList = new ArrayList<>();
        Mockito.doNothing().when(mapper).writeValue(new File("chores.json"), emptyChoreList);
        boolean result = repository.save(emptyChoreList);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("#save > When the chores is null > Throw a Exceprion")
    void saveWhenTheChoreIsNullThrowException() throws IOException {
        Mockito.doThrow(EmptyChoreListException.class).when(mapper).writeValue(new File("chores.json"), null);
        Assertions.assertThrows(EmptyChoreListException.class,()->repository.save(null));
    }

    @Test
    @DisplayName("#save > When the chore list is not empty and the file is found > Return true")
    void saveWhenChoreListIsNotEmptyAndFileIsFoundReturnTrue() throws IOException {
        List<Chore> chores = new ArrayList<>(Arrays.asList(new Chore("Chore 1", false, LocalDate.now())));
        Mockito.doNothing().when(mapper).writeValue(new File("chores.json"), chores);
        boolean result = repository.save(chores);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("#save > When the chore list is not empty > Return true")
    void saveWhenChoreListIsNotEmptyReturnTrue() throws IOException {
        List<Chore> nonEmptyChoreList = new ArrayList<>(Arrays.asList(new Chore("Chore 1", false, LocalDate.now())));
        Mockito.doNothing().when(mapper).writeValue(new File("chores.json"), nonEmptyChoreList);
        boolean result = repository.save(nonEmptyChoreList);
        Assertions.assertTrue(result);
    }
}
