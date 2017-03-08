package com.workspace.bin.wheelviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        CustomCitiesDialog dialog = new CustomCitiesDialog(this, new CustomCitiesDialog.ConfirmCustomDialogInterface() {
            @Override
            public void confirmResult(String sumMonth) {
                Toast.makeText(MainActivity.this,sumMonth,Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }
}
