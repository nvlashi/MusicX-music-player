package com.rks.musicx.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.appthemeengine.Config;
import com.rks.musicx.R;
import com.rks.musicx.data.loaders.ArtistLoader;
import com.rks.musicx.data.loaders.SortOrder;
import com.rks.musicx.data.model.Artist;
import com.rks.musicx.misc.utils.ATEUtils;
import com.rks.musicx.misc.utils.CustomLayoutManager;
import com.rks.musicx.misc.utils.DividerItemDecoration;
import com.rks.musicx.misc.utils.Extras;
import com.rks.musicx.misc.utils.Helper;
import com.rks.musicx.misc.utils.ItemOffsetDecoration;
import com.rks.musicx.ui.adapters.ArtistListAdapter;
import com.rks.musicx.ui.adapters.BaseRecyclerViewAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Coolalien on 6/28/2016.
 */

/*
 * ©2017 Rajneesh Singh
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ArtistListFragment extends miniFragment implements LoaderManager.LoaderCallbacks<List<Artist>>, SearchView.OnQueryTextListener {

    private FastScrollRecyclerView rv;
    private ArtistListAdapter artistListAdapter;
    private int artistLoader = -1;
    private List<Artist> artistlist;
    private SearchView searchView;

    private BaseRecyclerViewAdapter.OnItemClickListener OnClick = new BaseRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            switch (view.getId()) {
                case R.id.album_artwork:
                case R.id.item_view:
                    Fragment fragments = ArtistFragment.newInstance(artistListAdapter.getItem(position));
                    ImageView listartwork = (ImageView) view.findViewById(R.id.album_artwork);
                    fragTransition(fragments, listartwork, "TransitionArtwork");
                    rv.smoothScrollToPosition(position);
                    break;
            }
        }
    };

    public static ArtistListFragment newInstance() {
        return new ArtistListFragment();
    }

    private void fragTransition(Fragment fragment, ImageView imageView, String transition) {
        ViewCompat.setTransitionName(imageView,transition);
        Helper.setFragmentTransition(getActivity(), ArtistListFragment.this, fragment,  new Pair<View, String>(imageView, transition));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.common_rv, container, false);
        rv = (FastScrollRecyclerView) rootView.findViewById(R.id.commonrv);
        artistListAdapter = new ArtistListAdapter(getContext());
        int colorAccent = Config.accentColor(getContext(), Helper.getATEKey(getContext()));
        rv.setPopupBgColor(colorAccent);
        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);
        loadArtist();
        artistView();
        artistlist = new ArrayList<>();
        rv.setAdapter(artistListAdapter);
        artistListAdapter.setOnItemClickListener(OnClick);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_view_menu, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.artist_search));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search artist");
        if (Extras.getInstance().artistView()) {
            menu.findItem(R.id.grid_view).setVisible(false);
        } else {
            menu.findItem(R.id.grid_view).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                load();
                break;
            case R.id.menu_sort_by_za:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                load();
                break;
            case R.id.menu_sort_by_number_of_songs:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                load();
                break;
            case R.id.menu_sort_by_number_of_albums:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                load();
                break;
            case R.id.bytwo:
                Extras.getInstance().setArtistGrid(2);
                loadGridView();
                load();
                break;
            case R.id.bythree:
                Extras.getInstance().setArtistGrid(3);
                loadGridView();
                load();
                break;
            case R.id.byfour:
                Extras.getInstance().setArtistGrid(4);
                loadGridView();
                load();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String ateKey = Helper.getATEKey(getContext());
        ATEUtils.setStatusBarColor(getActivity(), ateKey, Config.primaryColor(getActivity(), ateKey));
    }

    @Override
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        ArtistLoader artistsLoader = new ArtistLoader(getActivity());
        if (id == artistLoader) {
            artistsLoader.setSortOrder(Extras.getInstance().getArtistSortOrder());
            return artistsLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
        if (data == null) {
            return;
        }
        artistlist = data;
        artistListAdapter.addDataList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {
        artistListAdapter.notifyDataSetChanged();
    }

    private void loadArtist() {
        getLoaderManager().initLoader(artistLoader, null, this);
    }

    @Override
    public void load() {
        getLoaderManager().restartLoader(artistLoader, null, this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Artist> filterlist = Helper.filterArtist(artistlist, newText);
        artistListAdapter.setFilter(filterlist);
        return true;
    }

    private void loadGridView() {
        if (Extras.getInstance().getArtistGrid() == 2) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            rv.setLayoutManager(layoutManager);
        } else if (Extras.getInstance().getArtistGrid() == 3) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
            rv.setLayoutManager(layoutManager);
        } else if (Extras.getInstance().getArtistGrid() == 4) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
            rv.setLayoutManager(layoutManager);
        }
    }

    private void artistView() {
        if (Extras.getInstance().artistView()) {
            artistListAdapter.setLayoutID(R.layout.item_list_view);
            CustomLayoutManager custom = new CustomLayoutManager(getContext());
            custom.setSmoothScrollbarEnabled(true);
            rv.setLayoutManager(custom);
            rv.addItemDecoration(new DividerItemDecoration(getContext(), 75, false));
        } else {
            artistListAdapter.setLayoutID(R.layout.item_grid_view);
            rv.addItemDecoration(new ItemOffsetDecoration(2));
            loadGridView();
        }
    }
}
