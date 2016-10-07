package alphaclo.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v7.widget.helper.ItemTouchHelper;

import alphaclo.fragment.LRFragment;
import alphaclo.fragment.QHFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static int totalPage;
    private Context _context;
    private LRFragment lrFragment;
    private QHFragment qhFragment;

    static {
        totalPage = 2;
    }

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this._context = context;
        if (this.lrFragment == null) {
            this.lrFragment = new LRFragment();
        }
        if (this.qhFragment == null)    {
            this.qhFragment = new QHFragment();
        }
    }

    public Fragment getItem(int position) {
        switch (position) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
                return this.lrFragment;
            default:
                return this.qhFragment;
        }
    }

    public int getCount() {
        return totalPage;
    }

    public void updateLRRatio(int leftPercent, int accLeftPercent) {
        lrFragment.updateRatio(leftPercent, accLeftPercent);
    }

    public void updateQHRatio(int quadsPercent, int accQuadsercent) {
        qhFragment.updateRatio(quadsPercent, accQuadsercent);
    }

    public void setMA(int ma) {
        this.lrFragment.setMA(ma);
    }
}
