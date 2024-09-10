package de.ufomc.config.benchmark;

import de.ufomc.config.core.UDObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor @NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
public class BenchMarkObject extends UDObject {
    List<String> names; //Todo: ??? (what is this even used for?)
    //Todo: If no extension is planned you might wanna make this a record
}
