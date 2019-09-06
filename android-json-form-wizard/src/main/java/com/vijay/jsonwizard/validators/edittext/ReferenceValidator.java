package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by samuelgithengi on 9/6/19.
 */
public class ReferenceValidator extends METValidator {

    private METValidator referenceValidator;
    private MaterialEditText referenceEditText;

    public ReferenceValidator(@NonNull String errorMessage, METValidator referenceValidator, MaterialEditText referenceEditText) {
        super(errorMessage);
        this.referenceValidator = referenceValidator;
        this.referenceEditText = referenceEditText;
    }


    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return referenceValidator.isValid(referenceEditText.getText(), isEmpty);
    }
}
