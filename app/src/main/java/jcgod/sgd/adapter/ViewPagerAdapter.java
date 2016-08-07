package jcgod.sgd.adapter;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import jcgod.sgd.activity.MainActivity;
import jcgod.sgd.tab.LRRatioTab;
import jcgod.sgd.tab.MainTab;
import jcgod.sgd.tab.QHRatioTab;
import jcgod.sgd.tab.AllRatioTab;

/**
 * Created by Jaecheol on 16. 6. 17..
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    Handler handler;
    MainActivity.Sensor sensor;

    LRRatioTab LRTab;
    MainTab mainTab;
    QHRatioTab QHTab;
    AllRatioTab allRatioTab;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Handler handler, MainActivity.Sensor sensor) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.handler = handler;
        this.sensor = sensor;

        initAdatper();
    }

    /**
     * initAdatper
     */
    private void initAdatper()  {
        if( LRTab == null ) {
            LRTab = new LRRatioTab();
            LRTab.setHanlder(handler);
            LRTab.setSensor(sensor);
        }
        if( mainTab == null )  {
            mainTab = new MainTab();
            mainTab.setHandler(handler);
            mainTab.setSensor(sensor);
        }
        if( QHTab == null )  {
            QHTab = new QHRatioTab();
            QHTab.setHandler(handler);
            QHTab.setSensor(sensor);
        }
        if( allRatioTab == null )  {
            allRatioTab = new AllRatioTab();
            allRatioTab.setHandler(handler);
            allRatioTab.setSensor(sensor);
        }
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch( position )  {
            case 0:    // if the position is 0 we are returning the First tab
                return mainTab;
            case 1:    // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
                return LRTab;
            case 2:
                return QHTab;
            default:
                return allRatioTab;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public void receivePacket(String packet)    {
        mainTab.update(packet);
        LRTab.update();
        QHTab.update();
        allRatioTab.update();
    }
}