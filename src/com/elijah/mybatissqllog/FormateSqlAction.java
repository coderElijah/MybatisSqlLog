package com.elijah.mybatissqllog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import org.apache.commons.lang.StringUtils;

import javax.xml.validation.Validator;
import java.awt.*;

/**
 * Description: idea plugin to format sql by click right button
 * 
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:MybatisSqlLog
 * PackageName: com.elijah.mybatissqllog
 * Date: 2020/5/25 12:30
 */
public class FormateSqlAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor mEditor = e.getData(PlatformDataKeys.EDITOR);
        if (null == mEditor) {
            return;
        }
        SelectionModel model = mEditor.getSelectionModel();
        final String selectedText = model.getSelectedText();
        if (StringUtils.isEmpty(selectedText)) {
            return;
        }
        String sqlModel = selectedText.substring(0, selectedText.indexOf("\n"));
        String sqlParams = selectedText.substring(selectedText.indexOf("\n") + 1);
        sqlModel = sqlModel.substring(sqlModel.indexOf("Preparing:") + 10).trim();
        sqlParams = sqlParams.substring(sqlParams.indexOf("Parameters:") + 11).trim();
        String[] params = sqlParams.split("\\(*\\)");
        for (String param : params) {
            param = param.substring(0, param.indexOf('('));
            if (param.startsWith(",")) {
                param = param.substring(1);
            } else if (param.endsWith(",")) {
                param = param.substring(0, param.length() - 1);
            } else {

            }
            sqlModel = sqlModel.replaceFirst("\\?", '\'' + param.trim() + '\'');
        }
        this.showPopupBalloon(mEditor, sqlModel);
    }

    private void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory factory = JBPopupFactory.getInstance();
            factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                    .setTitle("format mybaits sql")
                    .setCloseButtonEnabled(true)
                    .setHideOnCloseClick(true)
                    .setDialogMode(true)
                    .setFadeoutTime(100 * 1000)
                    .setHideOnAction(false)
                    .setHideOnClickOutside(true)
                    .setHideOnKeyOutside(false)
                    .createBalloon()
                    .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);

        });
    }
}
