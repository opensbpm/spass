package ${packageName};

public interface ${className}
<#list extendsClasses>
    extends <#items as superClass>${superClass}<#sep>, </#sep></#items>
</#list>
{
<#list properties as prop>
    ${prop.type} get${prop.name?cap_first}();
</#list>

    public interface Mutable
<#list extendsClasses>
    extends <#items as superClass>${superClass}.Mutable<#sep>, </#sep></#items>
</#list>
    {
    <#list properties as prop>
        void set${prop.name?cap_first}(${prop.type} value);
    </#list>
    }
}
