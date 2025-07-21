package ${packageName};

import ${apiPackageName}.PASSProcessModelElement.Mutable;
import ${implPackageName}.Default${className};
import org.semanticweb.owlapi.model.IRI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
    public static PASSProcessModelElement.Mutable getModelElement(IRI iri) {
        Map<IRI, ModelInstantiator> classInstantiators = new HashMap<>();
    <#list properties as prop>
        classInstantiators.put(IRI.create("${prop.iri}"), ObjectFactory::create${prop.name?cap_first});
    </#list>

        if (!classInstantiators.containsKey(iri)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have a corresponding PassModelElement", iri));
        }
        return classInstantiators.get(iri).apply(ObjectFactory.getInstance());
    }

    interface ModelInstantiator extends Function<ObjectFactory, Mutable> {
    }


    static ${className} getInstance() {
        return Default${className}.getInstance();
    }

<#list properties as prop>
    ${prop.type}.Mutable create${prop.name?cap_first}();
</#list>
}
