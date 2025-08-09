package ${packageName};

import ${apiPackageName}.*;
import org.semanticweb.owlapi.model.IRI;
import java.util.List;

public class ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
<#else>
    extends AbstractElement
</#list>
<#list implementsTypes>
    implements <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
<#list properties as prop>
    private ${prop.type} ${prop.name};
</#list>

    public ${className}(IRI iri) {
        super(iri);
    }

<#list properties as prop>

    public ${prop.type} get${prop.name?cap_first}(){
        return ${prop.name};
    }

    public void set${prop.name?cap_first}(${prop.type} ${prop.name}){
        this.${prop.name} = ${prop.name};
    <#if prop.hasInverseOf() >
        <#if prop.inverseOf.multiValue>
        ${prop.name}.forEach(v->((Mutable${prop.typeName})v).add${prop.inverseOf.name?cap_first}(this));
        <#else>
        ${prop.name}.set${prop.inverseOf.name?cap_first}(this);
        </#if>
    </#if>
    }

    <#if prop.multiValue>
    public void add${prop.name?cap_first}(${prop.typeName} value){
        if (this.${prop.name} == null) {
            this.${prop.name} = new java.util.ArrayList<>();
        }
        this.${prop.name}.add(value);
    <#if prop.hasInverseOf() >
        <#if prop.inverseOf.multiValue>
        ${prop.name}.forEach(v->((Mutable${prop.typeName})v).add${prop.inverseOf.name?cap_first}(this));
        <#else>
        ${prop.name}.set${prop.inverseOf.name?cap_first}(this);
        </#if>
    </#if>
    }
    </#if>
</#list>

    @Override
    public String toString() {
        return "${className}{" +
            "iri=" + getIri() +
            ", properties=" + propertiesToString() +
            '}';
    }

    private String propertiesToString() {
        StringBuilder sb = new StringBuilder();
        <#list properties as prop>
            sb.append("${prop.name}=").append(${prop.name}).append(", ");
        </#list>
        return sb.toString();
    }

}
