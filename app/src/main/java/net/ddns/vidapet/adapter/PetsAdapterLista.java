package net.ddns.vidapet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.ddns.vidapet.fragment.PetFragment;

/**
 * Created by Lafayete on 25/06/2017.
 */

public class PetsAdapterLista extends FragmentStatePagerAdapter {


    private TabAdapter tabAdapter;

    private String[] titulosAbas = {""};

    public PetsAdapterLista(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PetFragment();

        return fragment;
    }

    @Override
    public int getCount() {

        return 1;
    }



}
