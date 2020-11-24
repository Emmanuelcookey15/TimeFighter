package ng.hotels.materialdesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputEditText username;
    TextInputEditText password;
    TextInputLayout userTextLayout;
    TextInputLayout passwordLayout;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username_edit_text);
        password = findViewById(R.id.password_edit_text);
        passwordLayout = findViewById(R.id.password_text_input);
        linearLayout = findViewById(R.id.ll_view);

        linearLayout.setOnClickListener(null);
        passwordLayout.setCounterEnabled(true);
        passwordLayout.setCounterMaxLength(8);

    }
}
