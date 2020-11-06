package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.happi.android.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFbLoginAlertDialog extends Dialog {

    private TypefacedTextViewRegular tv_negative;
    private TypefacedTextViewRegular tv_positive;
    private String positive;
    private String negative;
    private EditText et_email;
    private EmailFbLoginAlertDialog.OnOkClicked onOkClicked;
    private EmailFbLoginAlertDialog.OnCancelClicked onCancelClicked;


    public EmailFbLoginAlertDialog(@NonNull Activity activity, String positive, String negative,
                                   EmailFbLoginAlertDialog.OnOkClicked onOkClicked,
                                   EmailFbLoginAlertDialog.OnCancelClicked onCancelClicked) {
        super(activity);
        this.positive = positive;
        this.negative = negative;
        this.onOkClicked = onOkClicked;
        this.onCancelClicked = onCancelClicked;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_email_enter);

        et_email = findViewById(R.id.et_email);
        tv_negative = findViewById(R.id.tv_negative);
        tv_positive = findViewById(R.id.tv_positive);
        tv_negative.setText(negative);
        tv_positive.setText(positive);

        et_email.setError(null);

        tv_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_email.getText().toString().trim().isEmpty()) {

                    et_email.setError("Please enter your email Address");
                    et_email.requestFocus();
                } else if (!isValidEmail(et_email.getText().toString().trim())) {

                    et_email.setError("Invalid Email");
                    et_email.requestFocus();
                }else{
                    String email = et_email.getText().toString().trim().toLowerCase();
                    onOkClicked.onClickPositive(email);
                    dismiss();
                }

            }
        });
        tv_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked.onClickNegative();
                dismiss();
            }
        });
    }

    public interface OnOkClicked{
        public void onClickPositive(String email);

    }
    public interface OnCancelClicked{
        public void onClickNegative();

    }

    public static boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}

