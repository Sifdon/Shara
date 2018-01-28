package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class PhoneEditText extends android.support.v7.widget.AppCompatEditText {

    private String phoneNumber = "375";

    public PhoneEditText(Context context) {
        super(context);
        initialize();
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {

        int length = getText().toString().replaceAll("[+()\\-_]", "").length();
        int selection;
        switch (length) {
            case 3:
            case 4:
                selection = length + 2;
                break;
            case 5:
            case 6:
            case 7:
                selection = length + 3;
                break;
            case 8:
            case 9:
                selection = length + 4;
                break;
            case 10:
            case 11:
                selection = length + 5;
                break;
            default:
                selection = 17;
        }

        setSelection(selection);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    private void initialize(){
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int position, int removeCount, int insertCount) {
            }

            @Override
            public void onTextChanged(CharSequence text, int position, int remove, int insert) {

                String stringNumber;
                String[] numbers = new String[12];

                if (remove != 0) {
                    if (phoneNumber.length() == 3) stringNumber = phoneNumber;
                    else
                        stringNumber = phoneNumber.substring(0, phoneNumber.length() - 1);
                } else {
                    stringNumber = text.toString().replaceAll("[+()\\-]", "");
                    stringNumber = stringNumber.substring(0, stringNumber.length() - 1);
                }

                if (stringNumber.length() < 12) {
                    int l = 12 - stringNumber.length();
                    for (int i = 0; i < l; i++) stringNumber += "_";
                }

                for (int i = 0; i < 12; i++)
                    numbers[i] = String.valueOf(stringNumber.charAt(i));

                removeTextChangedListener(this);
                setText(String.format("+%s%s%s(%s%s)%s%s%s-%s%s-%s%s", numbers));
                addTextChangedListener(this);

                phoneNumber = stringNumber.replaceAll("_", "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}