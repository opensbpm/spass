package ${packageName};

import ${implPackageName}.Default${className};

public interface ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
    static ${className} getInstance() {
        return Default${className}.getInstance();
    }

<#list properties as prop>
    ${prop.type} create${prop.name?cap_first}();
</#list>
}
