/*
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.wmix.condition;

import org.solmix.commons.expr.Expression;
import org.solmix.commons.expr.ExpressionContext;
import org.solmix.commons.expr.ExpressionFactory;
import org.solmix.commons.expr.ExpressionParseException;
import org.solmix.commons.expr.jexl.JexlExpressionFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.StringUtils;
import org.solmix.commons.util.TransformUtils;
import org.solmix.exchange.Message;
import org.solmix.runtime.Extension;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月12日
 */
@Extension(name="jexl")
public class JexlCondition implements Condition
{
    protected static final ExpressionFactory EXPRESSION_FACTORY = new JexlExpressionFactory();
    private String expressionString;
    private Expression expression;
    
    public JexlCondition(String expr){
        
    }
    
    public String getExpression(){
        return expressionString;
    }
    public void setExpression(String expr) {
        expr = Assert.assertNotNull(StringUtils.trimToNull(expr), "missing condition expression");

        try {
            this.expression = EXPRESSION_FACTORY.createExpression(expr);
        } catch (ExpressionParseException e) {
            throw new IllegalArgumentException("Invalid expression: \"" + expr + "\"", e);
        }

        this.expressionString = expr;
    }
    @Override
    public boolean accept(Message message) {
        Assert.assertNotNull(expression);
        MessageContext ctx = new MessageContext(message);
        Object value = expression.evaluate(ctx);

        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            try {
                return TransformUtils.transformType(Boolean.class, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to evaluating expression for JexlCondition into a boolean value: \"", e);
            }
        }
    }
    
    private static class  MessageContext implements ExpressionContext{

        
        private Message message;
        MessageContext(Message msg){
            this.message=msg;
        }
        
        @Override
        public Object get(String key) {
            return message.get(key);
        }

       
        @Override
        public void put(String key, Object value) {
            message.put(key, value);
        }
        
    }

}
