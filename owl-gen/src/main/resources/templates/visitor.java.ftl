package ${packageName};

public interface ${className} {

<#list properties as prop>
    default void visit${prop.type}(${prop.type} element){}

</#list>

}
