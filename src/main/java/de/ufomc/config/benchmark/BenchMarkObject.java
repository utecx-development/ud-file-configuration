package de.ufomc.config.benchmark;

import de.ufomc.config.core.UfObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BenchMarkObject extends UfObject {

    private List<String> names;

}
