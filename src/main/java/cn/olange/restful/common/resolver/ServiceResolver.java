package cn.olange.restful.common.resolver;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;

import java.util.List;

public interface ServiceResolver {

    /* 获取module 中所有的服务列表 */
    List<RestServiceItem> findAllSupportedServiceItemsInModule(GlobalSearchScope searchScope);

    /* 获取project 中所有的服务列表 */
    List<RestServiceItem> findAllSupportedServiceItemsInProject(GlobalSearchScope searchScope);

    List<RequestPath> getBasePaths(PsiClass psiClass);

    List<RequestPath> getMethodPaths(PsiMethod psiMethod);

}
