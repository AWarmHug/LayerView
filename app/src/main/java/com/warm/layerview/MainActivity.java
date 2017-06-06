package com.warm.layerview;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

        //推荐先弹一个弹框询问是否需要看。
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("我们增加了一些新功能，欢迎查看！")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layerView = new LayerView.Builder(MainActivity.this)
                                .setLayerColor(Color.parseColor("#75000000"))
                                .setTextColor(Color.WHITE)
//                .setContent(rv.getLayoutManager().findViewByPosition(0))
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
                .setNegativeButton("不用",null)
                .create();
        dialog.show();


    }



}
