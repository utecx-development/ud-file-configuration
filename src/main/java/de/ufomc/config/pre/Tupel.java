package de.ufomc.config.pre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tupel <M, T> {

    private M m;
    private T t;

}
