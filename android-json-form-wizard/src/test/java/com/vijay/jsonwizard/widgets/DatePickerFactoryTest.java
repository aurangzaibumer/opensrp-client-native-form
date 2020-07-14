package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatePickerFactoryTest extends BaseTest {
    private DatePickerFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout rootLayout;

    @Mock
    private MaterialEditText editText;

    @Mock
    private TextView duration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new DatePickerFactory();
    }

    @Test
    public void testDatePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        DatePickerFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Mockito.doReturn(editText).when(rootLayout).findViewById(R.id.edit_text);
        Mockito.doReturn(duration).when(rootLayout).findViewById(R.id.duration);
        Mockito.doReturn(duration).when(rootLayout).findViewById(R.id.duration);
        Mockito.doReturn(Locale.ENGLISH).when(factorySpy).getCurrentLocale(context);
        Mockito.doReturn("Age").when(duration).getTag(ArgumentMatchers.anyInt());
        Mockito.doReturn(Locale.ENGLISH).when(factorySpy).getSetLanguage(context);
        Mockito.doReturn("12 Age").when(factorySpy).getDurationText(context, "12-05-2010", Locale.ENGLISH);
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn("%1$dw").when(resources).getString(ArgumentMatchers.anyInt());

        String datePicker = "{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"date_picker\",\"hint\":\"Date first seen *\",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"value\":\"12-05-2020\",\"read_only\":true,\"label_info_text\":\"Just testing\",\"label_info_title\":\"Just testing\",\"duration\":{\"label\":\"AGE\"}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(datePicker), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        DatePickerFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("duration.label", editableProperties.iterator().next());
    }
}
