
package com.moneydesktop.finance.tablet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.moneydesktop.finance.BaseActivity;
import com.moneydesktop.finance.R;
import com.moneydesktop.finance.R.color;
import com.moneydesktop.finance.model.EventMessage.SyncEvent;
import com.moneydesktop.finance.tablet.fragment.IntroTabletFragment;
import com.moneydesktop.finance.util.Fonts;
import com.moneydesktop.finance.views.SmallSpinnerView;
import com.viewpagerindicator.CirclePageIndicator;

public class IntroTabletActivity extends BaseActivity {
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private TextView mLoadingMessage;
    private SmallSpinnerView mSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablet_intro_view);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        CirclePageIndicator mTitleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        mTitleIndicator.setViewPager(mPager);
        mLoadingMessage = (TextView) findViewById(R.id.loading_text);
        mLoadingMessage.setText(getResources().getText(R.string.loading_app));
        Fonts.applyPrimaryFont(mLoadingMessage, 24);
    }

    public void onEvent(SyncEvent event) {

        if (event.isFinished()) {
            TextView mStartButton = (TextView) findViewById(R.id.get_started);
            mSpinner = (SmallSpinnerView) findViewById(R.id.loading_spinner);
            mLoadingMessage.setVisibility(View.GONE);
            mSpinner.setVisibility(View.GONE);
            Fonts.applyPrimarySemiBoldFont(mStartButton, 18);
            mStartButton.setVisibility(View.VISIBLE);
            mStartButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(color.gray7);
                    Intent i = new Intent(IntroTabletActivity.this, DashboardTabletActivity.class);
                    startActivity(i);
                }
            });

        }
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IntroTabletFragment(R.drawable.intro1, R.string.intro_title_1,
                            R.string.intro_desc_1);
                case 1:
                    return new IntroTabletFragment(R.drawable.intro2, R.string.intro_title_2,
                            R.string.intro_desc_2);
                case 2:
                    return new IntroTabletFragment(R.drawable.intro3, R.string.intro_title_3,
                            R.string.intro_desc_3);
                case 3:
                    return new IntroTabletFragment(R.drawable.intro4, R.string.intro_title_4,
                            R.string.intro_desc_4);
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onPause() {
        this.finish();
        overridePendingTransition(R.anim.none, R.anim.out_down);
        super.onPause();
    }

    @Override
    protected void onStop() {
        overridePendingTransition(R.anim.none, R.anim.out_down);
        super.onPause();
    }

    @Override
    public String getActivityTitle() {
        // TODO Auto-generated method stub
        return null;
    }
}