package com.zilche.zilche;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GridItemView extends ImageView {

    public GridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int width, int height) {
        super.onMeasure(width, width + 15);
    }

}

