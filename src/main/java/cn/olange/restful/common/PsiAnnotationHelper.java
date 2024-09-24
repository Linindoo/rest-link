package cn.olange.restful.common;

import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PsiAnnotationHelper {

    public static List<String> getAnnotationAttributeValues(PsiAnnotation annotation, String attr) {
        if (annotation == null) {
            return new ArrayList<>();
        }
        PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue(attr);
        if (value == null) {
            return new ArrayList<>();
        }
        List<String> values = new ArrayList<>();
        if (value instanceof PsiReferenceExpression expression) {
            PsiElement psiElement = expression.resolve();
            if (psiElement instanceof PsiVariable psiVariable) {
                PsiExpression initializer = psiVariable.getInitializer();
                values.add(getAnnotationAttributeValue(initializer));
            } else {
                values.add(expression.getText());
            }
        } else if (value instanceof PsiLiteralExpression) {
            values.add(((PsiLiteralExpression) value).getValue().toString());
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();
            for (PsiAnnotationMemberValue initializer : initializers) {
                values.add(initializer.getText().replaceAll("\\\"", ""));
            }
        } else if (value instanceof PsiBinaryExpression expression) {
            values.add(processPsiBinaryExpression(expression));
        } else {
            values.add(value.getText());
        }

        return values;
    }

    private static String getAnnotationAttributeValue(PsiExpression psiExpression) {
        if (psiExpression == null) {
            return "";
        }
        if (psiExpression instanceof PsiReferenceExpression expression) {
            PsiElement psiElement = expression.resolve();
            if (psiElement instanceof PsiVariable psiVariable) {
                PsiExpression initializer = psiVariable.getInitializer();
                return getAnnotationAttributeValue(initializer);
            } else {
                return expression.getText();
            }
        } else if (psiExpression instanceof PsiLiteralExpression expression) {
            return Optional.ofNullable(expression.getValue()).map(Object::toString).orElse("");
        } else if (psiExpression instanceof PsiBinaryExpression expression) {
            return processPsiBinaryExpression(expression);
        } else {
            return psiExpression.getText();
        }
    }

    public static String getAnnotationAttributeValue(PsiAnnotation annotation, String attr) {
        List<String> values = getAnnotationAttributeValues(annotation, attr);
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    private static String processPsiBinaryExpression(PsiBinaryExpression expression) {
        PsiExpression lOperand = expression.getLOperand();
        PsiExpression rOperand = expression.getROperand();
        String leftValue = getAnnotationAttributeValue(lOperand);
        String rightValue = getAnnotationAttributeValue(rOperand);
        return leftValue + rightValue;
    }
}
