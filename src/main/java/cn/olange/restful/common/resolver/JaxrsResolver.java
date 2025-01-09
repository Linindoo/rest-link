package cn.olange.restful.common.resolver;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.annotations.JaxrsPathAnnotation;
import cn.olange.restful.common.jaxrs.JaxrsAnnotationHelper;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class JaxrsResolver extends BaseServiceResolver  {

    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {
        List<RestServiceItem> itemList = new ArrayList<>();
        Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(JaxrsPathAnnotation.PATH.getShortName(), globalSearchScope.getProject(), globalSearchScope);

        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
            PsiElement psiElement = psiModifierList.getParent();

            if (!(psiElement instanceof PsiClass psiClass)) {
                continue;
            }
            Module module = ModuleUtil.findModuleForFile(psiClass.getContainingFile());

            PsiMethod[] psiMethods = psiClass.getMethods();

            String classUriPath = JaxrsAnnotationHelper.getClassUriPath(psiClass);
            for (PsiMethod psiMethod : psiMethods) {
                List<RequestPath> methodUriPaths = getMethodPaths(psiMethod);
                for (RequestPath methodUriPath : methodUriPaths) {
                    RestServiceItem item = createRestServiceItem(psiMethod, classUriPath, methodUriPath);
                    item.setModule(module);
                    itemList.add(item);
                }
            }

        }
        return itemList;
    }

    @Override
    public List<RequestPath> getBasePaths(PsiClass psiClass) {
        String classUriPath = JaxrsAnnotationHelper.getClassUriPath(psiClass);
        return List.of(new RequestPath(classUriPath, ""));
    }

    @Override
    public List<RequestPath> getMethodPaths(PsiMethod psiMethod) {
        return JaxrsAnnotationHelper.getRequestPaths(psiMethod);
    }
}
