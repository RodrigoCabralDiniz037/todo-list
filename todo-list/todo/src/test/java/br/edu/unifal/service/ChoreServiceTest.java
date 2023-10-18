package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.enumerator.ChoreFilter;
import br.edu.unifal.excepition.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {
    private ChoreService service;

    @BeforeEach
    void setup() {
        service = new ChoreService();
    }

    @Test
    @DisplayName("#addChore > When the description is invalid > Throw an Exception")
    void addChoreWhenTheDescriptionIsInvalidThrowAnException() {
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the deadline is invalid > Throw an Exception")
    void addChoreWhenTheDeadlineIsInvalidThrowAnException() {
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the chore already exists > Throw an Exception")
    void addChoreWhenTheChoreAlreadyExistsThrowAnException() {
        service.addChore("Description", LocalDate.now());
        assertThrows(DuplicatedChoreException.class,
                () -> service.addChore("Description", LocalDate.now()));
    }

    @Test
    @DisplayName("#addChore > When the chore's list is empty > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListIsEmptyWhenAddingANewChoreAddTheChore() {
        Chore response = service.addChore("Description", LocalDate.now());
        assertAll(
                () -> assertEquals("Description", response.getDescription()),
                () -> assertEquals(LocalDate.now(), response.getDeadline()),
                () -> assertEquals(Boolean.FALSE, response.getIsCompleted())
        );
    }

    @Test
    @DisplayName("#addChore > When the chore's list has at least one element > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListHasAtLeastOneElementWhenAddingANewChoreAddTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        service.addChore("Chore #02", LocalDate.now().plusDays(2));
        assertAll(
                () -> assertEquals(2, service.getChores().size()),
                () -> assertEquals("Chore #01", service.getChores().get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", service.getChores().get(1).getDescription()),
                () -> assertEquals(LocalDate.now().plusDays(2), service.getChores().get(1).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#deleteChore > When the list is empty > Throw an Exception")
    void deleteChoreWhenTheListIsEmptyThrowAnException() {
        assertThrows(EmptyChoreListException.class, () -> {
            service.deleteChore("Description", LocalDate.now());
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore does not exist > Throw an Exception")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreDoesNotExistThrowAnException() {
        service.addChore("Description", LocalDate.now());
        assertThrows(ChoreNotFoundException.class, () -> {
            service.deleteChore("Chore to be deleted", LocalDate.now().plusDays(2));
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore exists > Delete the chore")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreExistsDeleteTheChore() {
        service.addChore("Description", LocalDate.now().plusDays(5));
        assertEquals(1, service.getChores().size());

        assertDoesNotThrow(() -> service.deleteChore("Description", LocalDate.now().plusDays(5)));
        assertEquals(0, service.getChores().size());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the chore doesn't exist > Throw an Exception")
    void toggleChoreWhenTheChoreDoesNotExistThrowAnException() {
        assertThrows(ChoreNotFoundException.class, () -> service.toggleChore("Chore #01", LocalDate.now()));
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is uncompleted > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsUncompletedToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());
        service.getChores().get(0).setDeadline(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > When toggle the chore twice > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidWhenToggleTheChoreTwiceToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertFalse(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is completed > Throw an Exception")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsCompletedToggleTheChore() {
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(1)));
        assertThrows(ToggleChoreWithInvalidDeadlineException.class,
                () -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsAllWhenTheListIsEmptyReturnAllChores() {
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is not empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsNotEmptyReturnAllChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertAll(
                () -> assertEquals(2, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", response.get(1).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsEmptyReturnAnEmptyList() {
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #02", response.get(0).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsEmptyReturnAnEmptyList() {
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#printChores > When the list is empty > Throw an Exception")
    void printChoresWhenTheListEmptyThrowAnException(){
        assertThrows(EmptyChoreListException.class, () -> service.printChores());
    }

    @Test
    @DisplayName("#printChores > When the list is not empty > When the status is TRUE > Print the chores")
    void printChoresWhenTheListIsNotEmptyWhenTheStatusIsTruePrintChores(){
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(5)));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now().plusDays(9) ));
        service.getChores().add(new Chore("Chore #03", Boolean.TRUE, LocalDate.now() ));

        assertDoesNotThrow(() -> service.printChores());
    }

    @Test
    @DisplayName("#printChores > When the list is not empty > When the status is FALSE > Print the chores")
    void printChoresWhenTheListIsNotEmptyWhenTheStatusIsFalsePrintChores(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now().minusDays(5)));
        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(9) ));
        service.getChores().add(new Chore("Chore #03", Boolean.FALSE, LocalDate.now() ));

        assertDoesNotThrow(() -> service.printChores());
    }

    @Test
    @DisplayName("#editChore > When the list is empty > Throw an Exception")
    void editChoreWhenTheListIsEmptyThrowAnException(){
        assertThrows(EmptyChoreListException.class, () -> service.editChore("Chore to be edited", LocalDate.now(), "New description", LocalDate.now()));
    }

    @Test
    @DisplayName("#editChore > When the chore doesn't exist > Throw an Exception")
    void editChoreWhenTheChoreDoesNotExistThrowAnException(){
        service.getChores().add(new Chore("I exist!", Boolean.FALSE, LocalDate.now()));
        assertThrows(ChoreNotFoundException.class, () -> service.editChore("I don't exist :(", LocalDate.now(), "New description", LocalDate.now()));
    }

    @Test
    @DisplayName("#editChore > When new description is invalid > Throw an Exception")
    void editChoreWhenNewDescriptionIsInvalidThrowAnException(){
        service.getChores().add(new Chore("Chore # 01", Boolean.FALSE, LocalDate.now()));
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(),"", null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), "", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), "", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#editChore > When new deadline is invalid > Throw an Exception")
    void editChoreWhenNewDeadlineIsInvalidThrowAnException(){
        service.getChores().add(new Chore("Description", Boolean.FALSE, LocalDate.now()));
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Description", LocalDate.now(),"Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Description", LocalDate.now(),"Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#editChore > When the new chore is equal to an existing chore > Throw an Exception")
    void editChoreWhenTheNewChoreIsEqualToAnExistingChore(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(2)));
        assertThrows(DuplicatedChoreException.class,
                () -> service.editChore("Chore #02", LocalDate.now().plusDays(2),"Chore #01",  LocalDate.now()));
    }

    @Test
    @DisplayName("#editChore > When trying to edit the status > Nothing happens")
    void editChoreWhenTryingToEditTheStatusNothingHappens(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow( () -> service.editChore("Chore #01", LocalDate.now(), "Chore #01", LocalDate.now()));
        assertFalse(service.getChores().get(0).getIsCompleted());

        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        assertTrue(service.getChores().get(1).getIsCompleted());

        assertDoesNotThrow( () -> service.editChore("Chore #02", LocalDate.now(), "Chore #02", LocalDate.now()));
        assertTrue(service.getChores().get(1).getIsCompleted());
    }

    @Test
    @DisplayName("#editChore > When editing the description > Edit the description")
    void editChoreWhenEditingTheDescriptionEditTheDescription(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        assertDoesNotThrow( () -> service.editChore("Old description", LocalDate.now(), "New description", LocalDate.now()));

        assertAll(
                () -> assertEquals("New description", service.getChores().get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline())
        );
    }

    @Test
    @DisplayName("#editChore > When editing the deadline > Edit the deadline")
    void editChoreWhenEditingTheDeadlineEditTheDeadline(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        assertDoesNotThrow( () -> service.editChore("Old description", LocalDate.now(), "Old description", LocalDate.now().plusDays(5)));

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(5), service.getChores().get(0).getDeadline()),
                () -> assertEquals("Old description", service.getChores().get(0).getDescription())
        );
    }

    @Test
    @DisplayName("#editChore > When editing the deadline and the description > Edit the deadline and description")
    void editChoreWhenEditingTheDeadlineAndTheDescriptionEditTheDeadlineAndDescription(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        assertDoesNotThrow( () -> service.editChore("Old description", LocalDate.now(), "New description", LocalDate.now().plusDays(5)));

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(5), service.getChores().get(0).getDeadline()),
                () -> assertEquals("New description", service.getChores().get(0).getDescription())
        );
    }

    @Test
    @DisplayName("#editChore > When editing more than one chore > Edit the chores")
    void editChoreWhenChoreEditedUpdateChoreInTheList(){
        service.getChores().add(new Chore("Old description #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Old description #02", Boolean.FALSE, LocalDate.now().plusDays(1)));
        service.getChores().add(new Chore("Old description #03", Boolean.FALSE, LocalDate.now().plusDays(2)));

        assertDoesNotThrow( () -> service.editChore("Old description #01", LocalDate.now(), "New description", LocalDate.now().plusDays(6)));
        assertDoesNotThrow( () -> service.editChore("Old description #02", LocalDate.now().plusDays(1), "Old description #02", LocalDate.now().plusDays(5)));
        assertDoesNotThrow( () -> service.editChore("Old description #03", LocalDate.now().plusDays(2), "New description", LocalDate.now().plusDays(2)));

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(6), service.getChores().get(0).getDeadline()),
                () -> assertEquals("New description", service.getChores().get(0).getDescription()),
                () -> assertEquals(3, service.getChores().size()),

                () -> assertEquals(LocalDate.now().plusDays(5), service.getChores().get(1).getDeadline()),
                () -> assertEquals("Old description #02", service.getChores().get(1).getDescription()),
                () -> assertEquals(3, service.getChores().size()),

                () -> assertEquals(LocalDate.now().plusDays(2), service.getChores().get(2).getDeadline()),
                () -> assertEquals("New description", service.getChores().get(2).getDescription()),
                () -> assertEquals(3, service.getChores().size())
        );

    }

    @Test
    @DisplayName("#editChore > When description and deadline are not altered > Chore remains unchanged")
    void editChoreWhenDescriptionAndDeadlineNotAlteredChoreRemainsUnchanged(){
        service.getChores().add(new Chore("Description", Boolean.FALSE, LocalDate.now()));
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals("Description", service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());

        assertDoesNotThrow( () -> service.editChore("Description", LocalDate.now(), "Description", LocalDate.now()));
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());
        assertEquals("Description", service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline());
    }

    @Test
    @DisplayName("#readFile > When file is empty > Throw an Exception")
    void readFileWhenFileIsEmptyThrowAnException(){
        File emptyFile = new File("./src/test/resources/empty.json");
        assertThrows(FileIsEmptyException.class,
                () -> service.readFile(emptyFile));
    }

    @Test
    @DisplayName("#readFile > When read the file > When the deadline is invalid > Throw an Exception")
    void readFileWhenDeadlineIsInvalidThrowAnException(){
        File invalidDeadlineFile = new File("./src/test/resources/invalid_deadline.json");
        assertThrows(InvalidDeadlineException.class,
                () -> service.readFile(invalidDeadlineFile));
    }

    @Test
    @DisplayName("#readFile > When read the file > When the description is invalid > Throw an Exception")
    void readFileWhenDescriptionIsInvalidThrowAnException(){
        File invalidDescriptionFile = new File("./src/test/resources/invalid_description.json");
        assertThrows(InvalidDescriptionException.class,
                () -> service.readFile(invalidDescriptionFile));
    }

    @Test
    @DisplayName("#readFile > When read the file > When duplicated chores > Throw an Exception")
    void readFileWhenDuplicatedChoresThrowAnException(){
        File duplicatedChoresFile = new File("./src/test/resources/duplicated_chores.json");
        assertThrows(DuplicatedChoreException.class,
                () -> service.readFile(duplicatedChoresFile));
    }

    @Test
    @DisplayName("#readFile > When read the file > Read the file and add to chores")
    void readFileAndAddToChores(){
        File file = new File("./src/test/resources/chores.json");
        assertDoesNotThrow(() -> service.readFile(file));
    }

}
