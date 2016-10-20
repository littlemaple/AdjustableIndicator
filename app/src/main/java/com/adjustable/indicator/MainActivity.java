package com.adjustable.indicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.adjustable.library.IndicatorView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp);
        SeekBar sbScale = (SeekBar) findViewById(R.id.sb_scale);
        SeekBar sbAngle = (SeekBar) findViewById(R.id.sb_angle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        final IndicatorView indicator1 = (IndicatorView) findViewById(R.id.indicator_1);
        final IndicatorView indicator2 = (IndicatorView) findViewById(R.id.indicator_2);
        indicator1.setViewPager(viewPager);
        indicator2.setViewPager(viewPager);
        sbScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                indicator1.setScale(i / (float) seekBar.getMax());
                indicator2.setScale(i / (float) seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                indicator1.setAngle(i / (float) seekBar.getMax() * 180);
                indicator2.setAngle(i / (float) seekBar.getMax() * 180);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Fragment getItem(int position) {
            return DisplayFragment.instantiation("index:" + position);
        }

    }
}
