package com.deathstudio.marcos.adoptaunperro;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class Chat_REC extends RecyclerView.ViewHolder{

    public TextView leftText,rightText;

    public Chat_REC(View itemView){
        super(itemView);
        leftText = itemView.findViewById(R.id.leftText);
        rightText = itemView.findViewById(R.id.rightText);
    }
}