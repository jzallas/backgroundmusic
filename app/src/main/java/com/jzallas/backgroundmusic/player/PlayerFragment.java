package com.jzallas.backgroundmusic.player;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jzallas.backgroundmusic.R;
import com.jzallas.backgroundmusic.TimeUtils;

import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment implements PlayerContract.View {

    private static final int MEDIA_DURATION_POLL_INTERVAL = 1000; // ms

    public static final String TAG = PlayerFragment.class.getName();

    @BindView(R.id.albumTextView)
    TextView albumTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.artistTextView)
    TextView artistTextView;
    @BindView(R.id.currentDurationTextView)
    TextView currentDurationTextView;
    @BindView(R.id.totalDurationTextView)
    TextView totalDurationTextView;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.albumArtImageView)
    ImageView albumArtImageView;

    @BindView(R.id.playControlImageButton)
    ImageButton playControlImageButton;
    @BindView(R.id.forwardControlImageButton)
    ImageButton forwardControlImageButton;
    @BindView(R.id.rewindControlImageButton)
    ImageButton rewindControlImageButton;

    private PlayerContract.Presenter presenter;

    private Timer timer;

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);

        // setup media controls
        playControlImageButton.setOnClickListener(v -> presenter.playMusic());
        forwardControlImageButton.setOnClickListener(v -> presenter.fastForward());
        rewindControlImageButton.setOnClickListener(v -> presenter.rewind());

        refreshLayout.setColorSchemeColors(
                getResources().getIntArray(R.array.color_scheme_swiperefresh)
        );

        refreshLayout.setOnRefreshListener(presenter::refresh);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // noop
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                endPolling();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.seekTo(seekBar.getProgress(), seekBar.getMax());
                beginPolling();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void setPresenter(PlayerContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void showArtist(String artist) {
        artistTextView.setText(artist);
    }

    @Override
    public void showAlbum(String album) {
        albumTextView.setText(album);
    }

    @Override
    public void showAlbumArt(byte[] albumArt) {
        Glide.with(getContext())
                .asBitmap()
                .load(albumArt)
                .into(albumArtImageView);
    }

    @Override
    public void showPlaying() {
        beginPolling();
        playControlImageButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void beginPolling() {
        if (timer != null) {
            timer.cancel();
        }

        timer = TimeUtils.startTimer(presenter::updateProgress, MEDIA_DURATION_POLL_INTERVAL);
    }

    private void endPolling() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void showPaused() {
        endPolling();
        playControlImageButton.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void showLoading(boolean isLoading) {
        refreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void showError(Throwable e) {
        if (getView() == null) {
            return;
        }

        Snackbar.make(getView(), R.string.media_load_failure, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_details, v -> showErrorDialog(e))
                .show();
    }

    private void showErrorDialog(Throwable e) {
        // Never show your stack trace to the user as it leaves your application vulnerable to
        // malicious attacks. If the user knows the details around why your application failed,
        // then the user might be able to exploit it and force your application to do things
        // it was never intended on doing. I'm only doing this here because I, as a developer,
        // want to know more about the failure details without actually checking logcat
        new AlertDialog.Builder(getActivity())
                .setMessage(Log.getStackTraceString(e))
                .setPositiveButton(R.string.dialog_button_retry, (dialogInterface, i) -> presenter.refresh())
                .setNeutralButton(R.string.dialog_button_okay, (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    @Override
    public void setControlsEnabled(boolean enabled) {
        playControlImageButton.setEnabled(enabled);
        forwardControlImageButton.setEnabled(enabled);
        rewindControlImageButton.setEnabled(enabled);
        seekBar.setEnabled(enabled);

        int durationVisibility = enabled ? View.VISIBLE : View.INVISIBLE;
        currentDurationTextView.setVisibility(durationVisibility);
        totalDurationTextView.setVisibility(durationVisibility);
    }

    @Override
    public void updateMediaProgress(int currentDuration, int totalDuration) {
        int progress = (currentDuration * seekBar.getMax()) / totalDuration;
        seekBar.setProgress(progress);

        currentDurationTextView.setText(TimeUtils.formatToMinuteSeconds(currentDuration));
        totalDurationTextView.setText(TimeUtils.formatToMinuteSeconds(totalDuration));
    }
}
