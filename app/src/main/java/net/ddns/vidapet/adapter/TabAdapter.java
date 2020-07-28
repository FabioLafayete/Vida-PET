package net.ddns.vidapet.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.ddns.vidapet.fragment.ContatosFragment;
import net.ddns.vidapet.fragment.ConversasFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] titulosAbas = {"Conversas", "Contatos"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titulosAbas.length;
    }


    //Recupera os titulos de cada uma das abas
    @Override
    public CharSequence getPageTitle(int position) {
        return titulosAbas[position];
    }
}
