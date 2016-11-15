package adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import set2.linkup.R;

/**
 *  Name: SelectGridViewAdapter
 * Description: Adapter for GridView in ImgFolderActivity
 **/
public class SelectGridViewAdapter extends BaseAdapter {

    private int maxNum;
    private Context context;
    private List<String> mDatas;
    public static List<String> mSelectedImage;

    public SelectGridViewAdapter(Context context, List<String> mDatas, int maxNum) {
        this.mDatas = mDatas;
        this.context = context;
        this.maxNum = maxNum;
        mSelectedImage = new ArrayList<String>();
    }

    public List<String> getSelectImages() {
        return mSelectedImage;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    //If image is selected, there will be a check mark on it
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.gridview_item_select, null);

            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.id_item_image);
            holder.select = (ImageView) convertView
                    .findViewById(R.id.id_item_select);
        }

        final String path = mDatas.get(position);
        Picasso.with(context).load(new File(path)).centerCrop().resize(150, 150).into(holder.imageView);

        if (mSelectedImage.contains(path)) {
            holder.select.setVisibility(View.VISIBLE);
            holder.imageView.setColorFilter(R.color.colorScrims);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.imageView.setColorFilter(null);
        }

        convertView.setTag(holder);
        convertView.setOnClickListener(new View.OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                // image is selected
                if (mSelectedImage.contains(path)) {
                    mSelectedImage.remove(path);

                    holder.select.setVisibility(View.GONE);
                    holder.imageView.setColorFilter(null);
                } else
                // image isn't selected
                {
                    if (mSelectedImage.size() < maxNum) {
                        mSelectedImage.add(path);

                        holder.select.setVisibility(View.VISIBLE);
                        holder.imageView.setColorFilter(R.color.colorScrims);
                    } else {
                        Snackbar.make(holder.imageView, "You can only select" + maxNum + "image", Snackbar.LENGTH_SHORT);
                    }
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView, select;
    }
}
