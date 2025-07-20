package ${packageName};

public interface ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
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
    </#list>
    }
}
