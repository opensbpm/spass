package ${packageName};

import ${apiPackageName}.Visitor;
import org.semanticweb.owlapi.model.IRI;
import java.util.List;

public abstract class ${className}{
    private final IRI iri;

    protected ${className}(IRI iri) {
        this.iri = iri;
    }

    public final IRI getIri() {
        return iri;
    }

    public void accept(Visitor visitor){}

}
