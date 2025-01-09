package cn.olange.restful.common.resolver;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.annotations.PathMappingAnnotation;
import cn.olange.restful.annotations.SpringControllerAnnotation;
import cn.olange.restful.common.spring.RequestMappingAnnotationHelper;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;
import org.apache.commons.collections.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpringResolver  extends BaseServiceResolver  {

    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {
        List<RestServiceItem> itemList = new ArrayList<>();

        SpringControllerAnnotation[] supportedAnnotations = SpringControllerAnnotation.values();
        for (PathMappingAnnotation controllerAnnotation : supportedAnnotations) {

            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getShortName(), project, globalSearchScope);
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
                PsiClass psiClass = (PsiClass) psiElement;
                List<RestServiceItem> serviceItemList = getServiceItemList(psiClass);
                if (serviceItemList != null) {
                    for (RestServiceItem restServiceItem : serviceItemList) {
                        restServiceItem.setModule(ModuleUtil.findModuleForFile(psiClass.getContainingFile()));
                    }
                    itemList.addAll(serviceItemList);
                }
            }

        }

        return itemList;
    }

    protected List<RestServiceItem> getServiceItemList(PsiClass psiClass) {

        PsiMethod[] psiMethods = psiClass.getMethods();

        List<RestServiceItem> itemList = new ArrayList<>();
        List<RequestPath> classRequestPaths = getBasePaths(psiClass);

        for (PsiMethod psiMethod : psiMethods) {
            List<RequestPath> methodRequestPaths = getMethodPaths(psiMethod);
            if (CollectionUtils.isNotEmpty(classRequestPaths)) {
                for (RequestPath classRequestPath : classRequestPaths) {
                    for (RequestPath methodRequestPath : methodRequestPaths) {
                        String path =  classRequestPath.getPath();
                        RestServiceItem item = createRestServiceItem(psiMethod, path, methodRequestPath);
                        itemList.add(item);
                    }
                }
            } else {
                for (RequestPath methodRequestPath : methodRequestPaths) {
                    RestServiceItem item = createRestServiceItem(psiMethod, "", methodRequestPath);
                    itemList.add(item);
                }
            }

        }
        return itemList;
    }

    @Override
    public List<RequestPath> getBasePaths(PsiClass psiClass) {
        return RequestMappingAnnotationHelper.getRequestPaths(psiClass);
    }

    @Override
    public List<RequestPath> getMethodPaths(PsiMethod psiMethod) {
        return RequestMappingAnnotationHelper.getRequestPaths(psiMethod);
    }
}
