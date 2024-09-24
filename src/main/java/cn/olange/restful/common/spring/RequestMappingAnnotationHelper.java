package cn.olange.restful.common.spring;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import cn.olange.restful.annotations.OpenFeignControllerAnnotation;
import cn.olange.restful.annotations.SpringRequestMethodAnnotation;
import cn.olange.restful.common.PsiAnnotationHelper;
import cn.olange.restful.method.RequestPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestMappingAnnotationHelper {


    /**
     * 过滤所有注解
     * @param psiClass
     * @return
     */
    public static List<RequestPath> getRequestPaths(PsiClass psiClass) {
        if (psiClass.getModifierList() == null) {
            return null;
        }
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();

        PsiAnnotation requestMappingAnnotation = null;
        List<RequestPath> list = new ArrayList<>();
        for (PsiAnnotation annotation : annotations) {
            for (SpringRequestMethodAnnotation mappingAnnotation : SpringRequestMethodAnnotation.values()) {
                if (annotation.getQualifiedName().equals(mappingAnnotation.getQualifiedName())) {
                    requestMappingAnnotation = annotation;
                    break;
                }
            }

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
                list.add(new RequestPath("/", null));
            }

        }

        return list;
    }


    /**
     * @param annotation
     * @param defaultValue
     * @return
     */
    private static List<RequestPath> getRequestMappings(PsiAnnotation annotation, String defaultValue) {
        List<RequestPath> mappingList = new ArrayList<>();

        SpringRequestMethodAnnotation requestAnnotation = SpringRequestMethodAnnotation.getByQualifiedName(annotation.getQualifiedName());

        if (requestAnnotation==null) {
            return new ArrayList<>();
        }

        List<String> methodList ;
        if (requestAnnotation.methodName() != null) {
            methodList = Arrays.asList(requestAnnotation.methodName()) ;
        } else { // RequestMapping 如果没有指定具体method，不写的话，默认支持所有HTTP请求方法
            methodList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "method");
        }

        List<String> pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "value");
        if (pathList.size() == 0) {
            pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "path");
        }

        // 没有设置 value，默认方法名
        if (pathList.size() == 0) {
            pathList.add(defaultValue);
        }

        if (methodList.size() > 0) {
            for (String method : methodList) {
                for (String path : pathList) {
                    mappingList.add(new RequestPath(path, method));
                }
            }
        } else {
            for (String path : pathList) {
                mappingList.add(new RequestPath(path, null));
            }
        }

        return mappingList;
    }

    /**
     * 过滤所有注解
     * @param psiMethod
     * @return
     */
    public static List<RequestPath> getRequestPaths(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        List<RequestPath> list = new ArrayList<>();

        for (PsiAnnotation annotation : annotations) {
            for (SpringRequestMethodAnnotation mappingAnnotation : SpringRequestMethodAnnotation.values()) {
                if (mappingAnnotation.getQualifiedName().equals(annotation.getQualifiedName())) {

                    String defaultValue = "/";
                    List<RequestPath> requestMappings = getRequestMappings(annotation, defaultValue);
                    if (!requestMappings.isEmpty()) {
                        list.addAll(requestMappings);
                    }
                }
            }
        }

        return list;
    }


    private static String getRequestMappingValue(PsiAnnotation annotation) {
        String value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");
        if (org.apache.commons.lang3.StringUtils.isEmpty(value))
            value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation,"path");
        return value;
    }

    public static String[] getRequestMappingValues(PsiAnnotation annotation) {
        String[] values ;
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue("value");

        if (attributeValue instanceof PsiLiteralExpression) {

            return  new String[]{((PsiLiteralExpression) attributeValue).getValue().toString()};
        }
        if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            values = new String[initializers.length];

            for (PsiAnnotationMemberValue initializer : initializers) {

            }

            for (int i = 0; i < initializers.length; i++) {
                values[i] = ((PsiLiteralExpression)(initializers[i])).getValue().toString();
            }
        }

        return new String[]{};
    }


    public static String getOneRequestMappingPath(PsiClass psiClass) {
        if (psiClass == null) {
            return "";
        }
        PsiAnnotation annotation = psiClass.getModifierList().findAnnotation(SpringRequestMethodAnnotation.REQUEST_MAPPING.getQualifiedName());

        String path = null;
        if (annotation != null) {
            path = RequestMappingAnnotationHelper.getRequestMappingValue(annotation);
        }

        return path != null ? path : "";
    }


    public static String getOneRequestMappingPath(PsiMethod psiMethod) {
        SpringRequestMethodAnnotation requestAnnotation = null;

        List<SpringRequestMethodAnnotation> springRequestAnnotations = Arrays.stream(SpringRequestMethodAnnotation.values()).filter(annotation ->
                psiMethod.getModifierList().findAnnotation(annotation.getQualifiedName()) != null
        ).toList();


        if (!springRequestAnnotations.isEmpty()) {
            requestAnnotation = springRequestAnnotations.get(0);
        }

        String mappingPath;
        if(requestAnnotation != null){
            PsiAnnotation annotation = psiMethod.getModifierList().findAnnotation(requestAnnotation.getQualifiedName());
            mappingPath = RequestMappingAnnotationHelper.getRequestMappingValue(annotation);
        }else {
            String methodName = psiMethod.getName();
            mappingPath = org.apache.commons.lang3.StringUtils.uncapitalize(methodName);
        }

        return mappingPath;
    }



}
