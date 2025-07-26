package org.opensbpm.spass.reader;


import org.opensbpm.spass.reader.model.api.PASSProcessModelElement;

import java.util.Collection;

public class ModelUtils {


    public static <T extends PASSProcessModelElement> Collection<T> getContains(PASSProcessModelElement modelElement, Class<T> passClass) {
        return modelElement.getContains().stream()
                .filter(passClass::isInstance)
                .map(passClass::cast)
                .toList();
    }
}
