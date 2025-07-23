package ${packageName};

import ${apiPackageName}.*;
import java.util.List;

public class ${className}
<#list extendsTypes>
    extends <#items as type>${type}<#sep>, </#sep></#items>
</#list>
<#list implementsTypes>
    implements <#items as type>${type}<#sep>, </#sep></#items>
</#list>
{
<#list properties as prop>
    private ${prop.type} ${prop.name};
</#list>

<#list properties as prop>

    public ${prop.type} get${prop.name?cap_first}(){
        return ${prop.name};
    }

    public void set${prop.name?cap_first}(${prop.type} ${prop.name}){
        this.${prop.name} = ${prop.name};
    }

    <#if prop.multiValue>
    public void add${prop.name?cap_first}(${prop.typeName} value){
        if (this.${prop.name} == null) {
            this.${prop.name} = new java.util.ArrayList<>();
        }
        this.${prop.name}.add(value);
    }
    </#if>
</#list>

}
