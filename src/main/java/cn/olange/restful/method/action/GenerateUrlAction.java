package cn.olange.restful.method.action;

import cn.olange.restful.common.ServiceHelper;
import cn.olange.restful.navigation.action.RestServiceItem;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.*;
import cn.olange.restful.action.AbstractBaseAction;
import cn.olange.restful.annotations.JaxrsHttpMethodAnnotation;
import cn.olange.restful.annotations.SpringRequestMethodAnnotation;
import org.apache.commons.collections.CollectionUtils;

import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 */
public class GenerateUrlAction extends AbstractBaseAction {
    Editor myEditor;

    @Override
    public void actionPerformed(AnActionEvent e) {

        myEditor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        PsiMethod psiMethod = (PsiMethod) psiElement;
        if (psiMethod == null) {
            return;
        }

        List<RestServiceItem> serviceItems = ServiceHelper.getServiceItemByMethod(psiMethod);
        if (CollectionUtils.isEmpty(serviceItems)) {
            return;
        }
        CopyPasteManager.getInstance().setContents(new StringSelection(serviceItems.get(0).getUrl()));
        showPopupBalloon("复制成功", myEditor);
    }

    /**
     * spring rest 方法被选中才触发
     *
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

        boolean visible = false;
        if (psiElement instanceof PsiMethod psiMethod) {
            visible = isRestfulMethod(psiMethod);
        }

        setActionPresentationVisible(e, visible);
    }

    private boolean isRestfulMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(SpringRequestMethodAnnotation.values()).map(SpringRequestMethodAnnotation::getQualifiedName).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JaxrsHttpMethodAnnotation.values()).map(JaxrsHttpMethodAnnotation::getQualifiedName).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        return false;
    }

    @Override
    public  ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
