package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.DateUtil;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeFormLangUtils;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

public class NativeDatePickerFactory implements FormWidgetFactory {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String DATE_FORMAT_REGEX = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)|\\s*";
    public static final SimpleDateFormat DATE_FORMAT_LOCALE_INDEPENDENT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static final String TAG = "DatePickerFactory";
    private FormUtils formUtils = new FormUtils();

    private static void showDatePickerDialog(Activity context, DatePickerDialog datePickerDialog, MaterialEditText editText, String defaultDate) {

            datePickerDialog.show();

            String text = editText.getText().toString();
            Calendar date = FormUtils.getDate(StringUtils.isNoneBlank(Form.getDatePickerDisplayFormat()) ?
                    Utils.formatDateToPattern(text, Form.getDatePickerDisplayFormat(), DATE_FORMAT.toPattern())
                    : text);
            if (text.isEmpty()) {
                if (!defaultDate.trim().isEmpty()) {
                    Calendar cal = FormUtils.getDate(defaultDate);
                    datePickerDialog.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    datePickerDialog.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                }
            } else {
                datePickerDialog.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            }
//        }
    }

    private void updateDateText(Context context, MaterialEditText editText, TextView duration, String date) {
        editText.setText(StringUtils.isNoneBlank(Form.getDatePickerDisplayFormat()) ?
                Utils.formatDateToPattern(date, DATE_FORMAT.toPattern(), Form.getDatePickerDisplayFormat())
                : date);
        String durationLabel = (String) duration.getTag(R.id.label);
        if (StringUtils.isNotBlank(durationLabel)) {
            Locale locale = getSetLanguage(context);
            String durationText = getDurationText(context, date, locale);
            if (StringUtils.isNotBlank(durationText)) {
                durationText = String.format("(%s: %s)", durationLabel, durationText);
            }
            duration.setText(durationText);
        }
    }

    @NotNull
    @VisibleForTesting
    protected String getDurationText(Context context, String date, Locale locale) {
        return DateUtil.getDuration(DateUtil.getDurationTimeDifference(date, null), locale.getLanguage().equals("ar") ? Locale.ENGLISH : locale, context);
    }

    @NotNull
    @VisibleForTesting
    protected Locale getSetLanguage(Context context) {
        return new Locale(NativeFormLangUtils.getLanguage(context));
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject,
                                       CommonListener listener, boolean popup) {
        return attachJson(stepName, context, formFragment, jsonObject, popup, listener);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, false, listener);
    }

    @Override
    @NonNull
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(NativeDatePickerFactory.KEY.DURATION + "." + JsonFormConstants.LABEL);
        return customTranslatableWidgetFields;
    }

    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                    boolean popup, CommonListener listener) {
        List<View> views = new ArrayList<>(1);
        try {

            RelativeLayout dateViewRelativeLayout = getRelativeLayout(context);
            MaterialEditText editText = dateViewRelativeLayout.findViewById(R.id.edit_text);
            TextView duration = dateViewRelativeLayout.findViewById(R.id.duration);

            attachLayout(stepName, context, formFragment, jsonObject, editText, duration);

            JSONArray canvasIds = new JSONArray();
            dateViewRelativeLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(dateViewRelativeLayout.getId());
            editText.setTag(R.id.canvas_ids, canvasIds.toString());
            editText.setTag(R.id.extraPopup, popup);

            ((JsonApi) context).addFormDataView(editText);
            views.add(dateViewRelativeLayout);
            attachInfoIcon(stepName, jsonObject, dateViewRelativeLayout, canvasIds, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    @VisibleForTesting
    protected RelativeLayout getRelativeLayout(Context context) {
        return (RelativeLayout) LayoutInflater.from(context).inflate(getLayout(), null);
    }

    protected void attachLayout(String stepName, final Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                final MaterialEditText editText, final TextView duration) {

        try {
            String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
            String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
            String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
            String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
            String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
            String calculations = jsonObject.optString(JsonFormConstants.CALCULATION);

            duration.setTag(R.id.key, jsonObject.getString(KEY.KEY));
            duration.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            duration.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            duration.setTag(R.id.openmrs_entity, openMrsEntity);
            duration.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            if (jsonObject.has(NativeDatePickerFactory.KEY.DURATION) && jsonObject.getJSONObject(NativeDatePickerFactory.KEY.DURATION) != null) {
                duration.setTag(R.id.label, jsonObject.getJSONObject(NativeDatePickerFactory.KEY.DURATION).getString(JsonFormConstants.LABEL));
            }

            updateEditText(editText, jsonObject, stepName, context, duration);
            editText.setTag(R.id.json_object, jsonObject);

            final DatePickerDialog datePickerDialog = createDateDialog(context, duration, editText, jsonObject);

            GenericTextWatcher genericTextWatcher = getGenericTextWatcher(stepName, (Activity) context, formFragment,
                    editText, datePickerDialog);
            editText.addTextChangedListener(genericTextWatcher);
            addRefreshLogicView(context, editText, relevance, constraints, calculations);

            editText.setOnClickListener(v -> showDatePickerDialog((Activity) context, datePickerDialog, editText, jsonObject.optString(JsonFormConstants.DEFAULT)));

            editText.setOnLongClickListener(v -> {
                updateDateText(context, editText, duration, "");
                return true;
            });
            editText.setFocusable(false);
        } catch (Exception e) {
            Timber.e(e.getMessage(), TAG);
        }

    }

    private void attachInfoIcon(String stepName, JSONObject jsonObject, RelativeLayout rootLayout, JSONArray canvasIds,
                                CommonListener listener) throws JSONException {
        if (jsonObject.has(JsonFormConstants.LABEL_INFO_TEXT)) {
            ImageView infoIcon = rootLayout.findViewById(R.id.date_picker_info_icon);
            formUtils.showInfoIcon(stepName, jsonObject, listener, FormUtils.getInfoDialogAttributes(jsonObject), infoIcon, canvasIds);
        }

    }

    @NonNull
    private GenericTextWatcher getGenericTextWatcher(String stepName, final Activity context, JsonFormFragment formFragment,
                                                     final MaterialEditText editText,
                                                     final DatePickerDialog datePickerDialog) {
        GenericTextWatcher genericTextWatcher = new GenericTextWatcher(stepName, formFragment, editText);
        genericTextWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    datePickerDialog.setArguments(new Bundle());
                    showDatePickerDialog(context, datePickerDialog, editText, "");
                }
            }
        });
        return genericTextWatcher;
    }

    private void addRefreshLogicView(Context context, MaterialEditText editText, String relevance, String constraints,
                                     String calculations) {
        if (StringUtils.isNotBlank(relevance) && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }

        if (StringUtils.isNotBlank(constraints) && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }

        if (StringUtils.isNotBlank(calculations) && context instanceof JsonApi) {
            editText.setTag(R.id.calculation, calculations);
            ((JsonApi) context).addCalculationLogicView(editText);
        }
    }

    private void updateEditText(MaterialEditText editText, JSONObject jsonObject, String stepName, Context context, TextView duration) throws JSONException {

        Locale locale = getCurrentLocale(context);
        final SimpleDateFormat DATE_FORMAT_LOCALE = new SimpleDateFormat("dd-MM-yyyy", locale);

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        editText.setHint(jsonObject.getString(KEY.HINT));
        editText.setFloatingLabelText(jsonObject.getString(KEY.HINT));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(KEY.KEY));
        editText.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(KEY.KEY));
        editText.setTag(R.id.locale_independent_value, jsonObject.optString(KEY.VALUE));

        if (jsonObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            boolean requiredValue = requiredObject.getBoolean(KEY.VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }
        }

        if (StringUtils.isNotBlank(jsonObject.optString(KEY.VALUE))) {
            updateDateText(context, editText, duration, DATE_FORMAT_LOCALE.format(FormUtils.getDate(jsonObject.optString(KEY.VALUE)).getTime()));
        }

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            editText.setEnabled(!readOnly);
            editText.setFocusable(!readOnly);
        }
    }


    @VisibleForTesting
    protected Locale getCurrentLocale(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().equals("ar") ? Locale.ENGLISH : context.getResources().getConfiguration().locale;//Arabic should render normal numbers/numeric digits
    }

    protected DatePickerDialog createDateDialog(final Context context, final TextView duration, final MaterialEditText editText,
                                                JSONObject jsonObject) throws JSONException {

        Calendar currentCalendar = Calendar.getInstance();
        int startYear = currentCalendar.get(Calendar.YEAR);
        int startMonth = currentCalendar.get(Calendar.MONTH);
        int startDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.AppThemeAlertDialog, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {

            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendarDate.set(Calendar.MONTH, month);
            calendarDate.set(Calendar.YEAR, year);

            editText.setTag(R.id.locale_independent_value, DATE_FORMAT_LOCALE_INDEPENDENT.format(calendarDate.getTime()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    && calendarDate.getTimeInMillis() >= view.getMinDate()
                    && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                updateDateText(context, editText, duration,
                        DATE_FORMAT.format(calendarDate.getTime()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                updateDateText(context, editText, duration, "");
            }

        }, startYear,startMonth,startDay);

        if (jsonObject.has(JsonFormConstants.MIN_DATE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Calendar minDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MIN_DATE));
            minDate.set(Calendar.HOUR_OF_DAY, 0);
            minDate.set(Calendar.MINUTE, 0);
            minDate.set(Calendar.SECOND, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        if (jsonObject.has(JsonFormConstants.MAX_DATE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Calendar maxDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MAX_DATE));
            maxDate.set(Calendar.HOUR_OF_DAY, 23);
            maxDate.set(Calendar.MINUTE, 59);
            maxDate.set(Calendar.SECOND, 59);
            maxDate.set(Calendar.MILLISECOND, 999);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        }

        return datePickerDialog;
    }


    protected int getLayout() {
        return R.layout.native_form_item_date_picker;
    }

    public static class KEY {
        public static final String DURATION = "duration";
        public static final String HINT = "hint";
        public static final String KEY = "key";
        public static final String VALUE = (JsonFormConstants.VALUE);
        public static final String DEFAULT = "default";
    }
}