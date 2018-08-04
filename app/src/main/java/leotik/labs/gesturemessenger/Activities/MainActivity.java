package leotik.labs.gesturemessenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.charbgr.BlurNavigationDrawer.v7.BlurActionBarDrawerToggle;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Service.OverlayService;

public class MainActivity extends Activity {
    private Button btn_draw;
    private DrawerLayout mdrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //make statusbar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        //set navigation view full width
        mNavigationView = findViewById(R.id.nav_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = metrics.widthPixels;
        mNavigationView.setLayoutParams(params);


        //Blur when drawer open
        mdrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new BlurActionBarDrawerToggle(
                this, mdrawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawer.addDrawerListener(toggle);
        toggle.syncState();


        btn_draw = findViewById(R.id.btn_draw);
        btn_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
                finish();
            }
        });

    }

    private void initializeView() {

        startService(new Intent(MainActivity.this, OverlayService.class));
        finish();

    }


}
