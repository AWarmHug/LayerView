package com.warm.layerview;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by warm on 17/6/7.
 */

public class MyFragment extends Fragment {

    private TextView tv;
    private RecyclerView rv;
    private LayerView layerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) view.findViewById(R.id.tv);

        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setAdapter(new RvAdapter());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //推荐先弹一个弹框询问是否需要看。
        AlertDialog dialog=new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("我们增加了一些新功能，欢迎查看！")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layerView = new LayerView.Builder(getContext())
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
