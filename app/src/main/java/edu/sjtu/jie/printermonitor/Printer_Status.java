package edu.sjtu.jie.printermonitor;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Printer_Status extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer__status);
//        ImageButton imageButton=findViewById(R.id.image_iat_set);
//        ImageView imageView=findViewById(R.id.print_status_img);
////        Resources resources = getResources();
////        image.setImageDrawable(resources.getDrawable(R.drawable.myfirstimage));
//        Resources resources=getResources();
//        imageButton.setImageDrawable(resources.getDrawable(R.drawable.main_setting_btn_np));
//        imageView.setImageDrawable(resources.getDrawable(R.drawable.printer_example));
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
