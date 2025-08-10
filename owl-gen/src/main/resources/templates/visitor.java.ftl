package ${packageName};

import ${apiPackageName}.PASSProcessModelElement.Mutable;

public interface ${className} {

<#list properties as prop>
    default void visit${prop.type}(${prop.type} element){}
</#list>

}
