package com.moneydesktop.finance.handset.fragment;

import com.moneydesktop.finance.shared.fragment.GrowFragment;
import com.moneydesktop.finance.tablet.fragment.AccountSummaryTabletFragment;
import com.moneydesktop.finance.tablet.fragment.TransactionsSummaryTabletFragment;

public class DashboardFragmentFactory {

    public static GrowFragment getHandsetInstance(int position) {
        
        switch (position) {
            case 0:
                return AccountSummaryHandsetFragment.getInstance(position);
            case 1:
                return TransactionSummaryHandsetFragment.getInstance(position);
        }
        
        return null;
    }

    public static GrowFragment getTabletInstance(int position) {
        
        switch (position) {
        	case 0:
        		return AccountSummaryTabletFragment.newInstance(position);
	        case 1:
	            return TransactionsSummaryTabletFragment.newInstance(position);
        }
        
        return null;
    }
}