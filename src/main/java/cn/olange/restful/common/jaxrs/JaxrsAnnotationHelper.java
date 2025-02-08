package cn.olange.restful.common.jaxrs;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import cn.olange.restful.annotations.JaxrsHttpMethodAnnotation;
import cn.olange.restful.annotations.JaxrsPathAnnotation;
import cn.olange.restful.common.PsiAnnotationHelper;
import cn.olange.restful.method.RequestPath;
import com.intellij.psi.PsiModifierList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JaxrsAnnotationHelper {

    private static String getWsPathValue(PsiAnnotation annotation) {
        String value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");
        return value != null ? value : "";
    }

    /**
     * 过滤所有注解
     * @param psiMethod
     * @return
     */
    public static List<RequestPath> getRequestPaths(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();
        List<RequestPath> list = new ArrayList<>();

        PsiAnnotation wsPathAnnotation = psiMethod.getModifierList().findAnnotation(JaxrsPathAnnotation.PATH.getQualifiedName());
        String path = wsPathAnnotation == null ? psiMethod.getName() : getWsPathValue(wsPathAnnotation);

        JaxrsHttpMethodAnnotation[] jaxrsHttpMethodAnnotations = JaxrsHttpMethodAnnotation.values();

        Arrays.stream(annotations).forEach(a -> Arrays.stream(jaxrsHttpMethodAnnotations).forEach(methodAnnotation -> {
            if (StringUtils.isNotEmpty(a.getQualifiedName()) && a.getQualifiedName().equalsIgnoreCase(methodAnnotation.getQualifiedName())) {
                list.add(new RequestPath(path, methodAnnotation.getShortName()));
            }
        }));

        return list;
    }


    public static String getClassUriPath(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return "";
        }
        PsiAnnotation annotation = modifierList.findAnnotation(JaxrsPathAnnotation.PATH.getQualifiedName());

        String path = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");
        return path != null ? path : "";
    }



}