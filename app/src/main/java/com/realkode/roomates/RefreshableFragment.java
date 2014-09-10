package com.realkode.roomates;

/**
 * Interface for the refreshFragmenth() method.
 * By using this, the fragment can be refreshed from the MainActivity without
 * knowing what kind of fragment it is, as long as the fragment implements this interface.
 *
 */
public interface RefreshableFragment {
    public void refreshFragment();
}