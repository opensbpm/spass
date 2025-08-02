package ${packageName};

import org.semanticweb.owlapi.model.IRI;
import java.util.List;

<#if comment?has_content>
/**
 * ${comment}
 */
</#if>
public interface ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
    IRI getIri();

<#list properties as prop>
    ${prop.type} get${prop.name?cap_first}();
</#list>

    public interface Mutable extends ${className}
<#list extendsTypes>
    , <#items as type>${type}.Mutable<#sep>, </#sep></#items>
</#list>
    {
    <#list properties as prop>

        void set${prop.name?cap_first}(${prop.type} value);

        <#if prop.multiValue>
        void add${prop.name?cap_first}(${prop.typeName} value);
        </#if>
    </#list>
    }
}
