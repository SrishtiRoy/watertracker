package com.shiva.hydra.OutlinesFragments;

import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.shiva.hydra.R;


public class OutlineActivity extends AppCompatActivity {
    private TextView pageMun;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_outline);



        pageMun = (TextView) findViewById(R.id.pageNum);
        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                pageMun.setText(""+((position + 1) + "/" + pagerAdapter.getCount()) );

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
 Fragment[] fragments = new Fragment[6];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments[0] = InfoFragment.create(R.string.wake_up_review, R.drawable.ic_bell);
            fragments[1] = InfoFragment.create( R.string.sleep_review, R.drawable.ic_bell);
            fragments[2] = InfoFragment.create( R.string.shower_review, R.drawable.ic_bell);
            fragments[3] = InfoFragment.create( R.string.meal_review, R.drawable.ic_bell);
            fragments[4] = InfoFragment.create( R.string.lose_weight, R.drawable.ic_bell);
            fragments[5] = InfoFragment.create( R.string.lack_water, R.drawable.ic_bell);





        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

