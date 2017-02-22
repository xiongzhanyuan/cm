package com.xzy.cm.common.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * spring el 执行器
 */
public class SpELHelper {
    /**
     * el 表达式求值
     *
     * @param el  表达式
     * @param methodName 执行方法名
     * @param method 执行方法
     * @param classType 返回类型
     * @param <T> 泛型
     * @return
     */
    public static <T> T elParser(String el, String methodName, Method method, Class<T> classType) {
        T t = null;
        ExpressionParser parser = new SpelExpressionParser();
        if (StringUtils.isBlank(methodName)) {
            Expression exp = parser.parseExpression(el);
            t = exp.getValue(classType);
        } else {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.registerFunction(methodName, method);
            Expression exp = parser.parseExpression(el);
            t = exp.getValue(context, classType);
        }
        return t;
    }
}
