package com.bizhan.auarai.authorization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bizhan.auarai.API.auth.Register;
import com.bizhan.auarai.R;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText emailTxt = findViewById(R.id.emailText);
        EditText password = findViewById(R.id.passwordText);
        EditText passwordConf = findViewById(R.id.passwordConfText);
        TextView pswrdNotSame = findViewById(R.id.passwordSameText);
        Button register = findViewById(R.id.registerButton);
        Button login = findViewById(R.id.loginButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailStr = emailTxt.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                String passwordConfStr = passwordConf.getText().toString().trim();

                if (emailStr.isEmpty() || passwordStr.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Type in email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordStr.equals(passwordConfStr)){
                    pswrdNotSame.setText("Passwords are not the same");
                    return;
                }

                pswrdNotSame.setText("");
                Register.register(RegisterActivity.this, emailStr, passwordStr, new Register.RegisterCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "Error " + errorMessage, Toast.LENGTH_LONG).show()
                        );
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
