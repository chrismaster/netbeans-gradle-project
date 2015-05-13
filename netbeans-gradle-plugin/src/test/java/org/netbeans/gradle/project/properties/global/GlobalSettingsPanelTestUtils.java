package org.netbeans.gradle.project.properties.global;

import java.util.Map;
import javax.swing.SwingUtilities;
import junit.framework.Assert;
import org.netbeans.gradle.model.util.Exceptions;
import org.netbeans.gradle.project.util.NbConsumer;

public final class GlobalSettingsPanelTestUtils {
    public static void testInitAndReadBack(
            final Class<? extends GlobalSettingsEditor> panelClass,
            final NbConsumer<? super GlobalGradleSettings> initializer) throws Exception {

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GlobalSettingsEditor panel;
                try {
                    panel = panelClass.newInstance();
                } catch (Exception ex) {
                    throw Exceptions.throwUnchecked(ex);
                }

                GlobalGradleSettings.PreferenceContainer preference
                        = GlobalGradleSettings.setCleanMemoryPreference();
                try {
                    GlobalGradleSettings input = new GlobalGradleSettings("input");

                    initializer.accept(input);
                    Map<String, String> inputValues = preference.getKeyValues("input");

                    panel.updateSettings(input);

                    GlobalGradleSettings output = new GlobalGradleSettings("output");

                    panel.saveSettings(output);
                    Map<String, String> outputValues = preference.getKeyValues("output");

                    Assert.assertEquals(inputValues, outputValues);
                } finally {
                    GlobalGradleSettings.setDefaultPreference();
                }
            }
        });
    }

    private GlobalSettingsPanelTestUtils() {
        throw new AssertionError();
    }
}
