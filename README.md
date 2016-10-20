# 可调节角度的viewpager 指示条
===
# 效果图
<img src="https://github.com/littlemaple/AdjustableIndicator/blob/master/screenshot/m1.gif?raw=true" width=320px height=640px/>
<img src="https://github.com/littlemaple/AdjustableIndicator/blob/master/screenshot/m2.gif?raw=true" width=320px height=640px/>

使用
===

* 添加依赖

 * 通过gradle导入依赖
  * 添加maven仓库地址
       ```
          maven{
                url "https://dl.bintray.com/lin/maven"
             }
        ```
        
  * 添加依赖
         ```
           compile 'com.adjustable.indicator:library:v1.0.1'
         ```
  * 在git上clone后导入library
   

* xml中的配置

  ```
        <com.adjustable.library.IndicatorView
            android:id="@+id/indicator_1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginLeft="10dp"
            android:layout_marginRight="10dp"
            android:layout_marginTop="10dp"
            app:angle="40"
            app:each_padding="30dp"
            app:filled_color="#faaab9"
            app:line_width="2dp"
            app:mode="select"
            app:scale="0.6"
            app:size="20dp"
            app:stroke_color="#fff" />
  ```
  
* java 代码
  ```
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
   ```
   
* 相关的参数解释
      * app:angle="40" 进度条的角度，0-180
      * app:each_padding="30dp" 每个圆圈的间隔
      * app:filled_color="#faaab9" 外边框的颜色（选中的颜色）
      * app:line_width="2dp" 外边框的大小
      * app:mode="select" 指示条的样式，select只区分当前选中，pregress以进度的形式展示
      * app:scale="0.6" 滑动中的曲线度，范围为0-1
      * app:size="20dp" 每个圆圈的大小
      * app:stroke_color="#fff" 内部空心的颜色

