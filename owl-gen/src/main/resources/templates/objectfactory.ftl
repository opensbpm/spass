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
    static ${className} getInstance() {
        return Default${className}.getInstance();
    }

<#list properties as prop>
    ${prop.type}.Mutable create${prop.name?cap_first}(IRI iri);
</#list>
}
