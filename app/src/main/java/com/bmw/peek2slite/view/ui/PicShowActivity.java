package com.bmw.peek2slite.view.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.utils.PullXmlParseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PicShowActivity extends BaseActivity {

    @Bind(R.id.photoview)
    PhotoView photoview;
    @Bind(R.id.pic_show_title)
    TextView mTitle;
    @Bind(R.id.pic_show_guandaoId)
    TextView mGuandaoId;
    @Bind(R.id.pic_show_style)
    TextView mStyle;
    @Bind(R.id.pic_show_name)
    TextView mName;
    @Bind(R.id.pic_show_grade)
    TextView mGrade;
    @Bind(R.id.pic_show_distance)
    TextView mDistance;
    @Bind(R.id.pic_show_clock)
    TextView mClock;
    @Bind(R.id.pic_show_length)
    TextView mLength;
    @Bind(R.id.pic_show_detail)
    TextView mDetail;
    @Bind(R.id.pic_show_bottom_container)
    LinearLayout container;
    @Bind(R.id.pic_show_lastPage)
    ImageView mLastPage;
    @Bind(R.id.pic_show_nextPage)
    ImageView mNextPage;
    private Bitmap bitmap;
    private List<File> files;
    private int position;
    private boolean isShowList;
    private boolean isShowMore;

    private GestureDetector gesture;
    private boolean mIsFileExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_show);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            String bitmapPath = intent.getStringExtra("bitmap");
            files = (List<File>) intent.getSerializableExtra("list");
            position = intent.getIntExtra("position", 0);
//            bitmap = getBitmap(new File(bitmapPath));
            setImage();
            getDataFromXml();

        }

        photoview.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                showMore();
            }
        });


    }

    private void getDataFromXml() {
        String xmlPath = files.get(position).getAbsolutePath();
        xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf(".")) + ".xml";
        File xmlFile = new File(xmlPath);
        if (xmlFile.exists()) {
            Map<String, String> map = PullXmlParseUtil.parsePicXml(xmlFile);
            mClock.setText(map.get(PullXmlParseUtil.ClockExpression));
            mDetail.setText(map.get(PullXmlParseUtil.DefectDescription));
            mDistance.setText(map.get(PullXmlParseUtil.Distance));
            mGrade.setText(map.get(PullXmlParseUtil.DefectLevel));
            mGuandaoId.setText(map.get(PullXmlParseUtil.PipeSection));
            mLength.setText(map.get(PullXmlParseUtil.DefectLength));
            mName.setText(map.get(PullXmlParseUtil.DefectCode));
            mStyle.setText(map.get(PullXmlParseUtil.DefectType));
            mIsFileExit = true;
            if (isShowMore)
                container.setVisibility(View.VISIBLE);
        } else {
            mClock.setText("");
            mDetail.setText("");
            mDistance.setText("");
            mGrade.setText("");
            mGuandaoId.setText("");
            mLength.setText("");
            mName.setText("");
            mStyle.setText("");
            mIsFileExit = false;
            container.setVisibility(View.GONE);
        }
    }

    private void setImage() {
        bitmap = getBitmap(files.get(position));

        if (bitmap != null) {
            photoview.setImageBitmap(bitmap);
            String fName = files.get(position).getName();
            fName = fName.substring(fName.lastIndexOf("/") + 1, fName.length());
            mTitle.setText(fName);
        } else {
            Toast.makeText(this, "文件损坏！", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    public Bitmap getBitmap(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null)
            bitmap.recycle();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            Log.d("wait", "onKeyDown: ");
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.pic_show_lastPage, R.id.pic_show_nextPage, R.id.photoview, R.id.picture_show_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pic_show_lastPage:
                lastPage();
                break;
            case R.id.pic_show_nextPage:
                nextPage();
                break;
            case R.id.photoview:
                break;
            case R.id.picture_show_more:
                showMore();
                break;

        }
    }

    private void lastPage() {
        if (position == 0) {
            position = files.size() - 1;
        } else {
            position -= 1;
        }

        setImage();
        getDataFromXml();
    }

    private void nextPage() {
        if (position == files.size() - 1) {
            position = 0;
        } else {
            position += 1;
        }
        setImage();
        getDataFromXml();
    }

    private void showMore() {

        if (!isShowMore) {
            mTitle.setVisibility(View.VISIBLE);
            if (mIsFileExit)
                container.setVisibility(View.VISIBLE);
            mLastPage.setVisibility(View.VISIBLE);
            mNextPage.setVisibility(View.VISIBLE);
            isShowMore = true;
        } else {
            mTitle.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
            mLastPage.setVisibility(View.GONE);
            mNextPage.setVisibility(View.GONE);
            isShowMore = false;
        }
    }


}
