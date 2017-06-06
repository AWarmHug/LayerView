package com.warm.layerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity111";
    private TextView tv;
    private RecyclerView rv;
    private LayerView layerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) this.findViewById(R.id.tv);

        rv = (RecyclerView) this.findViewById(R.id.rv);
        rv.setAdapter(new RvAdapter());
        rv.setLayoutManager(new LinearLayoutManager(this));
        layerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layerView.dismiss();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            layerView = new LayerView.Builder(this)
                    .setLayerColor(Color.parseColor("#75000000"))
                    .setTextColor(Color.WHITE)
//                .setContent(rv.getLayoutManager().findViewByPosition(0))
                    .setContent(tv)
                    .build();
            layerView.initshow();
        }
    }


}
