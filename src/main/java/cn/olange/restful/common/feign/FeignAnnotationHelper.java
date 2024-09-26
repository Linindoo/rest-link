package cn.olange.restful.common.feign;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import cn.olange.restful.annotations.OpenFeignControllerAnnotation;
import cn.olange.restful.common.PsiAnnotationHelper;
import cn.olange.restful.method.RequestPath;
import com.intellij.psi.PsiModifierList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujia
 * @since 2024/9/19 9:36
 */
public class
FeignAnnotationHelper {

    public static List<RequestPath> getRequestPaths(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return null;
        }
        PsiAnnotation[] annotations = modifierList.getAnnotations();

        PsiAnnotation requestMappingAnnotation = null;
        List<RequestPath> list = new ArrayList<>();
        for (PsiAnnotation annotation : annotations) {
            for (OpenFeignControllerAnnotation openFeignControllerAnnotation : OpenFeignControllerAnnotation.values()) {
                if (annotation.getQualifiedName().equals(openFeignControllerAnnotation.getQualifiedName())) {
                    requestMappingAnnotation = annotation;
                    break;
                }
            }

        }

        if (requestMappingAnnotation != null) {
            List<RequestPath> requestMappings = getRequestMappings(requestMappingAnnotation, "");
            if (!requestMappings.isEmpty()) {
                list.addAll(requestMappings);
            }
        } else {
            PsiClass superClass = psiClass.getSuperClass();
            if (superClass != null && !superClass.getQualifiedName().equals("java.lang.Object")) {
                list = getRequestPaths(superClass);
            } else {
                return null;
            }

        }

        return list;
    }

    private static List<RequestPath> getRequestMappings(PsiAnnotation annotation, String defaultValue) {
        List<RequestPath> mappingList = new ArrayList<>();
        List<String> pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "path");
        if (pathList.isEmpty()) {
            pathList.add(defaultValue);
        }
        for (String path : pathList) {
            if (StringUtils.isNotEmpty(path)) {
                mappingList.add(new RequestPath(path, null));
            }
        }

        return mappingList;
    }
}
