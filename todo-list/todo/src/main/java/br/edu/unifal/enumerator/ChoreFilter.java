package br.edu.unifal.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ChoreFilter {

    ALL(1L, "All Chores"),
    COMPLETED(2L, "Only completed chores"),
    UNCOMPLETED(3L, "Only uncompleted Chores");

    private Long identifier;
    private String description;
}
