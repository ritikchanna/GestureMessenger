package leotik.labs.gesturemessenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Service.OverlayService;

public class MainActivity extends Activity {
    Button btn_draw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_draw = findViewById(R.id.btn_draw);
        btn_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DrawActivity.class));
                finish();
            }
        });

    }
    private void initializeView() {

                startService(new Intent(MainActivity.this, OverlayService.class));
                finish();

    }


}
