package ${packageName};

import ${apiPackageName}.*;

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
    public ${prop.type} create${prop.name?cap_first}(){
        return new Mutable${prop.type}();
    }
</#list>
}
