package com.vijay.jsonwizard.utils;

import android.preference.PreferenceManager;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.TestUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Locale;

import static com.vijay.jsonwizard.utils.Utils.getTranslatedYamlFile;
import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 20/02/2020
 */
public class NativeFormLangUtilsTest extends BaseTest {

    private final TestUtils testUtils = new TestUtils();

    @Test
    public void testJsonFormTranslationShouldTranslateForm() {
        Locale.setDefault(new Locale("id"));
        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));

        Locale.setDefault(new Locale("en", "US"));
        expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonFormTranslationShouldTranslateFormUsingLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application));
        allSharedPreferences.saveLanguagePreference(new Locale("id").toLanguageTag());
        String expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));

        allSharedPreferences.saveLanguagePreference(new Locale("en", "US").toLanguageTag());
        expectedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonFormTranslationShouldReturnUntranslatedForm() {
        Locale.setDefault(new Locale("id"));
        String interpolatedJsonForm = testUtils.getResourceFileContentsAsString("test_form_translation_interpolated_missing_translations");
        assertEquals(interpolatedJsonForm, NativeFormLangUtils.getTranslatedString(interpolatedJsonForm, RuntimeEnvironment.application));
    }

    @Test
    public void testJsonSubFormTranslationShouldTranslateJsonSubForm() throws Exception {
        Locale.setDefault(new Locale("id"));
        String expectedSubFormJson = testUtils.getResourceFileContentsAsString("test_form_translation_in");
        String interpolatedSubFormJson = FormUtils.loadSubForm("test_form_translation_interpolated", JsonFormConstants.DEFAULT_SUB_FORM_LOCATION , RuntimeEnvironment.application, true);
        assertEquals(expectedSubFormJson, interpolatedSubFormJson);

        Locale.setDefault(new Locale("en", "US"));
        interpolatedSubFormJson = FormUtils.loadSubForm("test_form_translation_interpolated", JsonFormConstants.DEFAULT_SUB_FORM_LOCATION , RuntimeEnvironment.application, true);
        expectedSubFormJson = testUtils.getResourceFileContentsAsString("test_form_translation_en_US");
        assertEquals(expectedSubFormJson, interpolatedSubFormJson);
    }

    @Test
    public void testYamlFileTranslationShouldTranslateYamlFile() {
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("test_yaml_translation_interpolated", RuntimeEnvironment.application);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_en_US"), translatedYamlStr);
    }

    @Test
    public void testYamlFileTranslationShouldReturnUntranslatedYamlFile() {
        Locale.setDefault(new Locale("en", "US"));
        String translatedYamlStr = getTranslatedYamlFile("test_yaml_translation_interpolated_missing_translations", RuntimeEnvironment.application);
        assertEquals(testUtils.getResourceFileContentsAsString("test_yaml_translation_interpolated_missing_translations"), translatedYamlStr);
    }
}
