package cn.olange.restful.utils;

/**
 * @author zhoujia
 * @since 2024/10/16 11:32
 */
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaSimplePropertyGistKt;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PropertyUtilBase;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class PropertyUtil extends PropertyUtilBase {
    private PropertyUtil() {
    }

    @Nullable
    public static PsiField getFieldOfGetter(PsiMethod method) {
        return getFieldOfGetter(method, true);
    }

    @Nullable
    private static PsiField getFieldOfGetter(PsiMethod method, boolean useIndex) {
        return getFieldOfGetter(method, () -> getGetterReturnExpression(method), useIndex);
    }

    @Nullable
    public static PsiField getFieldOfGetter(PsiMethod method, Supplier<? extends PsiExpression> returnExprSupplier, boolean useIndex) {
        PsiField field = useIndex && method instanceof PsiMethodImpl && method.isPhysical()
            ? JavaSimplePropertyGistKt.getFieldOfGetter(method)
            : getSimplyReturnedField(returnExprSupplier.get());
        if (field == null || !checkFieldLocation(method, field)) return null;
        final PsiType returnType = method.getReturnType();
        return returnType != null && field.getType().equals(returnType) ? field : null;
    }

    @Nullable
    public static PsiField getSimplyReturnedField(@Nullable PsiExpression value) {
        value = PsiUtil.skipParenthesizedExprDown(value);
        if (!(value instanceof PsiReferenceExpression reference)) {
            return null;
        }

        if (hasSubstantialQualifier(reference)) {
            return null;
        }

        final PsiElement referent = reference.resolve();
        if (!(referent instanceof PsiField)) {
            return null;
        }

        return (PsiField)referent;
    }

    private static boolean hasSubstantialQualifier(PsiReferenceExpression reference) {
        final PsiExpression qualifier = PsiUtil.skipParenthesizedExprDown(reference.getQualifierExpression());
        if (qualifier == null) return false;

        if (qualifier instanceof PsiQualifiedExpression) {
            return false;
        }

        if (qualifier instanceof PsiReferenceExpression) {
            return !(((PsiReferenceExpression)qualifier).resolve() instanceof PsiClass);
        }
        return true;
    }


    @Nullable
    public static PsiField getFieldOfSetter(@Nullable PsiMethod method) {
        return getFieldOfSetter(method, true);
    }

    @Nullable
    private static PsiField getFieldOfSetter(@Nullable PsiMethod method, boolean useIndex) {
        if (method == null) {
            return null;
        }
        final PsiParameterList parameterList = method.getParameterList();
        if (parameterList.getParametersCount() != 1) {
            return null;
        }

        PsiField field;
        if (useIndex && method instanceof PsiMethodImpl && method.isPhysical()) {
            field = JavaSimplePropertyGistKt.getFieldOfSetter(method);
        }
        else {
            @NonNls final String name = method.getName();
            if (!name.startsWith(SET_PREFIX)) {
                return null;
            }
            final PsiCodeBlock body = method.getBody();
            if (body == null) {
                return null;
            }
            final PsiStatement[] statements = body.getStatements();
            List<PsiStatement> psiStatements = Arrays.stream(statements).filter(x -> x instanceof PsiExpressionStatement).toList();
            if (psiStatements.size() != 1) {
                return null;
            }
            final PsiStatement statement = psiStatements.get(0);
            if (!(statement instanceof PsiExpressionStatement possibleAssignmentStatement)) {
                return null;
            }
            final PsiExpression possibleAssignment = possibleAssignmentStatement.getExpression();
            if (!(possibleAssignment instanceof PsiAssignmentExpression assignment)) {
                return null;
            }
            if (!JavaTokenType.EQ.equals(assignment.getOperationTokenType())) {
                return null;
            }
            final PsiExpression lhs = PsiUtil.skipParenthesizedExprDown(assignment.getLExpression());
            if (!(lhs instanceof PsiReferenceExpression reference)) {
                return null;
            }
            final PsiExpression qualifier = PsiUtil.skipParenthesizedExprDown(reference.getQualifierExpression());
            if (qualifier instanceof PsiReferenceExpression referenceExpression) {
                final PsiElement target = referenceExpression.resolve();
                if (!(target instanceof PsiClass)) {
                    return null;
                }
            }
            else if (qualifier != null && !(qualifier instanceof PsiThisExpression) && !(qualifier instanceof PsiSuperExpression)) {
                return null;
            }
            final PsiElement referent = reference.resolve();
            if (!(referent instanceof PsiField)) {
                return null;
            }
            field = (PsiField)referent;

            final PsiExpression rhs = PsiUtil.skipParenthesizedExprDown(assignment.getRExpression());
            if (!(rhs instanceof PsiReferenceExpression rReference)) {
                return null;
            }
            final PsiExpression rQualifier = rReference.getQualifierExpression();
            if (rQualifier != null) {
                return null;
            }
            final PsiElement rReferent = rReference.resolve();
            if (rReferent == null) {
                return null;
            }
            if (!(rReferent instanceof PsiParameter) && !(rReferent instanceof PsiField)) {
                return null;
            }
        }
        return field != null && field.getType().equals(parameterList.getParameters()[0].getType()) && checkFieldLocation(method, field)
            ? field
            : null;
    }


    private static boolean checkFieldLocation(PsiMethod method, PsiField field) {
        return PsiResolveHelper.getInstance(method.getProject()).isAccessible(field, method, null) &&
            (!method.hasModifier(JvmModifier.STATIC) || field.hasModifier(JvmModifier.STATIC)) &&
            InheritanceUtil.isInheritorOrSelf(method.getContainingClass(), field.getContainingClass(), true);
    }

}
