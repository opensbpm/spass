package ${packageName};

import org.semanticweb.owlapi.model.IRI;
import java.util.Collection;

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
    <#if prop.multiValue>
    Collection<${prop.type}> get${prop.name?cap_first}();
    <#else>
    ${prop.type} get${prop.name?cap_first}();
    </#if>
</#list>

    void accept(Visitor visitor);

    public interface Mutable extends ${className}
<#list extendsTypes>
    , <#items as type>${type}.Mutable<#sep>, </#sep></#items>
</#list>
    {
    <#list properties as prop>
        <#if prop.multiValue>
        void set${prop.name?cap_first}(Collection<${prop.type}> value);
        <#else>
        void set${prop.name?cap_first}(${prop.type} value);
        </#if>

        <#if prop.multiValue>
        void add${prop.name?cap_first}(${prop.typeName} value);
        </#if>
    </#list>
    }
}
