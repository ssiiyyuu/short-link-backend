package com.siyu.server.annotation;

import com.siyu.server.enums.VisitorOperationType;

import javax.management.relation.RelationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitorOperationLogger {

    //操作类型
    VisitorOperationType operationType();

    // 操作对象
    String operationTarget();
}
