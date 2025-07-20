package ${packageName};

import ${apiPackageName}.*;

public class ${className}
<#list implementsTypes>
    implements <#items as type>${type}.Mutable<#sep>, </#sep></#items>
</#list>
{
<#list properties as prop>
    private ${prop.type} ${prop.name};
</#list>

<#list properties as prop>

    public ${prop.type} get${prop.name?cap_first}(){
        return ${prop.name};
    }

    public void set${prop.name?cap_first}(${prop.type} value){
        this.${prop.name} = ${prop.name};
    }
</#list>

}
