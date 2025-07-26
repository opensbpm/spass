package ${packageName};

import ${apiPackageName}.*;
import org.semanticweb.owlapi.model.IRI;

public class ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
<#list implementsTypes>
    implements <#items as type>${type}<#sep>, </#sep></#items>
</#list>

{

    private final static DefaultObjectFactory INSTANCE = new DefaultObjectFactory();

    public static DefaultObjectFactory getInstance() {
        return INSTANCE;
    }

    private DefaultObjectFactory() {
        // no instance creation
    }

<#list properties as prop>
    public ${prop.type}.Mutable create${prop.name?cap_first}(IRI iri) {
        return new Mutable${prop.type}(iri);
    }
</#list>
}
