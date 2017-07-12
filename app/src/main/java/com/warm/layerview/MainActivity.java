package com.warm.layerview;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFragment myFragment = new MyFragment();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, 0, 0, android.R.anim.slide_out_right)
                        .add(R.id.activity_main, myFragment)
                        .show(myFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        rv = (RecyclerView) this.findViewById(R.id.rv);
        rv.setAdapter(new RvAdapter());
        rv.setLayoutManager(new LinearLayoutManager(this));


        rv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
            }
        });


        //推荐先弹一个弹框询问是否需要看。
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("我们增加了一些新功能，欢迎查看！")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        layerView = new LayerView.Builder(MainActivity.this)
                                .setLayerColor(Color.parseColor("#75000000"))
                                .setTextColor(Color.WHITE)
//                                .setContent(rv.getLayoutManager().findViewByPosition(0))
                                .setContent(tv)
                                .build();
                        layerView.initshow();
                        layerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layerView.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("不用", null)
                .create();
        dialog.show();
    }
//    private boolean show;
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus&&!show){
//            show=true;
//            layerView = new LayerView.Builder(MainActivity.this)
//                    .setLayerColor(Color.parseColor("#75000000"))
//                    .setTextColor(Color.WHITE)
////                .setContent(rv.getLayoutManager().findViewByPosition(0))
//                    .setContent(tv)
//                    .build();
//            layerView.initshow();
//            layerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    layerView.dismiss();
//                }
//            });
//        }
//    }
}
